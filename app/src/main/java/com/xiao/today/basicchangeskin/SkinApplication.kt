package com.xiao.today.basicchangeskin

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import java.io.File

class SkinApplication: Application() {

    lateinit var skinResource: Resources

    var skinPackageName: String = ""

    override fun onCreate() {
        super.onCreate()
        loadSkinResource()
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun loadSkinResource() {
        try {
            val apkFile = File(cacheDir, "app-debug.apk")
            val clazz = AssetManager::class.java
            val constructor = clazz.getConstructor()
            val skinAssetManager = constructor.newInstance()
            val addAssetPath = clazz.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(skinAssetManager, apkFile.absolutePath)
            skinResource = Resources(skinAssetManager, resources.displayMetrics, resources.configuration)
            val packageArchiveInfo = packageManager.getPackageArchiveInfo(
                apkFile.absolutePath,
                PackageManager.GET_ACTIVITIES
            )
            skinPackageName = packageArchiveInfo?.packageName?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}