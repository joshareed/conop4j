package org.andrill.conop4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.andrill.conop4j.constraints.ConstraintChecker;
import org.andrill.conop4j.constraints.EventChecker;
import org.andrill.conop4j.constraints.NullChecker;
import org.andrill.conop4j.mutation.ConstrainedMutator;
import org.andrill.conop4j.mutation.MutationStrategy;
import org.andrill.conop4j.mutation.RandomMutator;
import org.andrill.conop4j.schedule.CoolingSchedule;
import org.andrill.conop4j.schedule.ExponentialCooling;
import org.andrill.conop4j.schedule.LinearCooling;
import org.andrill.conop4j.scoring.ConstraintsScore;
import org.andrill.conop4j.scoring.ExperimentalPenalty;
import org.andrill.conop4j.scoring.ScoringFunction;

import com.google.common.io.Closeables;

/**
 * Reads a run configuration from a properties file.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class RunConfig {
	protected final Properties properties;

	/**
	 * Create a new RunConfig.
	 * 
	 * @param file
	 *            the file.
	 */
	public RunConfig(final File file) {
		properties = new Properties();
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
			return new NullChecker();
		} else if ("event".equals(constraints)) {
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
			return new RandomMutator();
		} else if ("constrained".equals(mutator)) {
			return new ConstrainedMutator();
		} else {
			return new RandomMutator();
		}
	}

	/**
	 * Gets the configured {@link CoolingSchedule}.
	 * 
	 * <pre>
	 * Key: schedule
	 * Values:
	 * 		exponential - {@link ExponentialCooling} (default)
	 * 		linear      - {@link LinearCooling}
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
			return new ExponentialCooling(initial, delta, stepsPer, noProgress);
		} else if ("linear".equals(schedule)) {
			return new LinearCooling(initial, stepsPer, delta);
		} else {
			return new ExponentialCooling(initial, delta, stepsPer, noProgress);
		}
	}

	/**
	 * Gets the configured {@link ScoringFunction}.
	 * 
	 * <pre>
	 * Key: score
	 * Values:
	 * 		experimental - {@link ExperimentalPenalty} (default)
	 * 		constraints  - {@link ConstraintsScore}
	 * </pre>
	 * 
	 * @return the configured ScoringFunction.
	 */
	public ScoringFunction getScore() {
		String score = properties.getProperty("score", "experimental").toLowerCase();

		if ("experimental".equals(score)) {
			return new ExperimentalPenalty();
		} else if ("constraints".equals(score)) {
			return new ConstraintsScore();
		} else {
			return new ExperimentalPenalty();
		}
	}
}
