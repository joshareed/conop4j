package org.andrill.conop.core.internal;

import java.util.concurrent.LinkedBlockingQueue;

import org.andrill.conop.core.Solution;
import org.andrill.conop.core.constraints.Constraints;
import org.andrill.conop.core.mutators.Mutator;
import org.andrill.conop.core.solver.SolverContext;
import org.andrill.conop.core.solver.SolverStats;

public class SolutionGenerator extends Thread {
	protected Solution current = null;
	protected final Mutator mutator;
	protected final Constraints constraints;
	protected final SolverContext context;
	protected final LinkedBlockingQueue<Solution> valid;
	protected final LinkedBlockingQueue<Solution> invalid;
	protected final SolverStats stats;
	protected boolean alive = true;

	public SolutionGenerator(SolverContext context, Mutator mutator, Constraints constraints, int count) {
		this.context = context;
		this.mutator = mutator;
		this.constraints = constraints;

		valid = new LinkedBlockingQueue<Solution>(count);
		invalid = new LinkedBlockingQueue<Solution>(count);

		current = Solution.initial(context.getDataset());
		stats = context.get(SolverStats.class);
	}

	public void setCurrent(Solution current) {
		this.current = current;
	}

	public Solution getNext() {
		try {
			if (valid.isEmpty()) {
				return invalid.take();
			} else {
				return valid.take();
			}
		} catch (InterruptedException e) {
			return null;
		}
	}

	public void kill() {
		alive = false;
	}

	@Override
	public void run() {
		while (alive) {
			Solution next = context.getNext();
			if (next == null) {
				next = mutator.mutate(current);
			} else {
				setCurrent(next);
			}
			stats.total++;

			if (!invalid.offer(next)) {
				if (constraints.isValid(next)) {
					try {
						valid.put(next);
					} catch (InterruptedException e) {
						// ignore
					}
				} else {
					stats.skipped++;
				}
			}
		}
	}
}
