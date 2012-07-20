package org.andrill.conop.search.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Properties;

import org.andrill.conop.search.Event;
import org.andrill.conop.search.Observation;
import org.andrill.conop.search.Run;
import org.andrill.conop.search.Section;
import org.andrill.conop.search.Solution;
import org.andrill.conop.search.objectives.SectionPlacement;
import org.andrill.conop.search.util.TimerUtils;

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

	protected int next = 60;
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

	@Override
	protected void run(final double temp, final long iteration, final Solution current, final Solution best) {
		try {
			writeResults(solutionFile, best);
			writer.write(((next / 60) - 1) + "\t" + iteration + "\t" + D.format(temp) + "\t"
					+ D.format(best.getScore()) + "\n");
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
			writeResults(getFile("solution.csv"), solution);
		}
		solutionFile.delete();
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
		BufferedWriter writer = null;
		try {
			Run run = solution.getRun();
			Map<Section, SectionPlacement> placements = Maps.newHashMap();

			// open our writer
			writer = new BufferedWriter(new FileWriter(file));
			writer.write("Event\tRank\tMin Rank\tMax Rank");
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
				writer.write("\t" + solution.getMinRank(e) + "\t" + solution.getMaxRank(e));
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
			writer.write("\t\t");
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
