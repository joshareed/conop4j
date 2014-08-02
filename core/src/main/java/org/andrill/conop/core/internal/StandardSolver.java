package org.andrill.conop.core.internal;

import java.util.Random;

import org.andrill.conop.core.Solution;
import org.andrill.conop.core.constraints.Constraints;
import org.andrill.conop.core.listeners.Listener;
import org.andrill.conop.core.mutators.Mutator;
import org.andrill.conop.core.penalties.Penalty;
import org.andrill.conop.core.schedules.Schedule;
import org.andrill.conop.core.solver.AbstractSolver;
import org.andrill.conop.core.solver.SolverConfiguration;

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
		setContext(constraints, mutator, penalty, schedule);

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
		setContext(listeners);
	}

	@Override
	protected void solve(final Solution initial) {
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
				updateBest(next);

				// notify listeners
				for (Listener l : listeners) {
					l.tried(temp, current, getBest());
				}

				// accept the new solution if it is better than the current
				// or randomly based on score and temperature
				if ((next.getScore() < current.getScore())
						|| (Math.exp(-(next.getScore() - current.getScore())
								/ temp) > random.nextDouble())) {
					current = next;
				}

				temp = schedule.next(current);
			}
		} catch (Exception e) {
			handleError(e);
		}

		// clean up
		stopped(getBest());
	}
}
