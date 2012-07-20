package org.andrill.conop.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.andrill.conop.search.constraints.CoexistenceChecker;
import org.andrill.conop.search.constraints.ConstraintChecker;
import org.andrill.conop.search.constraints.EventChecker;
import org.andrill.conop.search.objectives.CoexistencePenalty;
import org.andrill.conop.search.objectives.MatrixPenalty;
import org.andrill.conop.search.objectives.ObjectiveFunction;
import org.andrill.conop.search.objectives.PlacementPenalty;
import org.andrill.conop.search.objectives.RulesPenalty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * A solution contains a set of events in a particular order.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Solution {

	private static double best = Double.MAX_VALUE;
	private static Map<Event, double[]> ranks;

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

		ConstraintChecker[] constraints = new ConstraintChecker[] { new EventChecker(), new CoexistenceChecker() };
		System.out.println("Constraints: ");
		for (ConstraintChecker c : constraints) {
			System.out.println("\t" + c + ": " + c.isValid(solution));
		}

		ObjectiveFunction[] objectives = new ObjectiveFunction[] { new PlacementPenalty(), new MatrixPenalty(),
				new CoexistencePenalty(), new RulesPenalty() };
		System.out.println("Objectives:");
		for (ObjectiveFunction f : objectives) {
			System.out.println("\t" + f + ": " + pretty.format(f.score(solution)));
		}
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
	 * Gets the max rank for the specified event.
	 * 
	 * @param e
	 *            the event.
	 * @return the max rank.
	 */
	public int getMaxRank(final Event e) {
		double[] array = ranks.get(e);
		if (array == null) {
			return -1;
		}

		int i = 0;
		while ((i < array.length) && (array[i] != score)) {
			i++;
		}
		return array.length - i;
	}

	/**
	 * Gets the min rank for the specified event.
	 * 
	 * @param e
	 *            the event.
	 * @return the min rank.
	 */
	public int getMinRank(final Event e) {
		double[] array = ranks.get(e);
		if (array == null) {
			return -1;
		}

		int i = array.length - 1;
		while ((i >= 0) && (array[i] != score)) {
			i--;
		}
		return array.length - i;
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
	 * Gets the rank for a specified event.
	 * 
	 * @param e
	 *            the event.
	 * @return the rank.
	 */
	public int getRank(final Event e) {
		return events.size() - getPosition(e);
	}

	/**
	 * Get the ranks array.
	 * 
	 * @return the ranks array.
	 */
	public Map<Event, double[]> getRanks() {
		return ranks;
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
	 * Generates a hash representation of this solution. This hash is only
	 * guaranteed to be unique for a single run.
	 * 
	 * @return the hash.
	 */
	public String hash() {
		StringBuilder hash = new StringBuilder();
		for (Event e : events) {
			hash.append(e.id);
			hash.append(':');
		}
		return hash.toString();
	}

	/**
	 * Sets the score for this solution.
	 * 
	 * @param score
	 *            the score.
	 */
	public void setScore(final double score) {
		this.score = score;

		// create our ranks
		if (ranks == null) {
			ranks = Maps.newHashMap();
			int size = events.size();
			for (Event e : events) {
				double[] array = new double[size];
				Arrays.fill(array, -1);
				ranks.put(e, array);
			}
		}

		// update our ranks
		if (score <= best) {
			best = score;
			for (Event e : events) {
				ranks.get(e)[getPosition(e)] = score;
			}
		}
	}
}
