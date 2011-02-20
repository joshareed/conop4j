package org.andrill.conop.search.listeners;

import org.andrill.conop.search.Configurable;
import org.andrill.conop.search.Solution;

/**
 * Notified when new solutions are tried.
 */
public interface Listener extends Configurable {

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