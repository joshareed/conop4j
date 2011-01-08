package org.andrill.conop4j;

import java.io.File;
import java.text.DecimalFormat;

import org.andrill.conop4j.listeners.ProgressListener;
import org.andrill.conop4j.listeners.RanksListener;
import org.andrill.conop4j.listeners.SnapshotListener;

/**
 * Runs a serial simulation which runs several CONOP processes, one after
 * another.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SerialSimulation {
	private static final DecimalFormat D = new DecimalFormat("0.00");

	/**
	 * Run the simulation.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(final String[] args) {
		// load the simulation configuration
		final Simulation config = new Simulation(new File(args[0]));
		Run run = config.getRun();
		int count = Integer.parseInt(args[1]);

		// setup our listeners
		ProgressListener progress = new ProgressListener();
		SnapshotListener snapshot = new SnapshotListener();
		RanksListener ranks = new RanksListener();

		// create an initial solution
		Solution solution = Solution.initial(run);

		// make several serial runs
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			// setup CONOP4J
			CONOP conop = new CONOP(config.getConstraints(), config.getMutator(), config.getObjectiveFunction(),
					config.getSchedule());

			// add our listeners
			conop.addListener(progress);
			conop.addListener(snapshot);
			conop.addListener(ranks);

			// solve
			solution = conop.solve(run, solution);
		}

		long elapsed = (System.currentTimeMillis() - start) / 60000;
		System.out.println("Elapsed time: " + elapsed + " minutes.  Final score: " + D.format(solution.getScore()));

		// write out the results
		Simulation.writeResults(solution, ranks);
	}
}
