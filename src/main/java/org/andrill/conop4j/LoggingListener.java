package org.andrill.conop4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.andrill.conop4j.CONOP.Listener;
import org.andrill.conop4j.scoring.ExperimentalPenalty;

import com.google.common.io.Closeables;

/**
 * Logs the CONOP run progress to a file.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class LoggingListener implements Listener {
	private static final DecimalFormat DEC = new DecimalFormat("0.00");
	private long iter = 0;
	private final File logFile;
	private double score = Double.MAX_VALUE;
	private final File solutionFile;
	private long start = -1;

	/**
	 * Create a new LoggingListener.
	 * 
	 * @param logFile
	 *            the log file.
	 */
	public LoggingListener(final File logFile, final File solutionFile) {
		this.logFile = logFile;
		if (logFile.exists()) {
			logFile.delete();
		}
		this.solutionFile = solutionFile;
		if (solutionFile.exists()) {
			solutionFile.delete();
		}
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if (start == -1) {
			start = System.currentTimeMillis();
		}
		iter++;
		if (current.getScore() < score) {
			score = current.getScore();
			long elapsed = (System.currentTimeMillis() - start);
			write(elapsed, temp, iter, score);
			System.out.print("                                                                            \r");
			System.out.print("CONOP4J: " + DEC.format(score) + " [ " + (elapsed / 60000) + "min | " + DEC.format(temp)
					+ "C | " + iter + " ]\r");
			try {
				ExperimentalPenalty.write(current, solutionFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void write(final Object... line) {
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
}
