package org.andrill.conop.search.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.andrill.conop.search.Event;
import org.andrill.conop.search.Observation;
import org.andrill.conop.search.Run;
import org.andrill.conop.search.Section;
import org.andrill.conop.search.Solution;
import org.andrill.conop.search.objectives.SectionPlacement;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Writes a log and current best solution to files every minute.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SnapshotListener extends AsyncListener {
	private static final DecimalFormat D = new DecimalFormat("0.00");

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

	protected long last = 0;
	private Map<Event, double[]> ranks;
	private double score = -1;
	protected File snapshotFile;
	protected File solutionFile;
	protected Writer writer;

	@Override
	public void configure(final Properties properties) {
		try {
			solutionFile = new File(properties.getProperty("solution.file", "solution.tmp"));
			snapshotFile = getFile(properties.getProperty("snapshot.file", "snapshot.csv"));
			writer = new BufferedWriter(new FileWriter(snapshotFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the max rank for the specified event.
	 * 
	 * @param e
	 *            the event.
	 * @return the max rank.
	 */
	public int getMax(final Event e) {
		double[] array = ranks.get(e);
		if (array == null) {
			return -1;
		}

		int i = 0;
		while ((array[i] != score) && (i < array.length)) {
			i++;
		}
		return array.length - i;
	}

	/**
	 * Gets the min rank for the specified event.
	 * 
	 * @param e
	 *            the event.
	 * @return the min rank.
	 */
	public int getMin(final Event e) {
		double[] array = ranks.get(e);
		if (array == null) {
			return -1;
		}

		int i = array.length - 1;
		while ((i >= 0) && (array[i] != score)) {
			i--;
		}
		return array.length - i;
	}

	/**
	 * Get the ranks array.
	 * 
	 * @return the ranks array.
	 */
	public Map<Event, double[]> getRanks() {
		return ranks;
	}

	@Override
	protected void run(final double temp, final long iteration, final Solution current, final Solution best) {
		try {
			writeResults(solutionFile, best);
			writer.write(last + "\t" + iteration + "\t" + D.format(temp) + "\t" + D.format(best.getScore()) + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void started(final Solution initial) {
		ranks = Maps.newHashMap();
		int size = initial.getEvents().size();
		for (Event e : initial.getEvents()) {
			double[] array = new double[size];
			Arrays.fill(array, -1);
			ranks.put(e, array);
		}
		score = initial.getScore();
	}

	@Override
	public void stopped(final Solution solution) {
		if (solution == null) {
			solutionFile.delete();
			snapshotFile.delete();
		} else {
			writeResults(getFile("solution.csv"), solution);
		}
	}

	@Override
	protected boolean test(final double temp, final long iteration, final Solution current, final Solution best) {
		if (current.getScore() <= score) {
			updateRanks(current);
		}

		long min = (System.currentTimeMillis() - start) / 60000;
		if (min > last) {
			last = min;
			return true;
		} else {
			return false;
		}
	}

	protected void updateRanks(final Solution current) {
		score = current.getScore();
		for (Event e : current.getEvents()) {
			ranks.get(e)[current.getPosition(e)] = score;
		}

	}

	protected void writeResults(final File file, final Solution solution) {
		BufferedWriter writer = null;
		try {
			Run run = solution.getRun();
			Map<Section, SectionPlacement> placements = Maps.newHashMap();

			// open our writer
			writer = new BufferedWriter(new FileWriter(file));
			writer.write("Event\tRank");
			if (ranks != null) {
				writer.write("\tMin Rank\tMax Rank");
			}
			for (Section s : run.getSections()) {
				writer.write("\t" + s.getName() + " (O)\t" + s.getName() + " (P)");
				placements.put(s, new SectionPlacement(s));
			}
			writer.write("\n");

			// build our placements
			double score = 0;
			for (final Section s : solution.getRun().getSections()) {
				SectionPlacement placement = placements.get(s);
				for (Event e : solution.getEvents()) {
					placement.place(e);
				}
				score += placement.getPenalty();
			}

			int total = solution.getEvents().size();
			for (int i = 0; i < total; i++) {
				Event e = solution.getEvent(i);
				writer.write("'" + e + "'\t" + (total - i));
				if (ranks != null) {
					writer.write("\t" + getMin(e) + "\t" + getMax(e));
				}
				for (Section s : run.getSections()) {
					writer.write("\t");
					Observation o = s.getObservation(e);
					if (o != null) {
						writer.write("" + o.getLevel());
					}
					writer.write("\t" + placements.get(s).getPlacement(e));
				}
				writer.write("\n");
			}

			writer.write("Total");
			writer.write("\t" + D.format(score));
			if (ranks != null) {
				writer.write("\t\t");
			}
			for (Section s : run.getSections()) {
				writer.write("\t\t" + D.format(placements.get(s).getPenalty()));
			}
			writer.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Closeables.closeQuietly(writer);
		}
	}
}
