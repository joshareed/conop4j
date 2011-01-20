package org.andrill.conop4j.objective;

/**
 * Defines the interface for a parallel function.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Parallel {

	/**
	 * Sets the number of processors to use for this parallel function.
	 * 
	 * @param processors
	 *            the number of processors.
	 */
	void setProcessors(int processors);
}
