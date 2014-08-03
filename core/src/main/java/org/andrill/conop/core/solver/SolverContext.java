package org.andrill.conop.core.solver;

import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Solution;

/**
 * Defines a simple interface for solver components to publish results from the
 * solver run.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface SolverContext {

	/**
	 * Gets the best solution found.
	 * 
	 * @return the best solution or null.
	 */
	Solution getBest();

	/**
	 * Sets the best solution.
	 * 
	 * @param best
	 *            the best solution.
	 * @return the best solution.
	 */
	Solution setBest(Solution best);

	/**
	 * Gets the dataset.
	 * 
	 * @return the dataset.
	 */
	Dataset getDataset();

	/**
	 * Sets the dataset.
	 * 
	 * @param dataset
	 *            the dataset.
	 * @return the dataset.
	 */
	Dataset setDataset(Dataset dataset);

	/**
	 * Get next potential solution.
	 * 
	 * @return the next solution or null.
	 */
	Solution getNext();

	/**
	 * Set the next solution to try.
	 * 
	 * @param next
	 *            the next solution.
	 * @return the next solution.
	 */
	Solution setNext(Solution next);

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
