package org.andrill.conop.search;

import java.util.Properties;

/**
 * Defines the interface for a configurable object.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Configurable {

	/**
	 * Configures this object with the specified properties.
	 * 
	 * @param properties
	 *            the properties.
	 */
	public void configure(Properties properties);
}
