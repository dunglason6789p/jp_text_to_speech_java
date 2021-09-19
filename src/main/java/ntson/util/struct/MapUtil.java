package ntson.util.struct;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapUtil {
    public static <K,V> Map<K,V> mapOf(Pair<K,V>... pairs) {
        Map<K,V> map = new HashMap<>();
        for (Pair<K,V> pair : pairs) {
            map.put(pair.a, pair.b);
        }
        return map;
    }
    public static class MapBuilder<K,V> {
        public static <K,V> MapBuilder<K,V> newBuilder() {
            return new MapBuilder<>();
        }
        private final Map<K,V> internalMap;
        public MapBuilder() {
            this.internalMap = new HashMap<>();
        }
        public MapBuilder(Supplier<Map<K,V>> mapType) {
            this.internalMap = mapType.get();
        }
        public MapBuilder<K,V> put(K key, V value) {
            this.internalMap.put(key, value);
            return this;
        }
        public Map<K,V> build() {
            return this.internalMap;
        }
    }
}
