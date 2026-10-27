package com.wyz.emlibrary.util

import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import com.wyz.emlibrary.TAG
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.text.isNotEmpty

object EMMapUtil {

    /**
     * 解析 InputStream 为 Map<String, Any?> 类型
     */
    fun parserInputStream(inputStream: InputStream): Map<String, Any?> {
        var jsonReader: JsonReader? = null
        try {
            val reader = inputStream.reader(Charset.forName("utf-8"))
            jsonReader = JsonReader(reader)
            return parserObject(jsonReader)
        } catch (e: Throwable) {
            Log.d(TAG, "parser(), e = $e")
        } finally {
            jsonReader?.close()
        }

        return LinkedHashMap()
    }

    fun optInt(map: Any?, defaultValue: Int, vararg path: String): Int {
        map ?: return defaultValue
        val obj = getObject(map, *path)
        return if (obj is Number) {
            obj.toInt()
        } else {
            defaultValue
        }
    }

    fun optLong(map: Any?, defaultValue: Long, vararg path: String): Long {
        map ?: return defaultValue
        val obj = getObject(map, *path)
        return if (obj is Number) {
            obj.toLong()
        } else {
            defaultValue
        }
    }

    fun optFloat(map: Any?, defaultValue: Float, vararg path: String): Float {
        map ?: return defaultValue
        val obj = getObject(map, *path)
        return if (obj is Number) {
            obj.toFloat()
        } else {
            defaultValue
        }
    }

    fun optBoolean(map: Any?, defaultValue: Boolean, vararg path: String): Boolean {
        map ?: return defaultValue
        val obj = getObject(map, *path)
        return obj as? Boolean ?: defaultValue
    }

    fun optString(map: Any?, defaultValue: String, vararg path: String): String {
        map ?: return defaultValue
        val obj = getObject(map, *path)
        return obj as? String ?: defaultValue
    }

    fun optList(map: Any?, defaultValue: List<*>?, vararg path: String): List<*>? {
        map ?: return defaultValue
        val obj = getObject(map, *path)
        return obj as? List<*> ?: defaultValue
    }

    fun optMap(map: Any?, defaultValue: Map<*, *>?, vararg path: String): Map<*, *>? {
        map ?: return defaultValue
        val obj = getObject(map, *path)
        return obj as? Map<*, *> ?: defaultValue
    }

    @Suppress("UNCHECKED_CAST")
    private fun getObject(map: Any?, vararg path: String): Any? {
        if (path.isEmpty() || map !is Map<*, *>) {
            return null
        }
        try {
            var curMap = map as Map<String, Any?>
            for (i in 0 until path.size - 1) {
                val any = curMap[path[i]]
                if (any is Map<*, *>) {
                    curMap = any as Map<String, Any?>
                    continue
                }
                return null
            }
            return curMap[path[path.size - 1]]
        } catch (_: Throwable) {
            return null
        }
    }

    private fun parserObject(reader: JsonReader): Map<String, Any?> {
        val map = LinkedHashMap<String, Any?>()
        reader.beginObject()
        while (reader.peek() == JsonToken.NAME) {
            val key = reader.nextName()
            if (key != null && key.isNotEmpty()) {
                map[key] = parserValue(reader)
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()

        return map
    }

    private fun parserValue(reader: JsonReader): Any? {
        return when (reader.peek()) {
            JsonToken.STRING -> reader.nextString()
            JsonToken.NUMBER -> reader.nextDouble()
            JsonToken.BOOLEAN -> reader.nextBoolean()
            JsonToken.NULL -> reader.nextNull()
            JsonToken.BEGIN_OBJECT -> parserObject(reader)
            JsonToken.BEGIN_ARRAY -> parserList(reader)
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    private fun parserList(reader: JsonReader): List<Any?> {
        val list = ArrayList<Any?>()
        reader.beginArray()
        var token = reader.peek()
        while (token != JsonToken.END_ARRAY) {
            list.add(parserValue(reader))
            token = reader.peek()
        }
        reader.endArray()
        return list
    }
}