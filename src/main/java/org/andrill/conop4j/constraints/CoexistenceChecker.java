package org.andrill.conop4j.constraints;

import org.andrill.conop4j.CoexistenceMatrix;
import org.andrill.conop4j.CoexistenceMatrix.Coexistence;
import org.andrill.conop4j.Event;
import org.andrill.conop4j.Solution;

public class CoexistenceChecker implements ConstraintChecker {

	@Override
	public boolean isValid(final Solution solution) {
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
		return true;
	}
}
