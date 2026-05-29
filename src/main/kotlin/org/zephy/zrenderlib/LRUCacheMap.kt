package org.zephy.zrenderlib

class LRUCacheMap<K, V>(private val capacity: Int) {
    private val cache = LinkedHashMap<K, V>(capacity, 0.75f, true)

    fun has(key: K): Boolean = cache.containsKey(key)

    operator fun get(key: K): V? = cache[key]

    operator fun set(key: K, value: V) = put(key, value)

    fun put(key: K, value: V) {
        cache[key] = value
        if (cache.size > capacity) {
            cache.remove(cache.keys.first())
        }
    }

    fun clear() = cache.clear()

    fun size(): Int = cache.size
}
