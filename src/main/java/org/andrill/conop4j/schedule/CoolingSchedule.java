package org.andrill.conop4j.schedule;

import org.andrill.conop4j.Solution;

/**
 * Encapsulates a cooling schedule.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface CoolingSchedule {

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
