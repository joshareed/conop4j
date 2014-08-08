package org.andrill.conop.core.internal;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

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

public class QueueSolver extends AbstractSolver {

	private class ScorerThread extends Thread {
		private final Penalty objective;

		private ScorerThread(final Penalty objective) {
			this.objective = objective;
		}

		@Override
		public void run() {
			while (!stopped) {
				try {
					Solution next = work.take();
					next.setScore(objective.score(next));
					complete.put(next);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}

	protected LinkedBlockingQueue<Solution> work;
	protected LinkedBlockingQueue<Solution> complete;
	protected Set<ScorerThread> scorers;
	protected Constraints constraints;
	protected Mutator mutator;
	protected Schedule schedule;
	protected Random random = new Random();
	protected SolverStats stats = new SolverStats();

	@Override
	protected void initialize(final SolverConfiguration config) {
		int procs = Runtime.getRuntime().availableProcessors();
		work = new LinkedBlockingQueue<>(procs + 1);
		complete = new LinkedBlockingQueue<>(procs + 1);

		// save our important components
		constraints = config.getConstraints();
		mutator = config.getMutator();
		schedule = config.getSchedule();
		setContext(constraints, mutator, schedule);

		// check for listeners
		if (constraints instanceof Listener) {
			addListener((Listener) constraints);
		}
		if (mutator instanceof Listener) {
			addListener((Listener) mutator);
		}
		if (schedule instanceof Listener) {
			addListener((Listener) schedule);
		}

		// create our scorer threads
		scorers = new HashSet<>();
		for (int i = 0; i < procs; i++) {
			Penalty penalty = config.getPenalty();
			setContext(penalty);
			scorers.add(new ScorerThread(penalty));
			if (penalty instanceof Listener) {
				addListener((Listener) penalty);
			}
		}

		// other listeners
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
		initial.setScore(Double.MAX_VALUE);

		started(initial);

		// fill the work queue
		for (int i = 0; i < scorers.size(); i++) {
			Solution next = mutator.mutate(current);
			stats.total++;
			while (!constraints.isValid(next)) {
				next = mutator.mutate(current);
				stats.skipped++;
				stats.total++;
			}
			try {
				work.put(next);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		// start all scorer threads
		for (ScorerThread thread : scorers) {
			thread.start();
		}

		try {
			// anneal
			while (temp > 0) {
				Solution next = complete.take();
				stats.scored++;

				// check if new best
				if (updateBest(next)) {
					stats.best = getBest().getScore();
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
				}

				// get our next temperature
				temp = schedule.next(current);
				stats.temperature = temp;
				stats.elapsed = TimerUtils.getCounter();

				// get a new solution that satisfies the constraints
				next = context.getNext();
				if (next == null) {
					next = mutator.mutate(current);
				}
				stats.total++;
				while (!constraints.isValid(next)) {
					next = mutator.mutate(current);
					stats.skipped++;
					stats.total++;
				}
				work.put(next);
			}
		} catch (Exception e) {
			handleError(e);
		}

		// clean up
		stopped(getBest());
	}

	@Override
	protected void stopped(final Solution solution) {
		for (ScorerThread thread : scorers) {
			thread.interrupt();
		}

		super.stopped(solution);
	}
}
