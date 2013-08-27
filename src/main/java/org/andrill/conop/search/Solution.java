package org.andrill.conop.search;

import java.io.*;
import java.util.*;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A solution contains a set of events in a particular order.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Solution {
	private static double best = Double.MAX_VALUE;
	private static Map<Event, double[]> ranks;

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
	 * Parse a solution from a file.
	 * 
	 * @param run the run data.
	 * @param file the solution file.
	 * @return the solution or null if an error occurs.
	 */
	public static Solution parse(final Run run, final File file) {
		Solution initial = null;
		try (CSVReader csv = new CSVReader(new BufferedReader(new FileReader(file)), '\t')) {
			// build a quick map of our eligible events
			Map<String, Event> lookup = Maps.newHashMap();
			for (Event e : run.getEvents()) {
				lookup.put(e.getName(), e);
			}

			// parse the CSV
			final List<Event> events = Lists.newArrayList();
			String[] row = csv.readNext();
			while ((row = csv.readNext()) != null) {
				Event e = lookup.get(row[0]);
				if (e == null) {
					System.err.println("No event with name '" + row[0] + "'");
					return null;
				} else {
					events.add(e);
				}
			}
			initial = new Solution(run, events);
		} catch (FileNotFoundException e) {
			System.err.println("Invalid solution file: " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return initial;
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

		synchronized (Solution.class) {
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
