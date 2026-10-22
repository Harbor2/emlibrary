package com.wyz.emlibrary.util

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

/*
 * **************************** ComponentActivity 中使用 ****************************
 *
 *   private val viewModel by activitySharedViewModel<XXXViewModel>({ XXXViewModel::class.java.simpleName })
 *   private val viewModel by activitySharedViewModel<XXXViewModel>("XXXViewModel")
 *
 * *********************************************************************************
 */
inline fun <reified VM : ViewModel> ComponentActivity.activitySharedViewModel(
    noinline scopeName: () -> String,
    noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> {
    val factory = factoryProducer ?: {
        defaultViewModelProviderFactory
    }

    return ActivityBusViewModelLazy(
        owner = this,
        scopeName = scopeName,
        viewModelClass = VM::class.java,
        factoryProducer = factory
    )
}

inline fun <reified VM : ViewModel> ComponentActivity.activitySharedViewModel(
    scopeName: String,
    noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> = activitySharedViewModel(
    scopeName = { scopeName },
    factoryProducer = factoryProducer
)

/*
 * ******************* activity、 fragment、view等LifecycleOwner体系中 ***************
 *
 * private val viewModel by ownerSharedViewModel<XXXViewModel>({ XXXViewModel::class.java.simpleName })
 * private val viewModel by ownerSharedViewModel<XXXViewModel>("XXXViewModel")
 *
 * *********************************************************************************
 */
inline fun <reified VM : ViewModel> LifecycleOwner.ownerSharedViewModel(
    noinline scopeName: () -> String,
    noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> {
    val factory = factoryProducer ?: {
        if (this is HasDefaultViewModelProviderFactory) {
            this.defaultViewModelProviderFactory
        } else {
            ViewModelProvider.NewInstanceFactory()
        }
    }

    return ActivityBusViewModelLazy(
        owner = this,
        scopeName = scopeName,
        viewModelClass = VM::class.java,
        factoryProducer = factory
    )
}

inline fun <reified VM : ViewModel> LifecycleOwner.ownerSharedViewModel(
    scopeName: String,
    noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> = ownerSharedViewModel(scopeName = { scopeName }, factoryProducer = factoryProducer)

/*
 * **************************** compose组件中使用 ****************************
 *
 * private val viewModel by composeSharedViewModel<XXXViewModel>({ XXXViewModel::class.java.simpleName })
 * private val viewModel by composeSharedViewModel<XXXViewModel>("XXXViewModel")
 *
 * *********************************************************************************
 */
@Composable
inline fun <reified VM : ViewModel> composeSharedViewModel(
    noinline scopeName: () -> String,
    noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> {
    val owner: LifecycleOwner = (LocalActivity.current as? LifecycleOwner) ?: LocalLifecycleOwner.current
    val context = LocalContext.current

    val factoryProducer: () -> Factory = factoryProducer ?: {
        if (owner is HasDefaultViewModelProviderFactory) {
            owner.defaultViewModelProviderFactory
        } else {
            (context.applicationContext as? Application)?.let {
                ViewModelProvider.AndroidViewModelFactory.getInstance(it)
            } ?: ViewModelProvider.NewInstanceFactory()
        }
    }

    return ActivityBusViewModelLazy(
        owner = owner,
        scopeName = scopeName,
        viewModelClass = VM::class.java,
        factoryProducer = factoryProducer
    )
}

@Composable
inline fun <reified VM : ViewModel> composeSharedViewModel(
    scopeName: String,
    noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> = composeSharedViewModel(scopeName = { scopeName }, factoryProducer = factoryProducer)

/**
 * ViewModel Lazy
 *
 * 根据 scopeName 获取共享的 ActivityBusViewModelStore，
 * 从而实现多个 Activity / LifecycleOwner 之间共享 ViewModel。
 */
class ActivityBusViewModelLazy<T : ViewModel>(
    private val owner: LifecycleOwner,
    private val scopeName: () -> String,
    private val viewModelClass: Class<T>,
    private val factoryProducer: () -> Factory
) : Lazy<T> {

    @Volatile
    private var viewModel: T? = null

    override val value: T
        get() {
            viewModel?.let {
                return it
            }

            synchronized(this) {
                viewModel?.let {
                    return it
                }

                val vmStore = commonViewStore(
                    scopeName = scopeName(),
                    owner = owner
                )

                val vm = ViewModelProvider(
                    vmStore,
                    factoryProducer()
                )[viewModelClass]

                viewModel = vm

                return vm
            }
        }

    override fun isInitialized(): Boolean {
        return viewModel != null
    }
}

/**
 * ViewModel Scope 注解。
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class VMScope(
    val scopeName: String
)


/**
 * 全局 ViewModelStore 缓存。
 *
 * 一个 scopeName 对应一个 ActivityBusViewModelStore。
 */
private val cache =
    ConcurrentHashMap<String, ActivityBusViewModelStore>()


/**
 * Activity 注入 ViewModel。
 *
 * 示例：
 *
 * @VMScope("wallpaper")
 * lateinit var wallpaperViewModel: WallpaperViewModel
 *
 * 然后：
 *
 * injectViewModel()
 */
fun ComponentActivity.injectViewModel() {

    this::class.java.declaredFields.forEach { field ->

        val annotation = field.getAnnotation(VMScope::class.java)
            ?: return@forEach

        // 确保字段是 ViewModel
        if (!ViewModel::class.java.isAssignableFrom(field.type)) {
            return@forEach
        }

        @Suppress("UNCHECKED_CAST")
        val viewModelClass =
            field.type as Class<out ViewModel>

        val vmStore = commonViewStore(
            scopeName = annotation.scopeName,
            owner = this
        )

        val viewModel = ViewModelProvider(
            vmStore
        )[viewModelClass]

        field.isAccessible = true
        field.set(this, viewModel)
    }
}


/**
 * 获取共享 ViewModelStore。
 */
fun commonViewStore(
    scopeName: String,
    owner: LifecycleOwner
): ActivityBusViewModelStore {

    val vmStore = cache.computeIfAbsent(scopeName) {
        ActivityBusViewModelStore()
    }

    vmStore.bindHost(owner)

    return vmStore
}


/**
 * Activity / Fragment / 其他 LifecycleOwner
 * 共享的 ViewModelStore。
 */
class ActivityBusViewModelStore : ViewModelStoreOwner {

    override val viewModelStore =
        ViewModelStore()

    /**
     * 保存当前正在使用这个 Store 的 LifecycleOwner。
     *
     * WeakHashMap 可以避免因为异常情况下的引用
     * 导致 Activity 无法回收。
     */
    private val hostList =
        Collections.synchronizedSet(
            Collections.newSetFromMap(
                WeakHashMap<LifecycleOwner, Boolean>()
            )
        )

    /**
     * 每个 Host 对应一个 Observer。
     */
    private val observers =
        ConcurrentHashMap<
                LifecycleOwner,
                LifecycleEventObserverImpl
                >()


    /**
     * 绑定 LifecycleOwner。
     */
    fun bindHost(owner: LifecycleOwner) {

        if (hostList.contains(owner)) {
            return
        }

        synchronized(hostList) {

            if (hostList.contains(owner)) {
                return
            }

            hostList.add(owner)

            val observer = LifecycleEventObserverImpl(
                store = this,
                owner = owner
            )

            observers[owner] = observer

            owner.lifecycle.addObserver(observer)
        }
    }


    /**
     * LifecycleOwner 销毁。
     */
    private fun unbindHost(owner: LifecycleOwner) {

        hostList.remove(owner)

        observers.remove(owner)?.let {
            owner.lifecycle.removeObserver(it)
        }

        /**
         * 已经没有任何 Host 使用这个 Store，
         * 清理 ViewModel。
         */
        if (hostList.isEmpty()) {
            clearIfUnused()
        }
    }


    /**
     * 清理 ViewModelStore。
     */
    private fun clearIfUnused() {

        if (hostList.isNotEmpty()) {
            return
        }

        for ((scopeName, store) in cache) {

            if (store === this) {

                cache.remove(
                    scopeName,
                    this
                )

                viewModelStore.clear()

                break
            }
        }
    }


    /**
     * Lifecycle Observer。
     */
    private class LifecycleEventObserverImpl(
        private val store: ActivityBusViewModelStore,
        private val owner: LifecycleOwner
    ) : DefaultLifecycleObserver {

        override fun onDestroy(
            owner: LifecycleOwner
        ) {
            store.unbindHost(owner)
        }
    }
}