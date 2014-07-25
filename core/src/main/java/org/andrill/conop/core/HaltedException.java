package org.andrill.conop.core;

/**
 * Thrown when a run should be aborted. Aborted runs should not be saved.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class HaltedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	protected final Solution solution;

	public HaltedException(final String message) {
		super(message);
		solution = null;
	}

	public HaltedException(final String message, final Solution solution) {
		super(message);
		this.solution = solution;
	}

	public Solution getSolution() {
		return solution;
	}
}
