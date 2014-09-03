package org.andrill.conop.core.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.Solution;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Periodically writes current best solution to a file.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class SnapshotListener extends PeriodicListener {
	private static final DecimalFormat I = new DecimalFormat("0");
	private static final DecimalFormat D = new DecimalFormat("0.00");
	private static final String[] HEADER = new String[] { "Event", "Position", "Min", "Max" };

	protected static File getFile(final String file) {
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

	protected File solutionFile;

	@Override
	public void configure(final Configuration config) {
		super.configure(config);

		solutionFile = getFile(config.get("solution", "solution.csv"));
		log.debug("Configuring solution file as '{}'", solutionFile);
	}

	@Override
	protected void fired(final double temp, final long iteration, final Solution current, final Solution best) {
		writeResults(solutionFile, best);
	}

	@Override
	public void stopped(final Solution solution) {
		writeResults(solutionFile, solution);
	}

	protected void writeResults(final File file, final Solution solution) {
		log.debug("Writing solution with score '{}' to '{}'", D.format(solution.getScore()), file);
		try (CSVWriter csv = new CSVWriter(new BufferedWriter(new FileWriter(file)), '\t')) {
			// write the header
			csv.writeNext(HEADER);

			PositionsMatrix matrix = null;
			if (context != null) {
				matrix = context.get(PositionsMatrix.class);
			}

			// write the events
			int total = solution.getEvents().size();
			String[] next = new String[4];
			for (int i = 0; i < total; i++) {
				Event e = solution.getEvent(i);

				next[0] = e.getName();
				next[1] = I.format(i);
				if (matrix == null) {
					next[2] = I.format(i);
					next[3] = I.format(i);
				} else {
					int[] range = matrix.getRange(e);
					next[2] = I.format(range[0]);
					next[3] = I.format(range[1]);
				}
				csv.writeNext(next);
			}
		} catch (IOException e) {
			log.error("Unable to write solution to file", e);
		}
	}

	@Override
	public String toString() {
		return "Snapshot Listener";
	}
}
