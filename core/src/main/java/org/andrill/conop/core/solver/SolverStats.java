package org.andrill.conop.core.solver;

public class SolverStats {
	public long scored = 0;
	public long skipped = 0;
	public long total = 0;
	public long elapsed = 0;
	public double best = Double.MAX_VALUE;
	public double temperature = Double.MAX_VALUE;
	public boolean constraints = false;
}
