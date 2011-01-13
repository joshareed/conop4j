package org.andrill.conop4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.andrill.conop4j.constraints.ConstraintChecker;
import org.andrill.conop4j.constraints.EventChecker;
import org.andrill.conop4j.constraints.NullChecker;
import org.andrill.conop4j.listeners.Listener;
import org.andrill.conop4j.listeners.ProgressListener;
import org.andrill.conop4j.listeners.RanksListener;
import org.andrill.conop4j.listeners.SnapshotListener;
import org.andrill.conop4j.mutation.ConstrainedMutator;
import org.andrill.conop4j.mutation.MulticastSharedMutator;
import org.andrill.conop4j.mutation.MutationStrategy;
import org.andrill.conop4j.mutation.RandomMutator;
import org.andrill.conop4j.objective.ObjectiveFunction;
import org.andrill.conop4j.objective.ParallelPlacementPenalty;
import org.andrill.conop4j.objective.PlacementPenalty;
import org.andrill.conop4j.objective.SectionPlacement;
import org.andrill.conop4j.schedule.CoolingSchedule;
import org.andrill.conop4j.schedule.ExponentialSchedule;
import org.andrill.conop4j.schedule.LinearSchedule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Reads a simulation configuration from a properties file.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Simulation {
	private static final DecimalFormat D = new DecimalFormat("0.00");

	/**
	 * Gets a new file using the specified base filename.
	 * 
	 * @param file
	 *            the base filename.
	 * @return the file.
	 */
	public static File getFile(final String file) {
		File f = new File(file);
		int i = 0;
		while (f.exists()) {
			i++;
			int j = file.indexOf('.');
			if (j < 0) {
				f = new File(file + i);
			} else {
				f = new File(file.substring(0, j) + i + file.substring(j));
			}
		}
		return f;
	}

	/**
	 * Runs a standard simulation.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(final String[] args) {
		// load the simulation configuration
		Simulation config = new Simulation(new File(args[0]));
		Run run = config.getRun();
		RanksListener ranks = new RanksListener();
		ProgressListener progress = new ProgressListener();
		SnapshotListener snapshot = new SnapshotListener();

		// find the optimal placement
		long start = System.currentTimeMillis();
		Solution solution = runSimulation(config, run, Solution.initial(run), ranks, progress, snapshot);
		long elapsed = (System.currentTimeMillis() - start) / 60000;
		System.out.println("Elapsed time: " + elapsed + " minutes.  Final score: " + D.format(solution.getScore()));

		// write out the solution and ranks
		writeResults(solution, ranks);
	}

	/**
	 * Runs a simulation.
	 * 
	 * @param simulation
	 *            the simulation.
	 * @param run
	 *            the run.
	 * @param initial
	 *            the initial solution.
	 * @param listeners
	 *            the listeners.
	 * @return the solution.
	 */
	public static Solution runSimulation(final Simulation simulation, final Run run, final Solution initial,
			final Listener... listeners) {
		int serial = simulation.getSerialRunCount();
		int parallel = simulation.getParallelProcessCount();
		ExecutorService pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors
				.newFixedThreadPool(parallel));

		Solution solution = initial;
		for (int i = 0; i < serial; i++) {
			if (serial > 1) {
				System.out.println("Starting iteration " + (i + 1) + "/" + serial);
			}
			List<Future<Solution>> tasks = Lists.newArrayList();
			final Solution next = new Solution(solution.getRun(), solution.getEvents());
			if (parallel > 1) {
				System.out.println("Starting a swarm of " + parallel + " CONOP processes");
			}
			for (int j = 0; j < parallel; j++) {
				// create our CONOP process
				final CONOP conop = new CONOP(simulation.getConstraints(), simulation.getMutator(),
						simulation.getObjectiveFunction(), simulation.getSchedule());

				// add our listeners
				for (Listener l : listeners) {
					if ((j == 0) || (l instanceof RanksListener)) {
						conop.addListener(l);
					}
				}

				// start a parallel process
				tasks.add(pool.submit(new Callable<Solution>() {
					@Override
					public Solution call() throws Exception {
						return conop.solve(run, new Solution(run, next.getEvents()));
					}
				}));
			}

			// get our best solution from the parallel tasks
			Solution best = null;
			for (Future<Solution> f : tasks) {
				try {
					Solution s = f.get();
					if ((best == null) || (s.getScore() < best.getScore())) {
						best = s;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			solution = best;
		}

		// run the endgame simulation
		File endgame = simulation.getEndgame();
		if (endgame == null) {
			return solution;
		} else {
			System.out.println("Starting endgame scenario: " + endgame.getName());
			return runSimulation(new Simulation(endgame), run, solution, listeners);
		}
	}

	/**
	 * Writes the results of a simulation.
	 * 
	 * @param file
	 *            the file.
	 * @param solution
	 *            the solution.
	 * @param ranks
	 *            the ranks.
	 */
	public static void writeResults(final File file, final Solution solution, final RanksListener ranks) {
		BufferedWriter writer = null;
		try {
			Run run = solution.getRun();
			Map<Section, SectionPlacement> placements = Maps.newHashMap();

			// open our writer
			writer = new BufferedWriter(new FileWriter(file));
			writer.write("Event\tRank");
			if (ranks != null) {
				writer.write("\tMin Rank\tMax Rank");
			}
			for (Section s : run.getSections()) {
				writer.write("\t" + s.getName() + " (O)\t" + s.getName() + " (P)");
				placements.put(s, new SectionPlacement(s));
			}
			writer.write("\n");

			// build our placements
			double score = 0;
			for (final Section s : solution.getRun().getSections()) {
				SectionPlacement placement = placements.get(s);
				for (Event e : solution.getEvents()) {
					placement.place(e);
				}
				score += placement.getPenalty();
			}

			int total = solution.getEvents().size();
			for (int i = 0; i < total; i++) {
				Event e = solution.getEvent(i);
				writer.write("'" + e + "'\t" + (total - i));
				if (ranks != null) {
					writer.write("\t" + ranks.getMin(e) + "\t" + ranks.getMax(e));
				}
				for (Section s : run.getSections()) {
					writer.write("\t");
					Observation o = s.getObservation(e);
					if (o != null) {
						writer.write("" + o.getLevel());
					}
					writer.write("\t" + placements.get(s).getPlacement(e));
				}
				writer.write("\n");
			}

			writer.write("Total");
			writer.write("\t" + D.format(score));
			if (ranks != null) {
				writer.write("\t\t");
			}
			for (Section s : run.getSections()) {
				writer.write("\t\t" + D.format(placements.get(s).getPenalty()));
			}
			writer.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Closeables.closeQuietly(writer);
		}
	}

	/**
	 * Writes the results to solution.csv.
	 * 
	 * @param solution
	 *            the solution.
	 * @param ranks
	 *            the ranks.
	 */
	public static void writeResults(final Solution solution, final RanksListener ranks) {
		writeResults(getFile("solution.csv"), solution, ranks);
	}

	protected final File directory;
	protected final Properties properties;
	protected Run run;

	/**
	 * Create a new Simulation.
	 * 
	 * @param file
	 *            the file.
	 */
	public Simulation(final File file) {
		properties = new Properties();
		directory = file.getParentFile();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			properties.load(fis);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Closeables.closeQuietly(fis);
		}
	}

	/**
	 * Gets the configured {@link ConstraintChecker}.
	 * 
	 * <pre>
	 * Key: constraints 
	 * Values: 
	 * 		null  - {@link NullChecker} (default) 
	 * 		event - {@link EventChecker}
	 * </pre>
	 * 
	 * @return the configured ConstraintChecker.
	 */
	public ConstraintChecker getConstraints() {
		String constraints = properties.getProperty("constraints", "null");
		if ("null".equalsIgnoreCase(constraints)) {
			System.out.println("Constraints: Null Checker");
			return new NullChecker();
		} else if ("event".equalsIgnoreCase(constraints)) {
			System.out.println("Constraints: Event Checker");
			return new EventChecker();
		} else {
			Class<?> clazz;
			try {
				clazz = Class.forName(constraints);
				if (ConstraintChecker.class.isAssignableFrom(clazz)) {
					return (ConstraintChecker) clazz.newInstance();
				} else {
					System.out.println("Class " + constraints + " does not implement the "
							+ ConstraintChecker.class.getName() + " interface");
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Unable to find class " + constraints + ".  Check your classpath.");
			} catch (InstantiationException e) {
				System.out.println("Unable to find class " + constraints + ".  Check your classpath.");
			} catch (IllegalAccessException e) {
				System.out.println("Unable to find class " + constraints + ".  Check your classpath.");
			}
			System.out.println("Unknown constraints '" + constraints + "'.  Defaulting to NullChecker.");
			return new NullChecker();
		}
	}

	/**
	 * Gets the endgame simulation.
	 * 
	 * @return the endgame simulation.
	 */
	public File getEndgame() {
		String endgame = properties.getProperty("endgame");
		if (endgame != null) {
			File f = new File(directory, endgame);
			if (f.exists()) {
				return f;
			}
		}
		return null;
	}

	/**
	 * Gets the configured {@link MutationStrategy}.
	 * 
	 * <pre>
	 * Key: mutator
	 * Values:
	 * 		random      - {@link RandomMutator} (default)
	 * 		constrained - {@link ConstrainedMutator}
	 * </pre>
	 * 
	 * @return the configured MutationStrategy.
	 */
	public MutationStrategy getMutator() {
		// get our mutator
		String mutator = properties.getProperty("mutator", "random");
		MutationStrategy base = null;
		if ("random".equalsIgnoreCase(mutator)) {
			System.out.println("Mutator: Random");
			base = new RandomMutator();
		} else if ("constrained".equalsIgnoreCase(mutator)) {
			System.out.println("Mutator: Constrained");
			base = new ConstrainedMutator();
		} else {
			Class<?> clazz;
			try {
				clazz = Class.forName(mutator);
				if (MutationStrategy.class.isAssignableFrom(clazz)) {
					base = (MutationStrategy) clazz.newInstance();
				} else {
					System.out.println("Class " + mutator + " does not implement the "
							+ MutationStrategy.class.getName() + " interface");
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Unable to find class " + mutator + ".  Check your classpath.");
			} catch (InstantiationException e) {
				System.out.println("Unable to find class " + mutator + ".  Check your classpath.");
			} catch (IllegalAccessException e) {
				System.out.println("Unable to find class " + mutator + ".  Check your classpath.");
			}
			System.out.println("Unknown mutator '" + mutator + "'.  Defaulting to RandomMutator.");
			base = new RandomMutator();
		}

		// check for multicast
		boolean multicast = Boolean.parseBoolean(properties.getProperty("multicast", "false"));
		double factor = Double.parseDouble(properties.getProperty("multicast.factor", "0.75"));
		if (multicast) {
			System.out.println("Multicasting: true");
			return new MulticastSharedMutator(getRun(), base, factor);
		} else {
			return base;
		}
	}

	/**
	 * Gets the configured {@link ObjectiveFunction}.
	 * 
	 * <pre>
	 * Key: score
	 * Values:
	 * 		experimental - {@link PlacementPenalty} (default)
	 * 		parallel-experimental  - {@link ParallelPlacementPenalty}
	 * </pre>
	 * 
	 * @return the configured ObjectiveFunction.
	 */
	public ObjectiveFunction getObjectiveFunction() {
		String score = properties.getProperty("objective", "experimental");

		if ("placement".equalsIgnoreCase(score)) {
			int processors = Integer.parseInt(properties.getProperty("processors", "1"));
			if (processors == 1) {
				System.out.println("Objective: Placement");
				return new PlacementPenalty();
			} else {
				System.out.println("Objective: Parallel Placement [" + processors + "]");
				return new ParallelPlacementPenalty(processors);
			}
		} else {
			Class<?> clazz;
			try {
				clazz = Class.forName(score);
				if (ObjectiveFunction.class.isAssignableFrom(clazz)) {
					return (ObjectiveFunction) clazz.newInstance();
				} else {
					System.out.println("Class " + score + " does not implement the "
							+ ObjectiveFunction.class.getName() + " interface");
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Unable to find class " + score + ".  Check your classpath.");
			} catch (InstantiationException e) {
				System.out.println("Unable to find class " + score + ".  Check your classpath.");
			} catch (IllegalAccessException e) {
				System.out.println("Unable to find class " + score + ".  Check your classpath.");
			}
			System.out.println("Unknown objective function '" + score + "'.  Defaulting to ExperimentalPlacement.");
			return new PlacementPenalty();
		}
	}

	/**
	 * Gets the parallel process count.
	 * 
	 * @return the parallel count.
	 */
	public int getParallelProcessCount() {
		return Integer.parseInt(properties.getProperty("swarm", "1"));
	}

	/**
	 * Gets the run for this simulation.
	 * 
	 * @return the run.
	 */
	public Run getRun() {
		if (run == null) {
			String data = properties.getProperty("data", ".");
			boolean overrideWeights = !Boolean.getBoolean(properties.getProperty("weights", "true"));
			File runDir = new File(directory, data);
			run = Run.loadCONOP9Run(runDir, overrideWeights);
		}
		return run;
	}

	/**
	 * Gets the configured {@link CoolingSchedule}.
	 * 
	 * <pre>
	 * Key: schedule
	 * Values:
	 * 		exponential - {@link ExponentialSchedule} (default)
	 * 		linear      - {@link LinearSchedule}
	 * 
	 * Additional Keys:
	 * 		schedule.initial    - initial temperature (double)
	 * 		schedule.delta      - temperature delta (double)
	 * 		schedule.stepsPer   - steps per temperature (long)
	 * 		schedule.noProgress - stop after steps with no progress (long)
	 * </pre>
	 * 
	 * @return the configured CoolingSchedule.
	 */
	public CoolingSchedule getSchedule() {
		String schedule = properties.getProperty("schedule", "exponential");
		double initial = Double.parseDouble(properties.getProperty("schedule.initial", "1000"));
		double delta = Double.parseDouble(properties.getProperty("schedule.delta", "0.01"));
		long stepsPer = Long.parseLong(properties.getProperty("schedule.stepsPer", "100"));
		long noProgress = Long.parseLong(properties.getProperty("schedule.noProgress", "1000000"));

		if ("exponential".equalsIgnoreCase(schedule)) {
			System.out.println("Schedule: Exponential");
			return new ExponentialSchedule(initial, delta, stepsPer, noProgress);
		} else if ("linear".equalsIgnoreCase(schedule)) {
			System.out.println("Schedule: Linear");
			return new LinearSchedule(initial, stepsPer, delta);
		} else {
			Class<?> clazz;
			try {
				clazz = Class.forName(schedule);
				if (CoolingSchedule.class.isAssignableFrom(clazz)) {
					return (CoolingSchedule) clazz.newInstance();
				} else {
					System.out.println("Class " + schedule + " does not implement the "
							+ CoolingSchedule.class.getName() + " interface");
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Unable to find class " + schedule + ".  Check your classpath.");
			} catch (InstantiationException e) {
				System.out.println("Unable to find class " + schedule + ".  Check your classpath.");
			} catch (IllegalAccessException e) {
				System.out.println("Unable to find class " + schedule + ".  Check your classpath.");
			}
			System.out.println("Unknown cooling schedule '" + schedule + "'.  Defaulting to Exponential.");
			return new ExponentialSchedule(initial, delta, stepsPer, noProgress);
		}
	}

	/**
	 * Gets the serial run count.
	 * 
	 * @return the serial count.
	 */
	public int getSerialRunCount() {
		return Integer.parseInt(properties.getProperty("series", "1"));
	}
}
