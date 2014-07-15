package org.andrill.conop.core.solver;

import java.util.Random;
import java.util.concurrent.RejectedExecutionException;

import org.andrill.conop.core.HaltedException;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.constraints.Constraints;
import org.andrill.conop.core.listeners.Listener;
import org.andrill.conop.core.mutators.Mutator;
import org.andrill.conop.core.penalties.Penalty;
import org.andrill.conop.core.schedules.Schedule;

public class StandardSolver extends AbstractSolver {
	protected Constraints constraints;
	protected Mutator mutator;
	protected Penalty penalty;
	protected Schedule schedule;
	protected Random random = new Random();

	@Override
	protected void initialize(final SolverConfiguration config) {
		constraints = config.getConstraints();
		mutator = config.getMutator();
		penalty = config.getPenalty();
		schedule = config.getSchedule();
		best = config.getInitialSolution();

		if (constraints instanceof Listener) {
			addListener((Listener) constraints);
		}
		if (mutator instanceof Listener) {
			addListener((Listener) mutator);
		}
		if (penalty instanceof Listener) {
			addListener((Listener) penalty);
		}
		if (schedule instanceof Listener) {
			addListener((Listener) schedule);
		}
		for (Listener l : config.getListeners()) {
			addListener(l);
		}
	}

	@Override
	protected Solution solve(final Solution initial) {
		Solution current = initial;

		// get our initial temperature and score
		double temp = schedule.getInitial();
		initial.setScore(penalty.score(initial));

		// initialize the listeners
		started(initial);

		try {
			// anneal
			while (temp > 0) {
				// get a new solution that satisfies the constraints
				Solution next = mutator.mutate(current);
				while (!constraints.isValid(next)) {
					next = mutator.mutate(current);
				}

				// score this solution
				next.setScore(penalty.score(next));

				// save as best if the penalty is less
				if (next.getScore() < best.getScore()) {
					best = next;
					if (best.getScore() == 0) {
						throw new RuntimeException("Score reached 0");
					}
				}

				// notify listeners
				for (Listener l : listeners) {
					l.tried(temp, current, best);
				}

				// accept the new solution if it is better than the current
				// or randomly based on score and temperature
				if ((next.getScore() < current.getScore()) || (Math.exp(-(next.getScore() - current.getScore()) / temp) > random.nextDouble())) {
					current = next;
				}

				temp = schedule.next(current);
			}
		} catch (Exception e) {
			HaltedException halt;
			if (e instanceof HaltedException) {
				halt = (HaltedException) e;
			} else if ((e instanceof InterruptedException) || (e instanceof RejectedExecutionException)) {
				halt = new HaltedException("User Interrupt");
			} else {
				halt = new HaltedException("Unexpected Error: " + e.getMessage());
			}
			stopped(best);
			throw halt;
		}

		// clean up
		stopped(best);
		return best;
	}
}
