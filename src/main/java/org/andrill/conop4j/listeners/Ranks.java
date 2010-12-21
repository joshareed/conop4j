package org.andrill.conop4j.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.andrill.conop4j.CONOP.Listener;
import org.andrill.conop4j.Event;
import org.andrill.conop4j.Solution;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Collects statistics about each events min and max ranks.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Ranks implements Listener {
	private Map<Event, double[]> ranks;

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
		}

		// update the ranks
		double score = current.getScore();
		for (Event e : current.getEvents()) {
			ranks.get(e)[current.getPosition(e)] = score;
		}
	}

	/**
	 * Write the ranks to a file.
	 * 
	 * @param file
	 *            the file.
	 */
	public void writeTo(final File file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for (Entry<Event, double[]> e : ranks.entrySet()) {
				writer.write(e.getKey().getName() + "\t");
				double[] ranks = e.getValue();
				for (int i = 0; i < ranks.length - 1; i++) {
					writer.write(ranks[i] + "\t");
				}
				writer.write(ranks[ranks.length - 1] + "\n");
			}
		} catch (IOException e) {
			// do nothing
		} finally {
			Closeables.closeQuietly(writer);
		}
	}
}
