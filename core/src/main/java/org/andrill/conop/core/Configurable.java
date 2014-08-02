package org.andrill.conop.core;

import org.andrill.conop.core.solver.SolverContext;

/**
 * Defines the interface for a configurable object.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Configurable {

	/**
	 * Configures this object with the specified configuration map.
	 *
	 * @param config
	 *            the configuration.
	 */
	public void configure(Configuration config);

	/**
	 * Sets the solver context for this object.
	 * 
	 * @param context
	 *            the solver context.
	 */
	public void setContext(SolverContext context);
}
