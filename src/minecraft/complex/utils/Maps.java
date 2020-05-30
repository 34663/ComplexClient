package complex.utils;

import java.util.Map;
import java.util.function.Function;

public class Maps {
    public static <K, V> void putAll(final Map<K, V> map, final Function<V, K> converter, final V... values) {
        for (final V value : values) {
            map.put(converter.apply(value), value);
        }
    }
}
