package com.wyz.emlibrary.util

internal object EMMapUtils {

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
}