package org.andrill.conop.core;

/**
 * Defines the interface for a configurable object.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Configurable {

	/**
	 * Configures this object with the specified simulation.
	 * 
	 * @param simulation
	 *            the simulation.
	 */
	public void configure(Simulation simulation);
}
