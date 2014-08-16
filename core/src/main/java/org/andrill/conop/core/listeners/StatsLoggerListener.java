package org.andrill.conop.core.listeners;

import java.text.DecimalFormat;

import org.andrill.conop.core.Solution;
import org.andrill.conop.core.solver.SolverStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsLoggerListener extends PeriodicListener {
	private static final Logger log = LoggerFactory.getLogger(StatsLoggerListener.class);
	private static final DecimalFormat I = new DecimalFormat("0");
	private static final DecimalFormat D = new DecimalFormat("0.00");

	@Override
	protected void fired(double temp, long iteration, Solution current, Solution best) {
		SolverStats stats = context.get(SolverStats.class);
		if (stats != null) {
			log.info("Best: {} | Elapsed: {} min | Temperature: {} C | Scored: {}/{} | Throughput: {}/s",
					D.format(stats.best), I.format(stats.elapsed / 60), D.format(stats.temperature),
					I.format(stats.scored), I.format(stats.total), I.format(stats.total / stats.elapsed));
		}
	}

	@Override
	public String toString() {
		return "Stats Logger Listener";
	}
}
