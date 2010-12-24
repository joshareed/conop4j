package org.andrill.conop4j.objective;

import org.andrill.conop4j.Event;
import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;

/**
 * A test scoring function.
 * 
 * @author Josh Reed (jareed@andrill.org0
 */
public class PlacementPenalty implements ObjectiveFunction {

	@Override
	public double score(final Solution solution) {
		double score = 0;
		for (final Section s : solution.getRun().getSections()) {
			SectionPlacement placement = new SectionPlacement(s);
			for (Event e : solution.getEvents()) {
				placement.place(e);
			}
			score += placement.getPenalty();
		}
		return score;
	}
}
