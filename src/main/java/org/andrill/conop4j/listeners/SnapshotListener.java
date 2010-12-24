package org.andrill.conop4j.listeners;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;

import org.andrill.conop4j.CONOP.Listener;
import org.andrill.conop4j.Simulation;
import org.andrill.conop4j.Solution;

import com.google.common.io.Closeables;

public class SnapshotListener implements Listener {
	private static final DecimalFormat D = new DecimalFormat("0.00");
	protected long iteration = 0;
	protected long last = 0;
	protected long start = -1;
	protected Writer writer;

	public SnapshotListener() {
		try {
			writer = new BufferedWriter(new FileWriter(Simulation.getFile("snapshot.csv")));
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					Closeables.closeQuietly(writer);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		if (start == -1) {
			start = System.currentTimeMillis();
		}
		iteration++;

		// log a snapshot every minute
		long min = (System.currentTimeMillis() - start) / 60000;
		if (min > last) {
			last = min;
			try {
				writer.write(min + "\t" + iteration + "\t" + D.format(temp) + "\t" + D.format(best.getScore()) + "\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
