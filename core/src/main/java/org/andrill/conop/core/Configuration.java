package org.andrill.conop.core;

import java.util.Map;

import com.google.common.collect.Maps;

public class Configuration {
	protected Map<Object, Object> properties;

	public Configuration(final Map<Object, Object> properties) {
		if (properties == null) {
			this.properties = Maps.newHashMap();
		} else {
			this.properties = properties;
		}
	}

	public boolean get(final String key, final boolean defaultValue) {
		Object result = properties.get(key);
		if (result == null) {
			return defaultValue;
		}
		if (result instanceof Boolean) {
			return (boolean) result;
		}

		try {
			return Boolean.parseBoolean(result.toString());
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public double get(final String key, final double defaultValue) {
		Object result = properties.get(key);
		if (result == null) {
			return defaultValue;
		}
		if (result instanceof Number) {
			return ((Number) result).doubleValue();
		}

		try {
			return Double.parseDouble(result.toString());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public int get(final String key, final int defaultValue) {
		Object result = properties.get(key);
		if (result == null) {
			return defaultValue;
		}
		if (result instanceof Number) {
			return ((Number) result).intValue();
		}

		try {
			return Integer.parseInt(result.toString());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public long get(final String key, final long defaultValue) {
		Object result = properties.get(key);
		if (result == null) {
			return defaultValue;
		}
		if (result instanceof Number) {
			return ((Number) result).longValue();
		}

		try {
			return Long.parseLong(result.toString());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public Object get(final String key, final Object defaultValue) {
		Object result = properties.get(key);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	public String get(final String key, final String defaultValue) {
		Object result = properties.get(key);
		if (result == null) {
			return defaultValue;
		}
		if (result instanceof String) {
			return (String) result;
		}
		return result.toString();
	}
}
