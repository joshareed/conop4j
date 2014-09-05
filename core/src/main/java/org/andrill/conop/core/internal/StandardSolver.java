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
import org.andrill.conop.core.solver.SolverStats;
import org.andrill.conop.core.util.TimerUtils;

public class StandardSolver extends AbstractSolver {
	protected Constraints constraints;
	protected Mutator mutator;
	protected Penalty penalty;
	protected Schedule schedule;
	protected Random random = new Random();
	protected SolverStats stats = new SolverStats();

	@Override
	protected void initialize(final SolverConfiguration config) {
		constraints = config.getConstraints();
		log.info("Using constraints '{}'", constraints);

		mutator = config.getMutator();
		log.info("Using mutator '{}'", mutator);

		penalty = config.getPenalty();
		log.info("Using penalty '{}'", penalty);

		schedule = config.getSchedule();
		log.info("Using schedule '{}'", schedule);

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
		context.put(SolverStats.class, stats);
	}

	@Override
	protected void solve(final Solution initial) {
		Solution current = initial;

		// get our initial temperature and score
		double temp = schedule.getInitial();
		initial.setScore(penalty.score(initial));

		SolutionGenerator generator = new SolutionGenerator(context, mutator, constraints, 20);
		generator.setCurrent(initial);
		generator.start();

		// initialize the listeners
		started(initial);

		try {
			// anneal
			while (temp > 0) {
				// get a new potential solution
				Solution next = generator.getNext();

				// score this solution
				next.setScore(penalty.score(next));
				stats.scored++;

				// save as best if the penalty is less
				if (updateBest(next)) {
					stats.best = getBest().getScore();
					stats.constraints = constraints.isValid(getBest());
				}

				// notify listeners
				for (Listener l : listeners) {
					l.tried(temp, next, getBest());
				}

				// accept the new solution if it is better than the current
				// or randomly based on score and temperature
				if ((next.getScore() < current.getScore())
						|| (Math.exp(-(next.getScore() - current.getScore()) / temp) > random.nextDouble())) {
					current = next;
					generator.setCurrent(current);
				}

				temp = schedule.next(current);
				stats.temperature = temp;
				stats.elapsed = TimerUtils.getCounter();
			}
		} catch (Exception e) {
			handleError(e);
		}

		// clean up
		stopped(getBest());
	}
}
