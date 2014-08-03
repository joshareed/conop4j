package org.andrill.conop.core.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantLock;

import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.util.TimerUtils;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Writes a log and current best solution to files every minute.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class SnapshotListener extends AsyncListener {
	private static final DecimalFormat I = new DecimalFormat("0");
	private static final DecimalFormat D = new DecimalFormat("0.00");
	private static final String[] HEADER = new String[] { "Event", "Position",
			"Min", "Max" };

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

	protected ReentrantLock lock = new ReentrantLock();
	protected long next = 60;
	protected File snapshotFile;
	protected File solutionFile;
	protected CSVWriter writer;

	@Override
	public void configure(final Configuration config) {
		super.configure(config);

		solutionFile = getFile(config.get("solution", "solution.csv"));
		snapshotFile = getFile(config.get("snapshot", "snapshot.csv"));
	}

	@Override
	protected void run(final double temp, final long iteration,
			final Solution current, final Solution best) {
		if (lock.tryLock()) {
			try {
				next = TimerUtils.getCounter() + 60;

				// write out our results
				writeResults(solutionFile, best);
				writer.writeNext(new String[] { I.format((next / 60) - 1),
						I.format(iteration), D.format(temp),
						D.format(best.getScore()) });
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
	}

	@Override
	public void started(final Solution initial) {
		super.started(initial);

		try {
			writer = new CSVWriter(new BufferedWriter(new FileWriter(
					snapshotFile)), '\t');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopped(final Solution solution) {
		if (solution == null) {
			snapshotFile.delete();
		} else {
			writeResults(solutionFile, solution);
		}
	}

	@Override
	protected boolean test(final double temp, final long iteration,
			final Solution current, final Solution best) {
		return (TimerUtils.getCounter() > next);
	}

	protected void writeResults(final File file, final Solution solution) {
		try (CSVWriter csv = new CSVWriter(new BufferedWriter(new FileWriter(
				file)), '\t')) {
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
			e.printStackTrace();
		}
	}
}
