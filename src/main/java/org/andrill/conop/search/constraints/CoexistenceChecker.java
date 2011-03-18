package org.andrill.conop.search.constraints;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.CoexistenceMatrix;
import org.andrill.conop.search.CoexistenceMatrix.Coexistence;
import org.andrill.conop.search.Event;
import org.andrill.conop.search.Run;
import org.andrill.conop.search.Section;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A constraints checker that verifies event constraints as well as coexistence
 * relationships for important events.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CoexistenceChecker extends AbstractConfigurable implements ConstraintChecker {
	protected Map<Event, Event> conjunct = Maps.newHashMap();

	protected void init(final Run run) {
		// identify important events
		List<Event> important = Lists.newArrayList();
		for (Event e : run.getEvents()) {
			int counter = 0;
			for (Section s : run.getSections()) {
				if (s.getObservation(e) != null) {
					counter++;
				}
			}
			if ((counter > 0.5 * run.getSections().size())
					&& (((e.getBeforeConstraint() == null) && (e.getAfterConstraint() == null)) || (e
							.getBeforeConstraint() != null))) {
				important.add(e);
			}
		}

		// find important conjunct pairs
		CoexistenceMatrix rm = run.getCoexistenceMatrix();
		for (int i = 0; i < important.size(); i++) {
			Event e1 = important.get(i);
			for (int j = i + 1; j < important.size(); j++) {
				Event e2 = important.get(j);
				if (rm.getCoexistence(e1, e2) == Coexistence.CONJUNCT) {
					conjunct.put(e1, e2);
				}
			}
		}
	}

	public boolean isValid(final Solution solution) {
		if (conjunct.size() == 0) {
			init(solution.getRun());
		}

		for (Event e : solution.getEvents()) {
			int position = solution.getPosition(e);
			Event before = e.getBeforeConstraint();
			if ((before != null) && (position > solution.getPosition(before))) {
				return false;
			}
			Event after = e.getAfterConstraint();
			if ((after != null) && (position < solution.getPosition(after))) {
				return false;
			}
		}

		for (Entry<Event, Event> e : conjunct.entrySet()) {
			Event e1 = e.getKey();
			Event e2 = e.getValue();
			int e1a = solution.getPosition(e1);
			int e1b = e1a;
			if (e1.getBeforeConstraint() != null) {
				e1b = solution.getPosition(e1.getBeforeConstraint());
			}
			int e2a = solution.getPosition(e2);
			int e2b = e2a;
			if (e2.getBeforeConstraint() != null) {
				e2b = solution.getPosition(e2.getBeforeConstraint());
			}
			if ((e1a > e2b) || (e2a > e1b)) {
				return false;
			}
		}
		return true;
	}
}
