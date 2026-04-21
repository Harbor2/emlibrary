package com.wyz.emlibrary.util

import android.util.Log
import com.wyz.emlibrary.TAG
import com.wyz.emlibrary.audioExtensionList
import com.wyz.emlibrary.docExtensionList
import com.wyz.emlibrary.imageExtensionList
import com.wyz.emlibrary.videoExtensionList
import com.wyz.emlibrary.zipExtensionList
import kotlinx.coroutines.*
import java.io.File
import java.io.FilenameFilter
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.text.contains
import kotlin.text.indexOfLast
import kotlin.text.map
import kotlin.text.replace
import kotlin.text.substring
import kotlin.text.toLowerCase
import kotlin.text.trimEnd

/**
 * 扫描文件工具
 * 优先级：
 *  1.空文件夹
 *  2.文件夹
 *  3.文件
 *  4.再上述基础上过滤文件和隐藏文件等内容
 *
 * 使用：
 * private val scanListener = object : EMScanFileUtil.ScanFileListener {...}
 *
 * val scanFile = EMScanFileUtil(externalStorageDirectory)
 *         // 设置过滤规则 Set up filter rules
 *         scanFile.setCallBackFilter(
 *             EMScanFileUtil.FileFilterBuilder().apply {
 *                 notScanHiddenFiles()
 *                 scanFilesByPrefix("apk")
 *                 scanImageFiles()
 *             }.build()
 *         )
 *         scanFile.setScanFileListener(scanListener)
 *         // 开始扫描 Start scanning
 *         scanFile.startAsyncScan()
 *
 * scanFile.stopScan() // 可主动结束扫描
 */
class EMScanFileUtil {
    /**
     * 过滤后的文件路径列表
     */
    private val filterFilePathList =  CopyOnWriteArrayList<String>()

    /**
     * 是否停止扫描
     */
    private var isScanningFinished = true

    /**
     * 要扫描的根路径
     */
    private val mRootPath: String

    /**
     * 扫描到文件回调过滤规则
     */
    private var mCallBackFilter: FilenameFilter? = null

    /**
     * 扫描时的过滤规则 只建议用来过滤隐藏文件和大小为0的文件
     */
    private var mScanFilter: FilenameFilter? = null

    /**
     * 扫描层级
     */
    private var mScanLevel = -1L

    /**
     * 协程扫描任务
     */
    private var mCoroutineScope: CoroutineScope? = null

    /**
     * 协程递归使用次数记录 每次递归调用都会产程一个新的进程 进程执行完成 递减1
     */
    private var mCoroutineSize = 0

    private var mScanTime = 0L

    /**
     * 文件扫描回调
     */
    private var mScanFileListener: ScanFileListener? = null

    /**
     * @param rootPath 扫描的路径
     */
    constructor(rootPath: String) {
        this.mRootPath = rootPath.trimEnd { it == '/' }
    }

    /**
     * @param rootPath 扫描的路径 Scanning path
     * @param scanFileListener 完成回调接口
     */
    constructor(rootPath: String, scanFileListener: ScanFileListener) {
        this.mRootPath = rootPath.trimEnd { it == '/' }
        mScanFileListener = scanFileListener
    }

    /**
     * 设置扫描监听器
     */
    fun setScanFileListener(scanFileListener: ScanFileListener) {
        mScanFileListener = scanFileListener
    }

    /**
     * 设置扫描层级数 最小为1
     */
    fun setScanLevel(level: Long) {
        if (level <= 0) return
        mScanLevel = level
    }

    /**
     * 停止扫描
     */
    fun stopScan() {
        if (isScanningFinished) return

        isScanningFinished = true
        mCoroutineScope?.cancel()
        mScanTime = System.currentTimeMillis() - mScanTime
        mScanFileListener?.onFinish(mScanTime, filterFilePathList)
    }

    /**
     * 获取扫描耗时
     *Time to get a scan
     */
    fun getScanTimeConsuming() = mScanTime

    /**
     * 开始异步扫描文件
     * Start scanning files asynchronously
     */
    fun startAsyncScan() {
        if (!isScanningFinished) {
            return
        }
        isScanningFinished = false
        mCoroutineSize = 0
        filterFilePathList.clear()

        Log.d(TAG, "根目录地址 $mRootPath")
        //检查路径的可用性
        val file = File(mRootPath)
        if (!file.exists()) {
            mScanFileListener?.onFinish(-1L, filterFilePathList)
            return
        }
        //如果协程是空的 或者已经结束过了，重新实例化协程
        if (mCoroutineScope == null || mCoroutineScope?.isActive == false) {
            mCoroutineScope = CoroutineScope(Dispatchers.IO)
        }
        mScanTime = System.currentTimeMillis()
        mScanFileListener?.onStart()
        //开始扫描
        asyncScan(file)
    }


