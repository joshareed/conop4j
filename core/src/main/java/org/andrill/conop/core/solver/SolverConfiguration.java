package org.andrill.conop.core.solver;

import java.util.List;

import org.andrill.conop.core.Solution;
import org.andrill.conop.core.constraints.Constraints;
import org.andrill.conop.core.listeners.Listener;
import org.andrill.conop.core.mutators.Mutator;
import org.andrill.conop.core.penalties.Penalty;
import org.andrill.conop.core.schedules.Schedule;

public interface SolverConfiguration {

	/**
	 * Gets the configured {@link Constraints}.
	 *
	 * @return the configured ConstraintChecker.
	 */
	public abstract Constraints getConstraints();

	/**
	 * Gets the initial solution specified by this simulation.
	 *
	 * @return the initial solution.
	 */
	public abstract Solution getInitialSolution();

	/**
	 * Get the list of listeners.
	 *
	 * @return the list of listeners.
	 */
	public abstract List<Listener> getListeners();

	/**
	 * Gets the configured {@link Mutator}.
	 *
	 * @return the configured MutationStrategy.
	 */
	public abstract Mutator getMutator();

	/**
	 * Gets the configured {@link Penalty}.
	 *
	 * @return the configured Penalty.
	 */
	public abstract Penalty getPenalty();

	/**
	 * Gets the configured {@link Schedule}.
	 *
	 * @return the configured CoolingSchedule.
	 */
	public abstract Schedule getSchedule();

	/**
	 * Gets the configured {@link Solver}.
	 *
	 * @return the solver.
	 */
	public abstract Solver getSolver();

}