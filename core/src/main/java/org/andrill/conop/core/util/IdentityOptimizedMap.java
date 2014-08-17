package org.andrill.conop.core.util;

import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.collect.Maps;

public class IdentityOptimizedMap<K, V> extends IdentityHashMap<K, V> {
	private static final long serialVersionUID = 1L;

	protected Map<K, V> secondary = Maps.newHashMap();

	@Override
	public V get(Object key) {
		V value = super.get(key);
		if (value == null) {
			value = secondary.get(key);
		}
		return value;
	}

	@Override
	public V put(K key, V value) {
		if (!secondary.containsKey(key)) {
			secondary.put(key, value);
		}
		return super.put(key, value);
	}

	@Override
	public boolean containsKey(Object key) {
		if (super.containsKey(key)) {
			return true;
		} else {
			return secondary.containsKey(key);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		if (super.containsValue(value)) {
			return true;
		} else {
			return secondary.containsValue(value);
		}
	}
}
