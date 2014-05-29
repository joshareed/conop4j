package org.andrill.conop.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.andrill.conop.core.constraints.ConstraintChecker;
import org.andrill.conop.core.constraints.EventChecker;
import org.andrill.conop.core.constraints.NullChecker;
import org.andrill.conop.core.listeners.ConsoleProgressListener;
import org.andrill.conop.core.listeners.Listener;
import org.andrill.conop.core.listeners.SnapshotListener;
import org.andrill.conop.core.listeners.StoppingListener;
import org.andrill.conop.core.mutators.AnnealingMutator;
import org.andrill.conop.core.mutators.ConstrainedMutator;
import org.andrill.conop.core.mutators.MethodicalMutator;
import org.andrill.conop.core.mutators.MutationStrategy;
import org.andrill.conop.core.mutators.RandomMutator;
import org.andrill.conop.core.objectives.MatrixPenalty;
import org.andrill.conop.core.objectives.MultiPenalty;
import org.andrill.conop.core.objectives.ObjectiveFunction;
import org.andrill.conop.core.objectives.PlacementPenalty;
import org.andrill.conop.core.objectives.RelativeOrderingPenalty;
import org.andrill.conop.core.schedules.CoolingSchedule;
import org.andrill.conop.core.schedules.ExponentialSchedule;
import org.andrill.conop.core.schedules.LinearSchedule;
import org.andrill.conop.core.schedules.TemperingSchedule;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Reads a simulation configuration from a properties file.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class Simulation {
	protected final File directory;
	protected final Properties properties;
	protected Run run;

	public static final Map<String, ObjectiveFunction> OBJECTIVES = Collections.unmodifiableMap(new HashMap<String, ObjectiveFunction>() {
		private static final long serialVersionUID = 1L;
		{
			put("placement", new PlacementPenalty());
			put("matrix", new MatrixPenalty());
			put("ordering", new RelativeOrderingPenalty());
			put("multi", new MultiPenalty());
			put("default", new PlacementPenalty());
		}
	});
	public static final Map<String, MutationStrategy> MUTATORS = Collections.unmodifiableMap(new HashMap<String, MutationStrategy>() {
		private static final long serialVersionUID = 1L;
		{
			put("random", new RandomMutator());
			put("constrained", new ConstrainedMutator());
			put("annealing", new AnnealingMutator());
			put("methodical", new MethodicalMutator());
			put("default", new RandomMutator());
		}
	});
	public static final Map<String, Listener> LISTENERS = Collections.unmodifiableMap(new HashMap<String, Listener>() {
		private static final long serialVersionUID = 1L;
		{
			put("console", new ConsoleProgressListener());
			put("snapshot", new SnapshotListener());
			put("stopping", new StoppingListener());
		}
	});
	public static final Map<String, ConstraintChecker> CONSTRAINTS = Collections.unmodifiableMap(new HashMap<String, ConstraintChecker>() {
		private static final long serialVersionUID = 1L;
		{
			put("null", new NullChecker());
			put("event", new EventChecker());
			put("default", new NullChecker());
		}
	});
	public static final Map<String, CoolingSchedule> SCHEDULES = Collections.unmodifiableMap(new HashMap<String, CoolingSchedule>() {
		private static final long serialVersionUID = 1L;
		{
			put("exponential", new ExponentialSchedule());
			put("tempering", new TemperingSchedule());
			put("linear", new LinearSchedule());
			put("default", new ExponentialSchedule());
		}
	});

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

	public Simulation(final Properties properties, final Run run) {
		this.properties = properties;
		this.directory = new File(".");
		this.run = run;
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
		return lookup(properties.getProperty("constraints", "default"), ConstraintChecker.class, CONSTRAINTS);
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
		return Solution.initial(run);
	}

	/**
	 * Get the list of listeners.
	 *
	 * @return the list of listeners.
	 */
	public List<Listener> getListeners() {
		// lookup our listeners
		List<Listener> listeners = Lists.newArrayList();
		for (String name : properties.getProperty("listeners", "").split(",")) {
			Listener l = lookup(name, Listener.class, LISTENERS);
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
	public MutationStrategy getMutator() {
		return lookup(properties.getProperty("mutator", "default"), MutationStrategy.class, MUTATORS);
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
	public ObjectiveFunction getObjectiveFunction() {
		return lookup(properties.getProperty("objective", "default"), ObjectiveFunction.class, OBJECTIVES);
	}

	/**
	 * Get an arbitrary property defined on the simulation.
	 *
	 * @param key
	 *            the key.
	 * @return the value or null if not set.
	 */
	public String getProperty(final String key) {
		return properties.getProperty(key);
	}

	/**
	 * Get an arbitrary property defined on the simulation.
	 *
	 * @param key
	 *            the key.
	 * @param defaultValue
	 *            the default value if not set.
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

			// run = Run.loadCONOP9Run(new File(runDir, sectionFile), new
			// File(runDir, eventFile), new File(runDir,
			// loadFile), overrideWeights);
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
		return lookup(properties.getProperty("schedule", "default"), CoolingSchedule.class, SCHEDULES);
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

	/**
	 * Gets the keys in this simulation file.
	 *
	 * @return the set of keys.
	 */
	public Set<String> keys() {
		Set<String> keys = Sets.newHashSet();
		for (Object o : properties.keySet()) {
			keys.add(o.toString());
		}
		return keys;
	}

	public <E> E lookup(final String key, final Class<E> type, final Map<String, E> map) {
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
			((Configurable) instance).configure(this);
		}
		return instance;
	}

	/**
	 * Set a property on the simulation.
	 *
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 */
	public void setProperty(final String key, final String value) {
		properties.setProperty(key, value);
	}
}
