package com.wyz.test.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wyz.emlibrary.TAG
import com.wyz.test.MyApplication
import com.wyz.test.db.UserEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TestViewModel() : ViewModel() {
    private val _loadObserver = MutableLiveData(false)
    val loadObserver = _loadObserver
    private fun load() {
        _loadObserver.value = true
    }


    private val _stateFlow = MutableStateFlow("default")
    val stateFlow = _stateFlow
    fun setState(state: String) {
        _stateFlow.value = state
    }


    private val _sharedFlow = MutableSharedFlow<String>(replay = 0)
    val sharedFlow = _sharedFlow.asSharedFlow()
    fun setSharedState(state: String) {
        viewModelScope.launch {
            Log.d(TAG, "sharedFLow 发送消息：")
            _sharedFlow.emit(state)
        }
    }


    val usersFlow = MyApplication
        .database
        .userDao()
        .getUsers()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val userLiveData = MyApplication
        .database
        .userDao()
        .getLastUser()

    fun insertUserInfo(user: UserEntity) {
        viewModelScope.launch {
            MyApplication.database.userDao().insert(user)
        }
    }

}

class TestViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TestViewModel() as T
    }
}
