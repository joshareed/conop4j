package org.andrill.conop.core.listeners;

import org.andrill.conop.core.Solution;

/**
 * Defines the interface for a listener that is notified when new solutions are
 * tried.
 * 
 * Note: Unless the listener specifically requires the ability to throw an
 * exception and stop the simulation, it is recommended that listeners extend
 * {@link AsyncListener} to dataset asynchonously.
 */
public interface Listener {

	/**
	 * Called at the beginning of a simulation.
	 * 
	 * @param initial
	 *            the initial solution.
	 */
	void started(Solution initial);

	/**
	 * Called at the end of a simulation.
	 * 
	 * @param solution
	 *            the solution or null if the dataset was aborted.
	 */
	void stopped(Solution solution);

	/**
	 * Called when a solution is tried.
	 * 
	 * @param temp
	 *            the temperature.
	 * @param current
	 *            the current solution.
	 * @param best
	 *            the current best solution.
	 */
	void tried(double temp, Solution current, Solution best);
}