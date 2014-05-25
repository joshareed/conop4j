package org.andrill.conop.core.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.RanksMatrix;
import org.andrill.conop.core.Simulation;
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
	private static final String[] HEADER = new String[] { "Event", "Rank", "Min Rank", "Max Rank" };

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

	protected int next = 60;
	protected File snapshotFile;
	protected File solutionFile;
	protected CSVWriter writer;

	@Override
	public void configure(final Simulation simulation) {
		super.configure(simulation);
		try {
			solutionFile = getFile(simulation.getProperty("solution.file", "solution.csv"));
			snapshotFile = getFile(simulation.getProperty("snapshot.file", "snapshot.csv"));
			writer = new CSVWriter(new BufferedWriter(new FileWriter(snapshotFile)), '\t');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void run(final double temp, final long iteration, final Solution current, final Solution best) {
		try {
			writeResults(solutionFile, best);
			writer.writeNext(new String[] {
					I.format((next / 60) - 1),
					I.format(iteration),
					D.format(temp),
					D.format(best.getScore())
			});
			writer.flush();
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
	protected boolean test(final double temp, final long iteration, final Solution current, final Solution best) {
		if (TimerUtils.getCounter() >= next) {
			next += 60;
			return true;
		} else {
			return false;
		}
	}

	protected void writeResults(final File file, final Solution solution) {
		try (CSVWriter csv = new CSVWriter(new BufferedWriter(new FileWriter(file)), '\t')) {
			// write the header
			csv.writeNext(HEADER);

			// write the events
			int total = solution.getEvents().size();
			RanksMatrix ranks = solution.getRun().getRanksMatrix();
			String[] next = new String[4];
			for (int i = 0; i < total; i++) {
				Event e = solution.getEvent(i);
				next[0] = e.getName();
				next[1] = I.format(total - i);
				next[2] = I.format(ranks.getMinRank(e));
				next[3] = I.format(ranks.getMaxRank(e));
				csv.writeNext(next);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
