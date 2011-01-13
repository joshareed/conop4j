package org.andrill.conop4j.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.andrill.conop4j.Event;
import org.andrill.conop4j.Solution;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Collects statistics about each events min and max ranks.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class RanksListener implements Listener {
	private Map<Event, double[]> ranks;
	private double score = -1;

	/**
	 * Gets the max rank for the specified event.
	 * 
	 * @param e
	 *            the event.
	 * @return the max rank.
	 */
	public int getMax(final Event e) {
		double[] array = ranks.get(e);
		if (array == null) {
			return -1;
		}

		int i = 0;
		while ((array[i] != score) && (i < array.length)) {
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
	public int getMin(final Event e) {
		double[] array = ranks.get(e);
		if (array == null) {
			return -1;
		}

		int i = array.length - 1;
		while ((array[i] != score) && (i >= 0)) {
			i--;
		}
		return array.length - i;
	}

	/**
	 * Get the ranks array.
	 * 
	 * @return the ranks array.
	 */
	public Map<Event, double[]> getRanks() {
		return ranks;
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if (ranks == null) {
			ranks = Maps.newHashMap();
			int size = current.getEvents().size();
			for (Event e : current.getEvents()) {
				double[] array = new double[size];
				Arrays.fill(array, -1);
				ranks.put(e, array);
			}
			score = best.getScore();
		}

		// only update the ranks if the score is better
		if (current.getScore() <= score) {
			score = current.getScore();
			for (Event e : current.getEvents()) {
				ranks.get(e)[current.getPosition(e)] = score;
			}
		}
	}

	/**
	 * Write the ranks to a file.
	 * 
	 * @param file
	 *            the file.
	 */
	public void writeTo(final File file) {
		DecimalFormat D = new DecimalFormat("0.00");

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for (Entry<Event, double[]> e : ranks.entrySet()) {
				writer.write(e.getKey().getName() + "\t");
				double[] ranks = e.getValue();
				for (int i = 0; i < ranks.length - 1; i++) {
					writer.write(D.format(ranks[i]) + "\t");
				}
				writer.write(D.format(ranks[ranks.length - 1]) + "\n");
			}
		} catch (IOException e) {
			// do nothing
		} finally {
			Closeables.closeQuietly(writer);
		}
	}
}
