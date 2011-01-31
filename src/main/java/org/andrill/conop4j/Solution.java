package org.andrill.conop4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.andrill.conop4j.constraints.ConstraintChecker;
import org.andrill.conop4j.constraints.EventChecker;
import org.andrill.conop4j.objectives.CoexistencePenalty;
import org.andrill.conop4j.objectives.MatrixPenalty;
import org.andrill.conop4j.objectives.PlacementPenalty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

/**
 * A solution contains a set of events in a particular order.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Solution {

	private static Event find(final Run run, final String col1, final String col2) {
		String name = col1;
		if (col1.startsWith("'") || col1.startsWith("\"")) {
			name = name.substring(1, name.length() - 1);
		}
		try {
			Integer.parseInt(col2);
		} catch (NumberFormatException e) {
			String type = col2;
			if (col2.startsWith("'") || col2.startsWith("\"")) {
				type = col2.substring(1, col2.length() - 1);
			}
			if ("LAD".equals(type) || "FAD".equals(type) || "MID".equals(type)) {
				name = name + " " + type;
			}
		}
		for (Event e : run.getEvents().asList()) {
			if (name.equalsIgnoreCase(e.getName())) {
				return e;
			}
		}
		System.out.println("No match for " + name);
		return null;
	}

	/**
	 * Load a solution from a CSV file.
	 * 
	 * @param run
	 *            the run.
	 * @param csv
	 *            the CSV file.
	 * @return the solution.
	 */
	public static Solution fromCSV(final Run run, final File csv) {
		String line = null;
		BufferedReader reader = null;
		List<Event> list = Lists.newArrayList();
		try {
			reader = new BufferedReader(new FileReader(csv));
			reader.readLine(); // eat header line
			while (((line = reader.readLine()) != null) && (list.size() < run.getEvents().size())) {
				String[] split = line.split("\t");
				list.add(find(run, split[0], split[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Closeables.closeQuietly(reader);
		}
		return new Solution(run, list);
	}

	/**
	 * Creates an initial solution with events sorted by type.
	 * 
	 * @param run
	 *            the run.
	 * @return the initial solution.
	 */
	public static Solution initial(final Run run) {
		final List<Event> events = Lists.newArrayList(run.getEvents());
		Collections.sort(events, new Comparator<Event>() {
			@Override
			public int compare(final Event o1, final Event o2) {
				return toInt(o1).compareTo(toInt(o2));
			}

			public Integer toInt(final Event e) {
				if ((e.before != null) && (e.after == null)) {
					return Integer.valueOf(-1);
				} else if ((e.after != null) && (e.before == null)) {
					return Integer.valueOf(1);
				} else {
					return Integer.valueOf(0);
				}
			}
		});
		return new Solution(run, events);
	}

	/**
	 * Score a solution.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(final String[] args) {
		Run run = Run.loadCONOP9Run(new File(args[0]));
		Solution solution = fromCSV(run, new File(args[1]));
		DecimalFormat pretty = new DecimalFormat("0.00");
		ConstraintChecker constraints = new EventChecker();
		System.out.println("Valid: " + constraints.isValid(solution));
		PlacementPenalty penalty = new PlacementPenalty();
		System.out.println("Score (" + penalty + "): " + pretty.format(penalty.score(solution)));
		MatrixPenalty matrix = new MatrixPenalty();
		System.out.println("Score (" + matrix + "): " + pretty.format(matrix.score(solution)));
		CoexistencePenalty coex = new CoexistencePenalty();
		System.out.println("Score (" + coex + "): " + pretty.format(coex.score(solution)));
	}

	protected final ImmutableList<Event> events;
	protected final Map<Event, Integer> positions;
	protected final Run run;
	protected double score = 0.0;

	/**
	 * Create a new Solution with the specified ordered list of events.
	 * 
	 * @param run
	 *            the run.
	 * @param events
	 *            the list of events.
	 */
	public Solution(final Run run, final List<Event> events) {
		this.events = ImmutableList.copyOf(events);
		this.run = run;

		// index our events
		positions = new IdentityHashMap<Event, Integer>();
		for (int i = 0; i < events.size(); i++) {
			positions.put(events.get(i), i);
		}
	}

	/**
	 * Gets the event at the specified position.
	 * 
	 * @param position
	 *            the position.
	 * @return the event.
	 */
	public Event getEvent(final int position) {
		return events.get(position);
	}

	/**
	 * Gets the ordered, immutable list of events in this solution.
	 * 
	 * @return the immutable list of events.
	 */
	public ImmutableList<Event> getEvents() {
		return events;
	}

	/**
	 * Gets the position of the event in this solution.
	 * 
	 * @param event
	 *            the event.
	 * @return the position.
	 */
	public int getPosition(final Event event) {
		return positions.get(event);
	}

	/**
	 * Gets the run this solution belongs to.
	 * 
	 * @return the run.
	 */
	public Run getRun() {
		return run;
	}

	/**
	 * Gets the score for this solution.
	 * 
	 * @return the score.
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Sets the score for this solution.
	 * 
	 * @param score
	 *            the score.
	 */
	public void setScore(final double score) {
		this.score = score;
	}
}
