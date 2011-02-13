package org.andrill.conop.search.objectives;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.CoexistenceMatrix;
import org.andrill.conop.search.CoexistenceMatrix.Coexistence;
import org.andrill.conop.search.Event;
import org.andrill.conop.search.Solution;

/**
 * Create a penalty based on event coexistence violations.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CoexistencePenalty extends AbstractConfigurable implements
		ObjectiveFunction {

	@Override
	public double score(final Solution solution) {
		int violations = 0;
		CoexistenceMatrix rm = solution.getRun().getCoexistenceMatrix();
		CoexistenceMatrix sm = new CoexistenceMatrix(solution);
		for (Event e1 : solution.getEvents()) {
			for (Event e2 : solution.getEvents()) {
				Coexistence rc = rm.getCoexistence(e1, e2);
				if ((rc != Coexistence.ABSENT) && (rc != Coexistence.MIXED)
						&& (rc != sm.getCoexistence(e1, e2))) {
					violations++;
				}
			}
		}
		return violations;
	}

	@Override
	public String toString() {
		return "Coexistence";
	}
}
