package org.andrill.conop.search.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Properties;

import org.andrill.conop.search.Simulation;
import org.andrill.conop.search.Solution;

/**
 * Writes a log and current best solution to files every minute.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SnapshotListener extends AsyncListener {
	private static final DecimalFormat D = new DecimalFormat("0.00");
	protected File file;
	protected long last = 0;
	protected RanksListener ranks;
	protected long start = -1;
	protected Writer writer;

	/**
	 * Create a new SnapshotListener.
	 * 
	 * @param ranks
	 *            the ranks listener or null.
	 * 
	 */
	public SnapshotListener(final RanksListener ranks) {
		this.ranks = ranks;
	}

	@Override
	public void configure(final Properties properties) {
		try {
			file = new File(properties.getProperty("snapshot.solution.file", "solution.tmp"));
			writer = new BufferedWriter(new FileWriter(Simulation.getFile(properties.getProperty(
					"snapshot.snapshot.file", "snapshot.csv"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void first(final double temp, final Solution current, final Solution best) {
		start = System.currentTimeMillis();
	}

	@Override
	protected void run(final double temp, final long iteration, final Solution current, final Solution best) {
		try {
			Simulation.writeResults(file, best, ranks);
			writer.write(last + "\t" + iteration + "\t" + D.format(temp) + "\t" + D.format(best.getScore()) + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean test(final double temp, final long iteration, final Solution current, final Solution best) {
		long min = (System.currentTimeMillis() - start) / 60000;
		if (min > last) {
			last = min;
			return true;
		} else {
			return false;
		}
	}
}
