package org.andrill.conop.core;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RejectedExecutionException;

import org.andrill.conop.core.constraints.ConstraintChecker;
import org.andrill.conop.core.listeners.Listener;
import org.andrill.conop.core.listeners.Listener.Mode;
import org.andrill.conop.core.mutators.MutationStrategy;
import org.andrill.conop.core.objectives.ObjectiveFunction;
import org.andrill.conop.core.schedules.CoolingSchedule;

import com.google.common.collect.Lists;

/**
 * A Java implementation of simulated annealing.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CONOP {
	private static final DecimalFormat D = new DecimalFormat("0.00");
	public static final String VERSION = "0.9.0";

	public static void main(final String[] args) {
		// load the simulation configuration
		Simulation simulation = new Simulation(new File(args[0]));

		// set/overried the initial solution on the config
		if (args.length > 1) {
			simulation.setProperty("initial", args[1]);
		}

		Run run = simulation.getRun();
		Solution initial = simulation.getInitialSolution();

		// create our CONOP object
		final CONOP conop = new CONOP(simulation.getConstraints(), simulation.getMutator(), simulation
				.getObjectiveFunction(), simulation.getSchedule(), simulation.getListeners());
		conop.filterMode(Mode.TUI);

		// find the optimal placement
		long start = System.currentTimeMillis();

		try {
			// run the simulation
			Solution solution = conop.solve(run, initial);

			long elapsed = (System.currentTimeMillis() - start) / 60000;
			System.out.println("Simulation completed after " + elapsed + " minutes.  Final score: "
					+ D.format(solution.getScore()) + "                                ");
		} catch (RuntimeException e) {
			Throwable cause = e;
			while (cause.getCause() != null) {
				cause = cause.getCause();
			}
			long elapsed = (System.currentTimeMillis() - start) / 60000;
			System.out.println("Simulation aborted after " + elapsed + " minutes: " + cause.getMessage()
					+ "                                ");
		}
		System.exit(0);
	}

	protected final ConstraintChecker constraints;
	protected final Set<Listener> listeners;
	protected final MutationStrategy mutator;
	protected final ObjectiveFunction objective;
	protected final Random random = new Random();
	protected final CoolingSchedule schedule;
	protected Solution best = null;
	protected boolean stopped = false;

	/**
	 * Create a new Constrained Optimization (CONOP) solver.
	 * 
	 * @param constraints the constraints checker.
	 * @param mutator the mutation strategy.
	 * @param objective the objective function.
	 * @param schedule the cooling schedule.
	 * @param listeners the list of listeners or null.
	 */
	public CONOP(final ConstraintChecker constraints, final MutationStrategy mutator,
			final ObjectiveFunction objective, final CoolingSchedule schedule, final List<Listener> listeners) {
		this.constraints = constraints;
		this.mutator = mutator;
		this.objective = objective;
		this.schedule = schedule;
		this.listeners = new CopyOnWriteArraySet<Listener>();

		// check for listeners
		if (constraints instanceof Listener) {
			this.listeners.add((Listener) constraints);
		}
		if (mutator instanceof Listener) {
			this.listeners.add((Listener) mutator);
		}
		if (objective instanceof Listener) {
			this.listeners.add((Listener) objective);
		}
		if (schedule instanceof Listener) {
			this.listeners.add((Listener) schedule);
		}
		if (listeners != null) {
			for (Listener l : listeners) {
				this.listeners.add(l);
			}
		}
	}

	/**
	 * Create a new Constrained Optimization (CONOP) solver.
	 * 
	 * @param simulation the simulation.
	 */
	public CONOP(final Simulation simulation) {
		this(simulation.getConstraints(), simulation.getMutator(), simulation.getObjectiveFunction(), simulation
				.getSchedule(), simulation.getListeners());
	}

	/**
	 * Add a new listener.
	 * 
	 * @param l
	 *            the listener.
	 */
	public void addListener(final Listener l) {
		listeners.add(l);
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
	 * Filters out any listeners that aren't declared as Listener.Mode.ANY or
	 * the specified mode.
	 * 
	 * @param mode
	 *            the mode.
	 */
	public void filterMode(final Mode mode) {
		List<Listener> remove = Lists.newArrayList();
		for (Listener l : listeners) {
			if ((l.getMode() != Mode.ANY) && (l.getMode() != mode)) {
				remove.add(l);
			}
		}
		listeners.removeAll(remove);
	}

	/**
	 * Remove a listener.
	 * 
	 * @param l
	 *            the listener.
	 */
	public void removeListener(final Listener l) {
		listeners.remove(l);
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
		initial.setScore(objective.score(initial));

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
				next.setScore(objective.score(next));

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

				// get our next temperature
				if (best.getScore() == 0) {
					throw new RuntimeException("Score reached 0");
				}
				temp = schedule.next(current);
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
			for (Listener l : listeners) {
				l.stopped(solution);
			}
		}
	}
}