    /**
     * 异步扫描文件， 递归调用
     * @param dirOrFile 要扫描的文件 或 文件夹
     */
    private fun asyncScan(dirOrFile: File) {
        plusCoroutineSize()
        // 将任务添加到列队中 Add tasks to the queue
        mCoroutineScope?.launch(Dispatchers.IO) {
            // 扫描路径层级判断 Scan path level judgment
            if (checkLevel(dirOrFile)) {
                checkCoroutineSize()
                return@launch
            }

            // 检查是否是文件 是文件就直接回调 返回true
            if (dirOrFile.isFile) {
                withContext(Dispatchers.Main) {
                    mScanFileListener?.onFileScan(dirOrFile)
                    if (filterFile(dirOrFile)) {
                        mScanFileListener?.onTargetFileScan(dirOrFile)
                    }
                }
                checkCoroutineSize()
                return@launch
            }
            // 获取文件夹中的文件集合
            val rootFile = getFilterFilesList(dirOrFile)
            // 遍历文件夹
            rootFile?.map {
                //如果是文件夹-回调, 调用自己,再遍历扫描
                if (it.isDirectory) {
                    withContext(Dispatchers.Main) {
                        mScanFileListener?.onFileScan(it)
                        if (filterFile(it)) {
                            mScanFileListener?.onTargetFileScan(it)
                        }
                    }
                    //再次调用此方法
                    asyncScan(it)
                } else {
                    //是文件,回调,验证过滤规则
                    withContext(Dispatchers.Main) {
                        mScanFileListener?.onFileScan(it)
                        if (filterFile(it)) {
                            mScanFileListener?.onTargetFileScan(it)
                        }
                    }
                }
            }
            checkCoroutineSize()
            return@launch
        }
    }

    /**
     * 增加一次协程使用次数
     * Increase the use of coroutines
     */
    @Synchronized
    private fun plusCoroutineSize() {
        mCoroutineSize++
    }

    /**
     * 检查协程使用次数 减少一次
     * 如果mCoroutineSize==0说明已经扫描完了
     */
    @Synchronized
    private fun checkCoroutineSize() {
        mCoroutineSize--
        // 如果mCoroutineSize==0,说明协程全部执行完毕，可以回调完成方法
        if (mCoroutineSize == 0) {
            isScanningFinished = true
            mCoroutineScope?.launch(Dispatchers.Main) {
                mScanTime = System.currentTimeMillis() - mScanTime
                mScanFileListener?.onFinish(mScanTime, filterFilePathList)
                mCoroutineScope?.cancel()
            }
        }
    }

