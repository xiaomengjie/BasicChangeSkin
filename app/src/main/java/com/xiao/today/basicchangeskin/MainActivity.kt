package com.xiao.today.basicchangeskin

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflater.from(this).factory2 = object : Factory2{
            override fun onCreateView(
                parent: View?,
                name: String,
                context: Context,
                attrs: AttributeSet
            ): View? {
                val view = if(-1 == name.indexOf('.')) {
                    var view: View? = null
                    sClassPrefixList.forEach {
                        if (view == null){
                            view = createView(context, it + name, attrs)
                        }else{
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun collectionAttributes(view: View?, viewName: String, attrs: AttributeSet) {
        val skinBeanList = mutableListOf<SkinBean>()
        for (i in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(i)
            if (collectionAttributes.contains(attributeName)){
                val value = attrs.getAttributeValue(i)
                if (value.startsWith('@')){
                    val valueInt = value.drop(1).toInt()
                    val valueName = resources.getResourceEntryName(valueInt)
                    skinBeanList.add(
                        SkinBean(attributeName, valueName, valueInt)
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
    val attributeName: String, val valueName: String, val value: Int
)