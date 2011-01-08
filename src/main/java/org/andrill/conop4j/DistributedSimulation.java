package org.andrill.conop4j;

import java.io.File;
import java.text.DecimalFormat;

import org.andrill.conop4j.listeners.ProgressListener;
import org.andrill.conop4j.listeners.RanksListener;
import org.andrill.conop4j.listeners.SnapshotListener;
import org.andrill.conop4j.mutation.MulticastSharedMutator;

/**
 * Run a distributed simulation in which CONOP processes communicate with each
 * other via multicast.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class DistributedSimulation {
	private static final DecimalFormat D = new DecimalFormat("0.00");

	/**
	 * Run the simulation.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(final String[] args) {
		// load the simulation configuration
		Simulation config = new Simulation(new File(args[0]));
		Run run = config.getRun();

		// distributed mutator
		MulticastSharedMutator mutator = new MulticastSharedMutator(run, config.getMutator(), 0.75);

		// setup CONOP4J
		CONOP conop = new CONOP(config.getConstraints(), mutator, config.getObjectiveFunction(), config.getSchedule());

		// add a listener to print out progress
		conop.addListener(new ProgressListener());
		conop.addListener(new SnapshotListener());

		// add a listener to collect event ranks
		RanksListener ranks = new RanksListener();
		conop.addListener(ranks);

		// add our distributed listener
		conop.addListener(mutator);

		// find the optimal placement
		long start = System.currentTimeMillis();
		Solution solution = conop.solve(run, Solution.initial(run));
		long elapsed = (System.currentTimeMillis() - start) / 60000;
		System.out.println("Elapsed time: " + elapsed + " minutes.  Final score: " + D.format(solution.getScore()));

		// write out the solution and ranks
		Simulation.writeResults(solution, ranks);
	}
}
