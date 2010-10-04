package org.andrill.conop4j.scoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.andrill.conop4j.Event;
import org.andrill.conop4j.Observation;
import org.andrill.conop4j.Run;
import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;

import com.google.common.collect.Maps;

/**
 * A test scoring function.
 * 
 * @author Josh Reed (jareed@andrill.org0
 */
public class ExperimentalPenalty implements ScoringFunction {

	/**
	 * Write the event placements using this penalty function.
	 * 
	 * @param solution
	 *            the solution.
	 * @param file
	 *            the file.
	 * @throws IOException
	 *             thrown if there is a problem writing the solution.
	 */
	public static void write(final Solution solution, final File file) throws IOException {
		Run run = solution.getRun();
		Map<Section, ExperimentalPlacement> placements = Maps.newHashMap();
		FileWriter writer = new FileWriter(file);
		writer.write("Event\tRank");
		for (Section s : run.getSections()) {
			writer.write("\t" + s.getName() + " (O)\t" + s.getName() + " (P)");
			placements.put(s, new ExperimentalPlacement(s));
		}
		writer.write("\n");

		// build our placements
		for (Event e : solution.getEvents()) {
			for (Section s : run.getSections()) {
				placements.get(s).place(e);
			}
		}

		int total = solution.getEvents().size();
		for (int i = 0; i < total; i++) {
			Event e = solution.getEvent(i);
			writer.write("'" + e + "'\t" + (total - i));
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

		writer.write("\tTotal");
		for (Section s : run.getSections()) {
			writer.write("\t\t" + placements.get(s).getPenalty());
		}
		writer.write("\n");
		writer.close();
	}

	@Override
	public Type getType() {
		return Type.PENALTY;
	}

	@Override
	public double score(final Solution solution) {
		double score = 0;
		for (Section s : solution.getRun().getSections()) {
			ExperimentalPlacement topDown = new ExperimentalPlacement(s);
			for (Event e : solution.getEvents()) {
				topDown.place(e);
			}
			score += topDown.getPenalty();
		}
		return score;
	}
}
