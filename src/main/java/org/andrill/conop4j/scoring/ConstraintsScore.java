package org.andrill.conop4j.scoring;

import org.andrill.conop4j.CoexistenceMatrix;
import org.andrill.conop4j.Event;
import org.andrill.conop4j.Run;
import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;

/**
 * Calculates a score based on the number of observed coexistences the potential
 * solution preserves.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ConstraintsScore implements ScoringFunction {

	@Override
	public Type getType() {
		return Type.SCORE;
	}

	@Override
	public double score(final Solution solution) {
		double score = 0;
		Run run = solution.getRun();
		CoexistenceMatrix sm = new CoexistenceMatrix(solution);
		for (Event e1 : solution.getEvents()) {
			for (Event e2 : solution.getEvents()) {
				for (Section s : run.getSections()) {
					CoexistenceMatrix cm = run.getCoexistenceMatrix(s);
					if (sm.getCoexistence(e1, e2) == cm.getCoexistence(e1, e2)) {
						score += 1;
					}
				}
			}
		}
		return score;
	}
}
