package org.andrill.conop.analysis.legacy;

import java.util.List;
import java.util.Map;

public interface Solution {

	/**
	 * Get all events.
	 * 
	 * @return the list of events.
	 */
	public List<Map<String, String>> getEvents();

	/**
	 * Gets the name of this dataset.
	 * 
	 * @return the name.
	 */
	public String getName();

	/**
	 * Gets all sections.
	 * 
	 * @return the list of sections.
	 */
	public List<Map<String, String>> getSections();

}