package org.andrill.conop.search.objectives;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.CoexistenceMatrix;
import org.andrill.conop.search.Event;
import org.andrill.conop.search.Solution;

/**
 * Create a penalty based on event coexistence violations.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CoexistencePenalty extends AbstractConfigurable implements ObjectiveFunction {

	@Override
	public double score(final Solution solution) {
		int penalty = 0;
		CoexistenceMatrix rm = solution.getRun().getCoexistenceMatrix();
		CoexistenceMatrix sm = new CoexistenceMatrix(solution);
		for (Event e1 : solution.getEvents()) {
			for (Event e2 : solution.getEvents()) {
				int observed = rm.getCoexistence(e1, e2);
				int proposed = sm.getCoexistence(e1, e2);
				int combined = observed & proposed;
				if (combined == 0) {
					penalty += 10;
				} else if (combined < observed) {
					penalty += 4;
				}
			}
		}
		return penalty;
	}

	@Override
	public String toString() {
		return "Coexistence";
	}
}
