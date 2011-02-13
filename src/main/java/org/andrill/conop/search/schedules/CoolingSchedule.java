package org.andrill.conop.search.schedules;

import org.andrill.conop.search.Configurable;
import org.andrill.conop.search.Solution;

/**
 * Encapsulates a cooling schedule.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface CoolingSchedule extends Configurable {

	/**
	 * Gets the initial temperature.
	 * 
	 * @return the temperature.
	 */
	double getInitial();

	/**
	 * Gets the next temperature.
	 * 
	 * @param temp
	 *            the existing temperature.
	 * @param solution
	 *            the current best solution.
	 * 
	 * @return the next temperature.
	 */
	double next(Solution solution);
}
