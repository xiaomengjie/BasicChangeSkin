package com.xiao.today.basicchangeskin

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
        try {
            val apkFile = File(cacheDir, "app-debug.apk")
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPath = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assetManager, apkFile.absolutePath)
            skinResource = Resources(assetManager, resources.displayMetrics, resources.configuration)
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