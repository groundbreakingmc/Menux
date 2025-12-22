package com.github.groundbreakingmc.menux.utils;

import com.github.groundbreakingmc.menux.click.ClickType;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class ClickMap<V> {

    private final Map<ClickType, V> map;

    public ClickMap(Map<ClickType, V> map) {
        this.map = ImmutableMap.copyOf(map);
    }

    public V get(ClickType clickType) {
        return this.map.get(clickType);
    }
}
