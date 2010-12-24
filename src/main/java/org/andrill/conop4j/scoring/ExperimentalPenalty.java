package org.andrill.conop4j.scoring;

import org.andrill.conop4j.Event;
import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;

/**
 * A test scoring function.
 * 
 * @author Josh Reed (jareed@andrill.org0
 */
public class ExperimentalPenalty implements ScoringFunction {

	@Override
	public Type getType() {
		return Type.PENALTY;
	}

	@Override
	public double score(final Solution solution) {
		double score = 0;
		for (final Section s : solution.getRun().getSections()) {
			ExperimentalPlacement placement = new ExperimentalPlacement(s);
			for (Event e : solution.getEvents()) {
				placement.place(e);
			}
			score += placement.getPenalty();
		}
		return score;
	}
}