    /**
     * 检查扫描路径是否已经到指定的层级
     */
    private fun checkLevel(dirOrFile: File): Boolean {
        if (mScanLevel != -1L) {
            var scanLevelCont = 0L
            dirOrFile.absolutePath.replace(mRootPath, "").map {
                if (it == '/') {
                    scanLevelCont++
                    if (scanLevelCont >= mScanLevel) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 校验并处理过滤文件
     */
    private fun filterFile(file: File): Boolean {
        return if (mCallBackFilter == null) {
            !isScanningFinished
        } else {
            val accept = mCallBackFilter!!.accept(file, file.name)
            // 有过滤规则且是文件类型，添加到过滤列表中
            if (accept) {
                filterFilePathList.add(file.path)
            }
            return accept && !isScanningFinished
        }
    }

    /**
     * 获取扫描任务，
     * 不要手动调用此方法，
     * 使用ScanTogetherManager().scan()时自动调用此方法。
     */
    private fun getScanningTask(): CoroutineScope? {
        return mCoroutineScope
    }

    /**
     *  文件通过callback返回结果时过滤规则
     *  @param filter 使用FileFilterBuilder设置过滤规则，Use File Filter Builder to set filter rules
     */
    fun setCallBackFilter(filter: FilenameFilter?) {
        this.mCallBackFilter = filter
    }

    /**
     * 扫描时过滤规则
     * 过滤速度很快，但是可能会过滤掉一些父级文件夹，它的子文件夹将不会被扫描
     * Filter rules when scanning.
     * The filtering speed is fast, but some parent folders may be filtered out,
     * and its subfolders will not be scanned
     * @Deprecated use {@link setCallBackFilter}
     */
    @Deprecated(message = "不建议使用")
    fun setScanningFilter(filter: FilenameFilter?) {
        this.mScanFilter = filter
    }


    /**
     * 获取文件夹中的文件列表 并且引用过滤规则
     * Get a list of files in a folder and reference filter rules
     */
    private fun getFilterFilesList(file: File): Array<File>? {
        return if (mScanFilter == null) {
            file.listFiles()
        } else {
            file.listFiles(mScanFilter)
        }
    }

    /**
     * 过滤器构造器
     */
    class FileFilterBuilder {
        /**
         * 表达式正则
         */
        private val pathRegexList: ArrayList<String> = arrayListOf()

        /**
         * 文件类型&文件后缀 扫描过滤规则 集合
         */
        private val mFilesSuffixFilterSet: MutableSet<String> = hashSetOf()

        /**
         * 扫描名字像它的 集合
         */
        private val mNameLikeFilterSet: MutableSet<String> = hashSetOf()

        /**
         * 扫描名字不像它的文件 集合,
         * 也就是不扫描名字像这个的文件 集合
         */
        private val mNameNotLikeFilterSet: MutableSet<String> = hashSetOf()

        /**
         * 是否扫描隐藏文件 true扫描 false不扫描
         */
        private var isScanHiddenFiles = true

        /**
         * 只接受空文件夹
         */
        private var isOnlyEmptyDir = false

        /**
         * 只扫描文件夹
         */
        private var isOnlyDir = false

        /**
         * 只扫描文件
         */
        private var isOnlyFile = false

        /**
         * 只扫描文件夹
         */
        fun onlyScanDir() {
            isOnlyDir = true
        }

        /**
         * 只扫描空目录
         */
        fun onlyScanEmptyDir() {
            isOnlyEmptyDir = true
        }

        /**
         * 只要扫描文件
         */
        fun onlyScanFile() {
            isOnlyFile = true
        }

        /**
         * 扫描名字像它的文件或者文件夹
         */
        fun scanNameLikeIt(like: String) {
            mNameLikeFilterSet.add(like.toLowerCase(Locale.getDefault()))
        }

        /**
         * 扫描名与其文件不同
         * 不要扫描这样的文件
         */
        fun scanNameNotLikeIt(like: String) {
            mNameNotLikeFilterSet.add(like.toLowerCase(Locale.getDefault()))
        }

        /**
         * 不扫描隐藏文件
         */
        fun notScanHiddenFiles() {
            isScanHiddenFiles = false
        }

        /**
         * 扫描apk文件
         */
        fun scanApkFiles() {
            mFilesSuffixFilterSet.add("apk")
        }

        /**
         * 扫描文档类型文件
         */
        fun scanDocFiles() {
            docExtensionList.forEach { mFilesSuffixFilterSet.add(it) }
        }

        /**
         * 扫描图片类型文件
         */
        fun scanImageFiles() {
            imageExtensionList.forEach { mFilesSuffixFilterSet.add(it) }
        }

        /**
         * 扫描多媒体文件类型
         */
        fun scanVideoFiles() {
            videoExtensionList.forEach { mFilesSuffixFilterSet.add(it) }
        }

        /**
         * 扫描音频文件类型
         */
        fun scanAudioFiles() {
            audioExtensionList.forEach { mFilesSuffixFilterSet.add(it) }
        }

        /**
         * 扫描压缩包文件类型
         */
        fun scanZipFiles() {
            zipExtensionList.forEach { mFilesSuffixFilterSet.add(it) }
        }

        /**
         * 增加正则表达式匹配
         */
        fun scanFilesByPrefix(prefixList: ArrayList<String>) {
            pathRegexList.addAll(prefixList)
        }

        /**
         * 检查名字相似过滤
         */
        private fun checkNameLikeFilter(name: String): Boolean {
            // 相似名字获取过滤
            if (mNameLikeFilterSet.isNotEmpty()) {
                mNameLikeFilterSet.map {
                    if (name.toLowerCase(Locale.getDefault()).contains(it)) {
                        return true
                    }
                }
                return false
            }
            return true
        }

        /**
         * 检查名字不相似过滤
         */
        private fun checkNameNotLikeFilter(name: String): Boolean {
            // 名字不相似顾虑
            if (mNameNotLikeFilterSet.isNotEmpty()) {
                mNameNotLikeFilterSet.map {
                    if (name.toLowerCase(Locale.getDefault()).contains(it)) {
                        return false
                    }
                }
                return true
            }
            return true
        }

        /**
         * 检查文件后缀过滤规则 既文件类型过滤规则
         */
        private fun checkSuffixFilter(name: String): Boolean {
            return if (mFilesSuffixFilterSet.isNotEmpty()) {
                // 获取文件后缀
                val suffix: String =
                    name.substring(name.indexOfLast { it == '.' } + 1, name.length)
                        .toLowerCase(Locale.getDefault())
                // return 是否包含这个文件
                mFilesSuffixFilterSet.contains(suffix)
            } else {
                true
            }
        }

        /**
         * 正则匹配
         */
        private fun isFilePathMatchRegexList(filePath: String): Boolean {
            return if (pathRegexList.isNotEmpty()) {
                EMUtil.isStrMatchRegexList(filePath, pathRegexList)
            } else {
                true
            }
        }

        /**
         * 重置构建器
         */
        fun resetBuild() {
            mFilesSuffixFilterSet.clear()
            mNameLikeFilterSet.clear()
            mNameNotLikeFilterSet.clear()
            pathRegexList.clear()
            isScanHiddenFiles = true
            isOnlyDir = false
            isOnlyFile = false
        }

        /**
         * 创建过滤规则
         * Create filter rule
         */
        fun build(): FilenameFilter {
            return object : FilenameFilter {

                override fun accept(dir: File, name: String): Boolean {
                    // 隐藏文件扫描规则 优先级高
                    if (!isScanHiddenFiles && dir.isHidden) {
                        return false
                    }

                    // 只扫描空文件夹
                    if (isOnlyEmptyDir) {
                        if (dir.isFile) {
                            return false
                        }
                        val count = EMFileUtil.getDirFilesCount(dir,
                            containerSubFile = true,
                            containDir = true,
                            containerHiddenFile = isScanHiddenFiles
                        ).first
                        if (count != 0) return false

                        return checkNameLikeFilter(name)
                                || checkNameNotLikeFilter(name)
                                || isFilePathMatchRegexList(dir.absolutePath)
                    }

                    // 只扫描文件夹
                    if (isOnlyDir) {
                        if (dir.isFile) {
                            return false
                        }
                        return checkNameLikeFilter(name)
                                || checkNameNotLikeFilter(name)
                                || isFilePathMatchRegexList(dir.absolutePath)
                    }

                    // 只扫描文件
                    if (isOnlyFile) {
                        if (dir.isDirectory) {
                            return false
                        }
                        return checkNameLikeFilter(name)
                                || checkNameNotLikeFilter(name)
                                || checkSuffixFilter(name)
                                || isFilePathMatchRegexList(dir.absolutePath)
                    }

                    // 其他情况
                    return if (dir.isDirectory) {
                        checkNameLikeFilter(name)
                                || checkNameNotLikeFilter(name)
                                || isFilePathMatchRegexList(dir.absolutePath)
                    } else {
                        checkNameLikeFilter(name)
                                || checkNameNotLikeFilter(name)
                                || checkSuffixFilter(name)
                                || isFilePathMatchRegexList(dir.absolutePath)
                    }
                }
            }
        }
    }

    /**
     * 扫描文件监听器
     * Scanning file listener
     */
    interface ScanFileListener {

        /**
         * 任何线程可调用
         * 扫描开始的时候 描述
         */
        fun onStart()

        /**
         * 在主线程回调
         * 扫描完成回调
         * 同时也是异常处理回调，取决于返回时间如果是负值的话
         * @param timeConsuming 耗时 时间为-1说明扫描目录不存在
         * @param filePathList 过滤后的文件列表
         */
        fun onFinish(timeConsuming: Long, filePathList: List<String>)

        /**
         * 在主线程回调
         * 文件扫描
         * 每扫描到一个文件则回调一次
         */
        fun onFileScan(file: File)

        /**
         * 在主线程回调
         * 扫描到文件时回调，每扫描到一个文件触发一次
         * @param file 扫描的文件
         */
        fun onTargetFileScan(file: File)
    }
}