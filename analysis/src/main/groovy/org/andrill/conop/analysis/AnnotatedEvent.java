package org.andrill.conop.analysis;

import java.util.Map;

import org.andrill.conop.core.Event;

import com.google.common.collect.Maps;

/**
 * An extended {@link Event} class that provides a mechanism to add annotations.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class AnnotatedEvent implements Event {
	public static final String POS = "pos";
	public static final String MAX_POS = "maxPos";
	public static final String MIN_POS = "minPos";

	protected final String name;
	protected final Map<String, Object> annotations;

	public AnnotatedEvent(final String name) {
		this.name = name;
		this.annotations = Maps.newHashMap();
	}

	public AnnotatedEvent(final String name, final Map<String, Object> annotations) {
		this.name = name;
		this.annotations = Maps.newHashMap(annotations);
	}

	public Object getAnnotation(final String key, final Object defaultValue) {
		if (annotations.containsKey(key)) {
			return annotations.get(key);
		} else {
			return defaultValue;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public void setAnnotation(final String key, final Object value) {
		annotations.put(key, value);
	}

}
