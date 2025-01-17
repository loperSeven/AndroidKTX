package com.lxj.androidktx.core

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.lxj.androidktx.livedata.StateLiveData
import com.lxj.xpopup.impl.LoadingPopupView
import com.lxj.xpopup.interfaces.XPopupImageLoader
import java.io.File

/**
 * 绑定LiveData的状态
 */
fun LoadingPopupView.bindState(state: StateLiveData.State,
                               onLoading: (()->Unit)? = null,
                               onSuccess: (()->Unit)? = null,
                               onError: (()->Unit)? = null,
                               onEmpty: (()->Unit)? = null){
    when(state){
        StateLiveData.State.Loading->{
            show()
            onLoading?.invoke()
        }
        StateLiveData.State.Success->{
            delayDismissWith(500){
                onSuccess?.invoke()
            }
        }
        StateLiveData.State.Empty->{
            delayDismissWith(500){
                onEmpty?.invoke()
            }
        }
        StateLiveData.State.Error->{
            delayDismissWith(500){
                onError?.invoke()
            }
        }
    }
}

/**
 * 直接监听state状态
 */
fun LoadingPopupView.observeState(owner: LifecycleOwner,
                                  state: MutableLiveData<StateLiveData.State>,
                                  onLoading: (()->Unit)? = null,
                                  onSuccess: (()->Unit)? = null,
                                  onError: (()->Unit)? = null,
                                  onEmpty: (()->Unit)? = null){
    state.observe(owner, Observer<StateLiveData.State> {
        bindState(it, onLoading = onLoading, onSuccess = onSuccess,
                onError = onError, onEmpty = onEmpty)
    })
}

class GlideImageLoader(var placeholder: Int = 0) : XPopupImageLoader {
    override fun loadImage(
            position: Int,
            url: Any,
            imageView: ImageView
    ) {
        Glide.with(imageView).load(url)
                .apply(RequestOptions().placeholder(placeholder).override(Target.SIZE_ORIGINAL))
                .into(imageView)
    }

    override fun getImageFile(
            context: Context,
            uri: Any
    ): File? {
        try {
            return Glide.with(context).downloadOnly().load(uri).submit().get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}