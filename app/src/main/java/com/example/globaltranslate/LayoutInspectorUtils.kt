package com.example.globaltranslate
import android.app.Activity
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import org.json.JSONArray
import org.json.JSONObject

object LayoutInspectorUtils {

    /**
     * 获取当前 Activity 的 View 树并导出为 JSON 格式的字符串
     * @param activity 当前 Activity
     * @return JSON 格式的字符串
     */
    fun exportViewHierarchyToJson(activity: Activity): String {
        val rootView = activity.window.decorView
        val jsonObject = viewToJson(rootView)
        // indentSpaces = 2 表示格式化输出，方便阅读；如果想压缩日志传给服务器，设为 0
        return jsonObject.toString(2)
    }

    /**
     * 递归将 View 转换为 JSONObject
     */
    private fun viewToJson(view: View): JSONObject {
        val json = JSONObject()

        try {
            // 1. 基础信息
            json.put("class", view.javaClass.simpleName) // 类名，如 AppCompatTextView
            json.put("id", getIdString(view)) // ID 字符串，如 id/tv_title

            // 2. 文本内容 (如果是 TextView 或其子类)
            if (view is TextView) {
                val text = view.text.toString()
                if (text.isNotEmpty()) {
                    // 截断过长的文本，避免日志爆炸
                    json.put("text", if (text.length > 20) text.take(20) + "..." else text)
                }
            }

            // 3. 可见性
            json.put("visibility", getVisibilityString(view.visibility))

            // 4. 屏幕坐标 (绝对坐标)
            val screenLocation = IntArray(2)
            view.getLocationOnScreen(screenLocation)
            val rect = Rect(
                screenLocation[0],
                screenLocation[1],
                screenLocation[0] + view.width,
                screenLocation[1] + view.height
            )
            json.put("bounds", "[${rect.left},${rect.top}][${rect.right},${rect.bottom}]")
            json.put("width", view.width)
            json.put("height", view.height)

            // 5. 递归处理子 View
            if (view is ViewGroup && view.childCount > 0) {
                val childrenArray = JSONArray()
                for (child in view.children) {
                    childrenArray.put(viewToJson(child))
                }
                json.put("children", childrenArray)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return json
    }

    /**
     * 辅助方法：将 int 类型的 ID 转换为字符串名称 (如 R.id.button_login)
     */
    private fun getIdString(view: View): String {
        if (view.id == View.NO_ID) return "NO_ID"
        return try {
            // 获取资源名称，格式通常为 "package:type/entry"
            // 我们只取最后一部分 "type/entry" 看着更清晰
            val resName = view.resources.getResourceName(view.id)
            resName.substringAfter(":")
        } catch (e: Resources.NotFoundException) {
            view.id.toString() // 如果找不到名字，就直接返回数字 ID
        }
    }

    /**
     * 辅助方法：可见性转字符串
     */
    private fun getVisibilityString(visibility: Int): String {
        return when (visibility) {
            View.VISIBLE -> "VISIBLE"
            View.INVISIBLE -> "INVISIBLE"
            View.GONE -> "GONE"
            else -> "UNKNOWN"
        }
    }
}