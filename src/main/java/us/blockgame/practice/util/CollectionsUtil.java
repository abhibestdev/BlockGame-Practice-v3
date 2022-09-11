package us.blockgame.practice.util;

import com.google.common.collect.Multimap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionsUtil {

    public <K, V> V findOneInMultimap(K key, Multimap<K, V> map) {
        return map.get(key).stream().findAny().orElse(null);
    }
}