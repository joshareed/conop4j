package org.andrill.conop.core;

/**
 * Thrown when a run should be aborted. Aborted runs should not be saved.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class HaltedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public HaltedException(final String message) {
		super(message);
	}
}
