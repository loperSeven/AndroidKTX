package com.lxj.androidktx.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lxj.statelayout.StateLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Description: 携带状态的LiveData
 * Create by lxj, at 2019/3/6
 */
class StateLiveData<T> : NoStickyLiveData<T>() {

    enum class State {
        Idle, Loading, Success, Error, Empty
    }

    val state = MutableLiveData<State>()
    var errMsg = ""

    init {
        state.value = State.Idle
    }

    fun postValueAndSuccess(value: T) {
        super.postValue(value)
        postSuccess()
    }
    fun postEmptyAndSuccess(t: T){
        super.postValue(t)
        postEmpty()
    }

    fun clearState() {
        this.errMsg = ""
        state.postValue(State.Idle)
    }

    fun postLoading() {
        state.postValue(State.Loading)
    }

    fun postSuccess() {
        state.postValue(State.Success)
    }

    fun postError(error: String = "") {
        if(error.isNotEmpty()) this.errMsg = ""
        state.postValue(State.Error)
    }

    fun postEmpty() {
        state.postValue(State.Empty)
    }

    fun changeState(s: State) {
        state.postValue(s)
    }

    fun bindStateLayout(owner: LifecycleOwner, stateLayout: StateLayout) {
        state.observe(owner, Observer {
            when (it) {
                State.Loading -> stateLayout.showLoading()
                State.Error -> stateLayout.showError()
                State.Success -> stateLayout.showContent()
                State.Empty -> stateLayout.showEmpty()
            }
        })
    }

    /**
     * 带绑定StateLayout
     */
    fun observeWithStateLayout(owner: LifecycleOwner, stateLayout: StateLayout, observer: Observer<T>) {
        bindStateLayout(owner, stateLayout)
        observe(owner, observer)
    }

    /**
     * 智能post值，能根据值进行智能的设置自己的状态，无需手工编写代码
     * @param dataValue 目标值，根据目标值去设置对应的state
     */
    fun smartPost(dataValue: T?){
        if(dataValue==null){
            postError() //未得到期望数据，不传递null
        }else if(dataValue is Collection<*> && dataValue.isEmpty()){
            postEmptyAndSuccess(dataValue) //数据成功但是空，需要传递空，UI需要刷新
        }else{
            postValueAndSuccess(dataValue)
        }
    }

    /**
     * 强大而实用的封装，启动协程执行逻辑（比如网络请求），并对逻辑结果进行智能post。示例如下：
     * launchAndSmartPost {
     *      "https://iandroid.xyz/api".http().get<T>().await()
     * }
     */
    fun launchAndSmartPost(block: suspend CoroutineScope.() -> T?): Job {
        postLoading()
        return GlobalScope.launch { smartPost(block()) }
    }



}

