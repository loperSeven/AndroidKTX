package com.lxj.androidktxdemo

import android.app.Application
import com.lxj.androidktx.AndroidKtxConfig
import com.lxj.androidktx.core.*
import com.lxj.androidktxdemo.entity.User
import kotlin.math.log

/**
 * Description:
 * Create by dance, at 2018/12/5
 */
class AndroidKtxApp: Application(){
    override fun onCreate() {
        super.onCreate()
//        AndroidKtxConfig.init(this)
        AndroidKtxConfig.init(context = this,
                isDebug = BuildConfig.DEBUG,
                defaultLogTag = "androidktx",
                sharedPrefName = "demo")


        logd("ktx app start...")
//        "sss".e()

        val user = sp().getObject<User>("user")
        loge("user: ${user}")
        sp().putObject("user", User(name = "李晓俊", age = 1000))
        loge("user: ${sp().getObject<User>("user")}")


    }
}