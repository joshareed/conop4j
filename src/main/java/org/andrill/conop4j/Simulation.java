package org.andrill.conop4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Properties;

import org.andrill.conop4j.constraints.ConstraintChecker;
import org.andrill.conop4j.constraints.EventChecker;
import org.andrill.conop4j.constraints.NullChecker;
import org.andrill.conop4j.listeners.ProgressListener;
import org.andrill.conop4j.listeners.RanksListener;
import org.andrill.conop4j.listeners.SnapshotListener;
import org.andrill.conop4j.mutation.ConstrainedMutator;
import org.andrill.conop4j.mutation.MutationStrategy;
import org.andrill.conop4j.mutation.RandomMutator;
import org.andrill.conop4j.objective.ObjectiveFunction;
import org.andrill.conop4j.objective.ParallelPlacementPenalty;
import org.andrill.conop4j.objective.PlacementPenalty;
import org.andrill.conop4j.objective.SectionPlacement;
import org.andrill.conop4j.schedule.CoolingSchedule;
import org.andrill.conop4j.schedule.ExponentialSchedule;
import org.andrill.conop4j.schedule.LinearSchedule;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Reads a simulation configuration from a properties file.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Simulation {
	private static final DecimalFormat D = new DecimalFormat("0.00");

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

	public static void main(final String[] args) throws Exception {
		// load the simulation configuration
		Simulation config = new Simulation(new File(args[0]));
		Run run = config.getRun();

		// setup CONOP4J
		CONOP conop = new CONOP(config.getConstraints(), config.getMutator(), config.getObjectiveFunction(),
				config.getSchedule());

		// add a listener to print out progress
		conop.addListener(new ProgressListener());
		conop.addListener(new SnapshotListener());

		// add a listener to collect event ranks
		RanksListener ranks = new RanksListener();
		conop.addListener(ranks);

		// find the optimal placement
		long start = System.currentTimeMillis();
		Solution solution = conop.solve(run, Solution.initial(run));
		long elapsed = (System.currentTimeMillis() - start) / 60000;
		System.out.println("Elapsed time: " + elapsed + " minutes.  Final score: " + D.format(solution.getScore()));

		// write out the solution and ranks
		writeResults(solution, ranks);
	}

	public static void writeResults(final Solution solution, final RanksListener ranks) {
		BufferedWriter writer = null;
		try {
			Run run = solution.getRun();
			Map<Section, SectionPlacement> placements = Maps.newHashMap();

			// open our writer
			writer = new BufferedWriter(new FileWriter(getFile("solution.csv")));
			writer.write("Event\tRank\tMin Rank\tMax Rank");
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
				writer.write("'" + e + "'\t" + (total - i) + "\t" + ranks.getMin(e) + "\t" + ranks.getMax(e));
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
			writer.write("\t" + score + "\t\t");
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

	protected final File directory;
	protected final Properties properties;

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
		String constraints = properties.getProperty("constraints", "null").toLowerCase();
		if ("null".equals(constraints)) {
			System.out.println("Constraints: Null Checker");
			return new NullChecker();
		} else if ("event".equals(constraints)) {
			System.out.println("Constraints: Event Checker");
			return new EventChecker();
		} else {
			System.out.println("Unknown constraints '" + constraints + "'.  Defaulting to NullChecker.");
			return new NullChecker();
		}
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
		String mutator = properties.getProperty("mutator", "random").toLowerCase();
		if ("random".equals(mutator)) {
			System.out.println("Mutator: Random");
			return new RandomMutator();
		} else if ("constrained".equals(mutator)) {
			System.out.println("Mutator: Constrained");
			return new ConstrainedMutator();
		} else {
			System.out.println("Mutator: Random");
			return new RandomMutator();
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
		String score = properties.getProperty("objective", "experimental").toLowerCase();

		if ("experimental".equals(score)) {
			System.out.println("Objective: Experimental");
			return new PlacementPenalty();
		} else if ("parallel-experimental".equals(score)) {
			int processors = Integer.parseInt(properties.getProperty("processors", "2"));
			System.out.println("Objective: Parallel Experimental [" + processors + "]");
			return new ParallelPlacementPenalty(processors);
		} else {
			System.out.println("Objective: Experimental");
			return new PlacementPenalty();
		}
	}

	public Run getRun() {
		String data = properties.getProperty("data", ".");
		boolean overrideWeights = !Boolean.getBoolean(properties.getProperty("weights", "true"));
		File runDir = new File(directory, data);
		return Run.loadCONOP9Run(runDir, overrideWeights);
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
		String schedule = properties.getProperty("schedule", "exponential").toLowerCase();
		double initial = Double.parseDouble(properties.getProperty("schedule.initial", "1000"));
		double delta = Double.parseDouble(properties.getProperty("schedule.delta", "0.01"));
		long stepsPer = Long.parseLong(properties.getProperty("schedule.stepsPer", "100"));
		long noProgress = Long.parseLong(properties.getProperty("schedule.noProgress", "1000000"));

		if ("exponential".equals(schedule)) {
			System.out.println("Schedule: Exponential");
			return new ExponentialSchedule(initial, delta, stepsPer, noProgress);
		} else if ("linear".equals(schedule)) {
			System.out.println("Schedule: Linear");
			return new LinearSchedule(initial, stepsPer, delta);
		} else {
			System.out.println("Schedule: Exponential");
			return new ExponentialSchedule(initial, delta, stepsPer, noProgress);
		}
	}
}
