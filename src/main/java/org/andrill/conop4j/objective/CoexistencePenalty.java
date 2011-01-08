package org.andrill.conop4j.objective;

import org.andrill.conop4j.CoexistenceMatrix;
import org.andrill.conop4j.CoexistenceMatrix.Coexistence;
import org.andrill.conop4j.Event;
import org.andrill.conop4j.Solution;

/**
 * Create a penalty based on event coexistence violations.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CoexistencePenalty implements ObjectiveFunction {

	@Override
	public double score(final Solution solution) {
		int violations = 0;
		CoexistenceMatrix rm = solution.getRun().getCoexistenceMatrix();
		CoexistenceMatrix sm = new CoexistenceMatrix(solution);
		for (Event e1 : solution.getEvents()) {
			for (Event e2 : solution.getEvents()) {
				Coexistence rc = rm.getCoexistence(e1, e2);
				if ((rc != Coexistence.ABSENT) && (rc != Coexistence.MIXED) && (rc != sm.getCoexistence(e1, e2))) {
					violations++;
				}
			}
		}
		return violations;
	}
}
