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
	private final File file;
	private long iter = 0;
	private double score = Double.MAX_VALUE;
	private long start = -1;

	public LoggingListener(final File file) {
		this.file = file;
		if (file.exists()) {
			file.delete();
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
				ExperimentalPenalty.write(current, new File("solution.csv"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void write(final Object... line) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
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
