package org.andrill.conop.search;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

import org.andrill.conop.search.constraints.ConstraintChecker;
import org.andrill.conop.search.listeners.Listener;
import org.andrill.conop.search.mutators.MutationStrategy;
import org.andrill.conop.search.objectives.ObjectiveFunction;
import org.andrill.conop.search.schedules.CoolingSchedule;

public class QNOP {
	private class ScorerThread extends Thread {
		private ObjectiveFunction objective;

		private ScorerThread(final ObjectiveFunction objective) {
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
					e.printStackTrace();
				}
			}
		}
	}

	public static final String VERSION = "0.9.0";
	private static final DecimalFormat D = new DecimalFormat("0.00");

	public static void main(final String[] args) {
		// load the simulation configuration
		Simulation simulation = new Simulation(new File(args[0]));

		// set/override the processors set on the config
		simulation.setProperty("processors", "1");

		// set/overried the initial solution on the config
		if (args.length > 1) {
			simulation.setProperty("initial", args[1]);
		}

		Run run = simulation.getRun();
		Solution initial = simulation.getInitialSolution();

		// create our QNOP object
		QNOP qnop = new QNOP(simulation);

		long start = System.currentTimeMillis();
		try {
			// run the simulation
			Solution solution = qnop.solve(run, initial);

			long elapsed = (System.currentTimeMillis() - start) / 60000;
			System.out.println("Simulation completed after " + elapsed + " minutes.  Final score: "
					+ D.format(solution.getScore()));
		} catch (RuntimeException e) {
			Throwable cause = e;
			while (cause.getCause() != null) {
				cause = cause.getCause();
			}
			long elapsed = (System.currentTimeMillis() - start) / 60000;
			System.out.println("Simulation aborted after " + elapsed + " minutes: " + cause.getMessage());
		}
		System.exit(0);
	}

	protected final LinkedBlockingQueue<Solution> work;
	protected final LinkedBlockingQueue<Solution> complete;
	protected boolean stopped = false;
	protected Solution best = null;
	protected final ConstraintChecker constraints;
	protected final Set<Listener> listeners;
	protected final MutationStrategy mutator;
	protected final Set<ScorerThread> scorers;
	protected final Random random = new Random();
	protected final CoolingSchedule schedule;

	/**
	 * Create a new queue-based Constrained Optimization (CONOP) solver.
	 * 
	 * @param simulation the simulation.
	 */
	public QNOP(final Simulation simulation) {
		int procs = Runtime.getRuntime().availableProcessors();
		work = new LinkedBlockingQueue<>(procs + 1);
		complete = new LinkedBlockingQueue<>(procs + 1);

		// setup our listeners
		this.listeners = new CopyOnWriteArraySet<Listener>();

		// save our important components
		this.constraints = simulation.getConstraints();
		this.mutator = simulation.getMutator();
		this.schedule = simulation.getSchedule();

		// create our scorer threads
		scorers = new HashSet<>();
		for (int i = 0; i < procs; i++) {
			ObjectiveFunction objective = simulation.getObjectiveFunction();
			scorers.add(new ScorerThread(objective));
			if (objective instanceof Listener) {
				this.listeners.add((Listener) objective);
			}
		}

		// check for listeners
		if (constraints instanceof Listener) {
			this.listeners.add((Listener) constraints);
		}
		if (mutator instanceof Listener) {
			this.listeners.add((Listener) mutator);
		}
		if (schedule instanceof Listener) {
			this.listeners.add((Listener) schedule);
		}
		this.listeners.addAll(simulation.getListeners());
	}

	private void addShutdownHook() {
		// add our shutdown hook so we can make an effort to run stopped
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopped(best);
			}
		});
	}

	/**
	 * Solve the constrained optimization problem.
	 * 
	 * @param run
	 *            the run data.
	 * @param initial
	 *            the initial solution.
	 * @return the best solution.
	 */
	public Solution solve(final Run run, final Solution initial) throws AbortedException {
		addShutdownHook();

		best = initial;
		Solution current = initial;

		// get our initial temperature and score
		double temp = schedule.getInitial();
		initial.setScore(Double.MAX_VALUE);
		started(initial);

		// fill the work queue
		for (int i = 0; i < scorers.size(); i++) {
			Solution next = mutator.mutate(current);
			while (!constraints.isValid(next)) {
				next = mutator.mutate(current);
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

				// save as best if the penalty is less
				if (next.getScore() < best.getScore()) {
					best = next;
				}

				// accept the new solution if it is better than the current
				// or randomly based on score and temperature
				if ((next.getScore() < current.getScore())
						|| (Math.exp(-(next.getScore() - current.getScore()) / temp) > random.nextDouble())) {
					current = next;
				}

				// notify listeners
				for (Listener l : listeners) {
					l.tried(temp, current, best);
				}

				// check if done
				if (best.getScore() == 0) {
					throw new RuntimeException("Score reached 0");
				}

				// get our next temperature
				temp = schedule.next(current);

				// get a new solution that satisfies the constraints
				next = mutator.mutate(current);
				while (!constraints.isValid(next)) {
					next = mutator.mutate(current);
				}
				work.put(next);
			}
		} catch (AbortedException e) {
			stopped(null);
			throw e;
		} catch (Exception e) {
			if ((e instanceof InterruptedException) || (e instanceof RejectedExecutionException)) {
				System.out.println("Halted: user interrupt");
			} else {
				System.out.println("Halted: " + e.getMessage());
			}
		}

		// clean up
		stopped(best);
		return best;
	}

	protected void started(final Solution initial) {
		for (Listener l : listeners) {
			l.started(initial);
		}
	}

	protected void stopped(final Solution solution) {
		if (!stopped) {
			stopped = true;
			for (ScorerThread thread : scorers) {
				thread.interrupt();
			}
			for (Listener l : listeners) {
				l.stopped(solution);
			}
		}
	}
}
