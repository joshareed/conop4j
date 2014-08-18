package org.andrill.conop.core.constraints;

import java.util.List;
import java.util.Set;

import org.andrill.conop.core.AbstractConfigurable;
import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.Location;
import org.andrill.conop.core.Observation;
import org.andrill.conop.core.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Ensures all event constraints are satisfied.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class EventConstraints extends AbstractConfigurable implements Constraints {
	private static final int DEFAULT_SUPPORT = 2;
	private static final boolean DEFAULT_TAXA = true;
	private static final boolean DEFAULT_AGES = true;

	protected class Constraint {
		Event before;
		Event after;

		boolean check(final Solution test) {
			int i1 = test.getPosition(before);
			int i2 = test.getPosition(after);
			return (i1 < i2);
		}
	}

	private static final Logger log = LoggerFactory.getLogger(EventConstraints.class);
	protected List<Constraint> constraints;
	protected boolean taxa = DEFAULT_TAXA;
	protected boolean ages = DEFAULT_AGES;
	protected int support = DEFAULT_SUPPORT;

	protected void calculateConstraints(final Dataset dataset) {
		constraints = Lists.newArrayList();

		if (taxa == true) {
			calculateTaxaConstraints(dataset.getEvents());
		}

		if (ages == true) {
			calculateAgeConstraints(dataset);
		}

		log.info("Configured {} constraints", constraints.size());
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
						log.debug("Configuring new constraint for '{}' before '{}'", e, f);
					}
				}
			}
		}
	}

	protected void calculateAgeConstraints(Dataset dataset) {
		// find all of our ages
		List<Event> ages = Lists.newArrayList();
		for (Event e : dataset.getEvents()) {
			if (e.getName().endsWith("AGE") || e.getName().endsWith("ASH")) {
				ages.add(e);
			}
		}

		for (Event before : ages) {
			for (Event after : ages) {
				int support = 0;

				for (Location l : dataset.getLocations()) {
					Observation o1 = l.getObservation(before);
					Observation o2 = l.getObservation(after);
					if (o1 != null && o2 != null) {
						if (o1.getLevel().compareTo(o2.getLevel()) < 0) {
							support = -1;
							break;
						} else if (o1.getLevel().compareTo(o2.getLevel()) > 0 && support >= 0) {
							support++;
						}
					}
				}

				if (!before.equals(after) && support >= this.support) {
					Constraint c = new Constraint();
					c.before = before;
					c.after = after;
					constraints.add(c);
					log.debug("Configuring new constraint for '{}' before '{}' (support: {})", before, after, support);
				}
			}
		}
	}

	@Override
	public void configure(final Configuration config) {
		super.configure(config);

		taxa = config.get("taxa", DEFAULT_TAXA);
		log.debug("Configuring use taxa constraints as '{}'", taxa);

		ages = config.get("ages", DEFAULT_AGES);
		log.debug("Configuring use age constraints as '{}'", ages);

		support = config.get("support", DEFAULT_SUPPORT);
		log.debug("Configuring support as '{}'", support);
	}

	@Override
	public boolean isValid(final Solution solution) {
		if (constraints == null) {
			calculateConstraints(context.getDataset());
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
		return "Event Constraints";
	}
}
