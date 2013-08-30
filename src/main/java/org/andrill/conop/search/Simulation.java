package org.andrill.conop.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.andrill.conop.search.constraints.ConstraintChecker;
import org.andrill.conop.search.constraints.EventChecker;
import org.andrill.conop.search.constraints.NullChecker;
import org.andrill.conop.search.listeners.*;
import org.andrill.conop.search.mutators.AnnealingMutator;
import org.andrill.conop.search.mutators.ConstrainedMutator;
import org.andrill.conop.search.mutators.MutationStrategy;
import org.andrill.conop.search.mutators.RandomMutator;
import org.andrill.conop.search.objectives.MatrixPenalty;
import org.andrill.conop.search.objectives.ObjectiveFunction;
import org.andrill.conop.search.objectives.PlacementPenalty;
import org.andrill.conop.search.objectives.RelativeOrderingPenalty;
import org.andrill.conop.search.schedules.CoolingSchedule;
import org.andrill.conop.search.schedules.ExponentialSchedule;
import org.andrill.conop.search.schedules.LinearSchedule;
import org.andrill.conop.search.schedules.TemperingSchedule;

import com.google.common.collect.Lists;

/**
 * Reads a simulation configuration from a properties file.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Simulation {
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
		try (FileInputStream fis = new FileInputStream(file)) {
			properties.load(fis);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
	@SuppressWarnings("serial")
	public ConstraintChecker getConstraints() {
		return lookup(properties.getProperty("constraints", "default"), ConstraintChecker.class,
				new HashMap<String, ConstraintChecker>() {
					{
						put("null", new NullChecker());
						put("event", new EventChecker());
						put("default", new NullChecker());
					}
				});
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
	 * Gets the initial solution specified by this simulation.
	 * 
	 * @return the initial solution.
	 */
	public Solution getInitialSolution() {
		// support loading from a file
		String path = properties.getProperty("initial");
		Solution initial = null;
		if (path != null) {
			initial = Solution.parse(run, new File(directory, path));
		}
		if (initial != null) {
			return initial;
		} else {
			return Solution.initial(run);
		}
	}

	/**
	 * Get the list of listeners.
	 * 
	 * @return the list of listeners.
	 */
	@SuppressWarnings("serial")
	public List<Listener> getListeners() {
		Map<String, Listener> map = new HashMap<String, Listener>() {
			{
				put("console", new ConsoleProgressListener());
				put("snapshot", new SnapshotListener());
				put("stopping", new StoppingListener());
				put("conopweb", new ConopWebProgressListener());
			}
		};

		// lookup our listeners
		List<Listener> listeners = Lists.newArrayList();
		for (String name : properties.getProperty("listeners", "").split(",")) {
			Listener l = lookup(name, Listener.class, map);
			if (l != null) {
				listeners.add(l);
			}
		}
		return listeners;
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
	@SuppressWarnings("serial")
	public MutationStrategy getMutator() {
		return lookup(properties.getProperty("mutator", "default"), MutationStrategy.class,
				new HashMap<String, MutationStrategy>() {
					{
						put("random", new RandomMutator());
						put("constrained", new ConstrainedMutator());
						put("annealing", new AnnealingMutator());
						put("default", new RandomMutator());
					}
				});
	}

	/**
	 * Gets the configured {@link ObjectiveFunction}.
	 * 
	 * <pre>
	 * Key: score
	 * Values:
	 * 		placement - {@link PlacementPenalty} (default)
	 * 		matrix    - {@link MatrixPenalty}
	 * </pre>
	 * 
	 * @return the configured ObjectiveFunction.
	 */
	@SuppressWarnings("serial")
	public ObjectiveFunction getObjectiveFunction() {
		return lookup(properties.getProperty("objective", "default"), ObjectiveFunction.class,
				new HashMap<String, ObjectiveFunction>() {
					{
						put("placement", new PlacementPenalty());
						put("matrix", new MatrixPenalty());
						put("ordering", new RelativeOrderingPenalty());
						put("default", new PlacementPenalty());
					}
				});
	}

	/**
	 * Get an arbitrary property defined on the simulation.
	 * 
	 * @param key the key.
	 * @return the value or null if not set.
	 */
	public String getProperty(final String key) {
		return properties.getProperty(key);
	}

	/**
	 * Get an arbitrary property defined on the simulation.
	 * 
	 * @param key the key.
	 * @param defaultValue the default value if not set.
	 * @return the value or the defaultValue.
	 */
	public String getProperty(final String key, final String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * Gets the run for this simulation.
	 * 
	 * @return the run.
	 */
	public Run getRun() {
		if (run == null) {
			boolean overrideWeights = !Boolean.getBoolean(properties.getProperty("weights", "true"));
			File runDir = new File(directory, properties.getProperty("data", "."));
			String sectionFile = properties.getProperty("data.sectionFile", "sections.sct");
			String eventFile = properties.getProperty("data.eventFile", "events.evt");
			String loadFile = properties.getProperty("data.loadFile", "loadfile.dat");

			run = Run.loadCONOP9Run(new File(runDir, sectionFile), new File(runDir, eventFile), new File(runDir,
					loadFile), overrideWeights);
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
	@SuppressWarnings("serial")
	public CoolingSchedule getSchedule() {
		return lookup(properties.getProperty("schedule", "default"), CoolingSchedule.class,
				new HashMap<String, CoolingSchedule>() {
					{
						put("exponential", new ExponentialSchedule());
						put("tempering", new TemperingSchedule());
						put("linear", new LinearSchedule());
						put("default", new ExponentialSchedule());
					}
				});
	}

	protected <E> E instantiate(final String name, final Class<E> type) {
		Class<?> clazz;
		try {
			clazz = Class.forName(name);
			if (type.isAssignableFrom(clazz)) {
				return type.cast(clazz.newInstance());
			} else {
				System.err.println("Class " + name + " does not implement the " + type.getName() + " interface");
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to find class " + name + ".  Check your classpath.");
		} catch (InstantiationException e) {
			System.err.println("Unable to find class " + name + ".  Check your classpath.");
		} catch (IllegalAccessException e) {
			System.err.println("Unable to find class " + name + ".  Check your classpath.");
		}
		return null;
	}

	protected <E> E lookup(final String key, final Class<E> type, final Map<String, E> map) {
		String name = key.trim();
		if ("".equals(name)) {
			return null;
		}
		E instance = null;
		if (map.containsKey(name)) {
			instance = map.get(name);
		} else {
			instance = instantiate(name, type);
		}
		if ((instance == null) && map.containsKey("default")) {
			instance = map.get("default");
			System.err.println("Unknown " + type.getName() + " '" + name + "', defaulting to " + instance);
		}
		if ((instance != null) && (instance instanceof Configurable)) {
			((Configurable) instance).configure(properties, run);
		}
		return instance;
	}

	/**
	 * Set a property on the simulation.
	 * 
	 * @param key the key.
	 * @param value the value.
	 */
	public void setProperty(final String key, final String value) {
		properties.setProperty(key, value);
	}
}
