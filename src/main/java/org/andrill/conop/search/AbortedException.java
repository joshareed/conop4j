package org.andrill.conop.search;

/**
 * Thrown when a run should be aborted. Aborted runs should not be saved.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class AbortedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AbortedException(final String message) {
		super(message);
	}
}
