package com.xiao.today.basicchangeskin

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val sClassPrefixList = arrayOf(
        "android.widget.",
        "android.webkit.",
        "android.app.",
        "android.view."
    )

    private val mConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)

    private val collectionAttributes = arrayOf(
        "text", "textSize", "textColor", "background"
    )

    private val skinMap = mutableMapOf<View, List<SkinBean>>()

    private lateinit var skinApplication: SkinApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutInflaterFactory()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        skinApplication = application as SkinApplication

        Handler(Looper.getMainLooper()).postDelayed({
            skinMap.forEach { map ->
                map.value.forEach {
                    changeSkin(map.key, it)
                }
            }
        }, 3_000)
    }

    private fun changeSkin(view: View, bean: SkinBean) {
        when(bean.attributeName){
            "text" -> {
                val skinPackageResourcesId =
                    getSkinPackageResourcesId(skinApplication.skinResource, bean.value)
                if (view is TextView){
                    view.text = skinApplication.skinResource.getText(skinPackageResourcesId)
                }
            }
            "textSize" -> {
                val skinPackageResourcesId =
                    getSkinPackageResourcesId(skinApplication.skinResource, bean.value)
                if (view is TextView){
                    val textSize =
                        skinApplication.skinResource.getDimensionPixelSize(skinPackageResourcesId)
                            .toFloat()
                    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                }
            }
            "textColor" -> {
                val skinPackageResourcesId =
                    getSkinPackageResourcesId(skinApplication.skinResource, bean.value)
                if (view is TextView){
                    view.setTextColor(skinApplication.skinResource.getColor(skinPackageResourcesId))
                }
            }
            "background" -> {
                val skinPackageResourcesId =
                    getSkinPackageResourcesId(skinApplication.skinResource, bean.value)
                if (view is TextView){
                    view.setBackgroundColor(skinApplication.skinResource.getColor(skinPackageResourcesId))
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getSkinPackageResourcesId(skinResources: Resources, originalResourcesId: Int): Int {
        return skinResources.getIdentifier(
            resources.getResourceEntryName(originalResourcesId),
            resources.getResourceTypeName(originalResourcesId),
            skinApplication.skinPackageName
        )
    }

    private fun setLayoutInflaterFactory() {
        LayoutInflater.from(this).factory2 = object : Factory2 {
            override fun onCreateView(
                parent: View?,
                name: String,
                context: Context,
                attrs: AttributeSet
            ): View? {
                val view = if (-1 == name.indexOf('.')) {
                    var view: View? = null
                    sClassPrefixList.forEach {
                        if (view == null) {
                            view = createView(context, it + name, attrs)
                        } else {
                            return@forEach
                        }
                    }
                    view
                } else {
                    createView(context, name, attrs)
                }
                collectionAttributes(view, name, attrs)
                return view
            }

            override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
                return onCreateView(null, name, context, attrs)
            }

        }
    }

    private fun collectionAttributes(view: View?, viewName: String, attrs: AttributeSet) {
        val skinBeanList = mutableListOf<SkinBean>()
        for (i in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(i)
            if (collectionAttributes.contains(attributeName)){
                val value = attrs.getAttributeValue(i)
                if (value.startsWith('@')){
                    val valueInt = value.drop(1).toInt()
                    skinBeanList.add(
                        SkinBean(attributeName, valueInt)
                    )
                }
            }
        }
        if (view != null && skinBeanList.isNotEmpty()) skinMap[view] = skinBeanList
    }

    private fun createView(
        context: Context,
        name: String,
        attrs: AttributeSet
    ): View? {
        return try {
            val forName = Class.forName(name)
            val constructor = forName.getConstructor(*mConstructorSignature)
             constructor.newInstance(context, attrs) as View?
        } catch (e: Exception) {
            null
        }
    }
}

data class SkinBean(
    val attributeName: String, val value: Int
)