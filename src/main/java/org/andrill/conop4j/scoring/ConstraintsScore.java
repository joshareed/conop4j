package org.andrill.conop4j.scoring;

import java.util.Map;

import org.andrill.conop4j.CoexistenceMatrix;
import org.andrill.conop4j.Event;
import org.andrill.conop4j.Run;
import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;

import com.google.common.collect.Maps;

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

	protected void remove(final Event e, final Map<Event, Boolean> check) {
		check.put(e, true);
		Event before = e.getBeforeConstraint();
		if (before != null) {
			check.put(before, true);
		}
		Event after = e.getAfterConstraint();
		if (after != null) {
			check.put(after, true);
		}
	}

	@Override
	public double score(final Solution solution) {
		double score = 0;
		Run run = solution.getRun();
		CoexistenceMatrix sm = new CoexistenceMatrix(solution);
		for (Section s : run.getSections()) {
			CoexistenceMatrix cm = run.getCoexistenceMatrix(s);
			Map<Event, Boolean> check = Maps.newHashMap();
			for (Event e1 : s.getEvents()) {
				remove(e1, check);
				for (Event e2 : s.getEvents()) {
					if (!check.containsKey(e2)) {
						if (sm.getCoexistence(e1, e2) == cm.getCoexistence(e1, e2)) {
							score += 1;
						}
					}
					remove(e2, check);
				}
			}
		}
		return score;
	}
}
