package org.andrill.conop4j.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.andrill.conop4j.CONOP.Listener;
import org.andrill.conop4j.Event;
import org.andrill.conop4j.Solution;
import org.andrill.conop4j.scoring.ExperimentalPenalty;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Logs the CONOP run progress to a file.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class LoggingListener implements Listener {
	private long iter = 0;
	private final File logFile;
	private final Map<Event, double[]> ranks;
	private final File ranksFile;
	private double score = Double.MAX_VALUE;
	private final File solutionFile;
	private long start = -1;

	/**
	 * Create a new LoggingListener.
	 * 
	 * @param logFile
	 *            the log file.
	 * @param solutionFile
	 *            the solution file.
	 * @param ranksFile
	 *            the ranks file.
	 */
	public LoggingListener(final File logFile, final File solutionFile, final File ranksFile) {
		this.logFile = logFile;
		if (logFile.exists()) {
			logFile.delete();
		}
		this.solutionFile = solutionFile;
		if (solutionFile.exists()) {
			solutionFile.delete();
		}
		this.ranksFile = ranksFile;
		if (ranksFile.exists()) {
			ranksFile.delete();
		}
		ranks = Maps.newHashMap();
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if (start == -1) {
			start = System.currentTimeMillis();

			// initialize our ranks
			int size = current.getEvents().size();
			for (Event e : current.getEvents()) {
				double[] array = new double[size];
				Arrays.fill(array, Double.MAX_VALUE);
				ranks.put(e, array);
			}
		}
		iter++;
		if (current.getScore() < score) {
			score = current.getScore();
			long elapsed = (System.currentTimeMillis() - start);

			// write the log entry
			writeLog(elapsed, temp, iter, score);

			// update the ranks
			double score = current.getScore();
			for (Event e : current.getEvents()) {
				ranks.get(e)[current.getPosition(e)] = score;
			}

			// write solution
			try {
				ExperimentalPenalty.write(current, solutionFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// write ranks
			writeRanks();
		}
	}

	private void writeLog(final Object... line) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(logFile, true));
			for (int i = 0; i < line.length - 1; i++) {
				writer.write(line[i] + "\t");
			}
			writer.write(line[line.length - 1] + "\n");
		} catch (IOException e) {
			// do nothing
		} finally {
			Closeables.closeQuietly(writer);
		}
	}

	private void writeRanks() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(ranksFile));
			for (Entry<Event, double[]> e : ranks.entrySet()) {
				writer.write(e.getKey().getName() + "\t");
				double[] ranks = e.getValue();
				for (int i = 0; i < ranks.length - 1; i++) {
					writer.write(ranks[i] + "\t");
				}
				writer.write(ranks[ranks.length - 1] + "\n");
			}
		} catch (IOException e) {
			// do nothing
		} finally {
			Closeables.closeQuietly(writer);
		}
	}
}
