package org.andrill.conop.core.constraints;

import java.util.Set;
import java.util.TreeSet;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.Run;
import org.andrill.conop.core.Solution;

/**
 * Ensures all event constraints are satisfied.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class EventConstraints extends AbstractConfigurable implements Constraints {
	protected class Constraint implements Comparable<Constraint> {
		Event before;
		Event after;
		int failed = 0;

		boolean check(final Solution test) {
			int i1 = test.getPosition(before);
			int i2 = test.getPosition(after);
			boolean result = (i1 < i2);
			if (!result) {
				failed++;
			}
			return result;
		}

		@Override
		public int compareTo(final Constraint o) {
			return Integer.compare(failed, o.failed);
		}
	}

	protected Set<Constraint> constraints;
	protected boolean taxa = false;

	protected void calculateConstraints(final Run run) {
		constraints = new TreeSet<>();

		if (taxa == true) {
			calculateTaxaConstraints(run.getEvents());
		}
	}

	protected void calculateTaxaConstraints(final Set<Event> events) {
		for (Event e : events) {
			if (e.getName().endsWith("LAD")) {
				String fad = e.getName().replace("LAD", "FAD");
				for (Event f : events) {
					if (fad.equalsIgnoreCase(f.getName())) {
						Constraint c = new Constraint();
						c.before = e;
						c.after = f;
						constraints.add(c);
					}
				}
			}
		}
	}

	@Override
	public void configure(final Configuration config) {
		super.configure(config);

		taxa = config.get("taxa", true);
	}

	@Override
	public boolean isValid(final Solution solution) {
		if (constraints == null) {
			calculateConstraints(solution.getRun());
		}
		for (Constraint c : constraints) {
			if (!c.check(solution)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Event";
	}
}
