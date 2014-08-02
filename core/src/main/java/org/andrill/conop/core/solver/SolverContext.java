package org.andrill.conop.core.solver;

/**
 * Defines a simple interface for solver components to publish results from the
 * solver run.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface SolverContext {

	/**
	 * Get a result by type.
	 * 
	 * @param type
	 *            the interface or class of the type.
	 * @return the object or null.
	 */
	<O> O get(Class<? super O> type);

	/**
	 * Put a result by type.
	 * 
	 * @param type
	 *            the interface or class of the type.
	 * @param obj
	 *            the object.
	 * @return the object.
	 */
	<O> O put(Class<? super O> type, O obj);
}
