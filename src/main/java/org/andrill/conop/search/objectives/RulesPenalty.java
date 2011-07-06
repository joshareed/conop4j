package org.andrill.conop.search.objectives;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.andrill.conop.search.AbstractConfigurable;
import org.andrill.conop.search.Event;
import org.andrill.conop.search.Observation;
import org.andrill.conop.search.Section;
import org.andrill.conop.search.Solution;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;

public class RulesPenalty extends AbstractConfigurable implements ObjectiveFunction {
	static class Rule {
		Event first;
		Event second;
		int observed = 0;
		int total = 0;
		double penalty;
	}

	protected List<Rule> rules;
	protected ExecutorService pool;
	protected int procs = 1;
	protected double factor = 1000.0;
	protected int observations = 2;

	@Override
	public void configure(final Properties properties) {
		procs = Integer.parseInt(properties.getProperty("processors", ""
				+ Runtime.getRuntime().availableProcessors()));
		pool = MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newFixedThreadPool(procs));
		factor = Double.parseDouble(properties.getProperty("rule.factor", "1000"));
		observations = Integer.parseInt(properties.getProperty(
				"rule.observations", "2"));
	}

	protected Future<Double> execute(final List<Rule> list, final Solution solution) {
		if (pool == null) {
			pool = MoreExecutors
					.getExitingExecutorService((ThreadPoolExecutor) Executors
							.newFixedThreadPool(procs));
		}
		return pool.submit(new Callable<Double>() {
			public Double call() {
				double penalty = 0.0;
				for (Rule rule : list) {
					int i1 = solution.getPosition(rule.first);
					int i2 = solution.getPosition(rule.second);
					if (i1 > i2) {
						penalty += rule.penalty;
					}
				}
				return penalty;
			}
		});

	}

	protected void generateRules(final Solution solution) {
		rules = Lists.newArrayList();

		// generate our list of rules
		List<Event> events = solution.getEvents();
		for (int i = 0; i < (events.size() - 1); i++) {
			for (int j = i + 1; j < events.size(); j++) {
				Event e1 = solution.getEvent(i);
				Event e2 = solution.getEvent(j);
				Rule r1 = new Rule();
				r1.first = e1;
				r1.second = e2;
				Rule r2 = new Rule();
				r2.first = e2;
				r2.second = e1;

				// walk through our sections
				for (Section s : solution.getRun().getSections()) {
					Observation o1 = s.getObservation(e1);
					Observation o2 = s.getObservation(e2);
					if ((o1 != null) && (o2 != null)) {
						r1.total++;
						r2.total++;
						int compare = o1.getLevel().compareTo(o2.getLevel());
						if (compare < 0) {
							r1.observed++;
						} else if (compare > 0) {
							r2.observed++;
						}
					}
				}

				if (r1.observed > observations) {
					rules.add(r1);
				}
				if (r2.observed > observations) {
					rules.add(r2);
				}
			}
		}

		// calculate our weights
		for (Rule rule : rules) {
			double ratio = (double) rule.observed / (double) rule.total;
			rule.penalty = factor * ratio;
		}
	}

	public double score(final Solution solution) {
		if (rules == null) {
			generateRules(solution);
		}
		List<Future<Double>> results = Lists.newArrayList();
		int slice = rules.size() / procs;
		for (int i = 0; i < procs; i++) {
			int start = i * slice;
			int end = Math.min(start + slice, rules.size());
			results.add(execute(rules.subList(start, end), solution));
		}

		double penalty = 0;
		for (Future<Double> r : results) {
			try {
				penalty += r.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return penalty;
	}

	@Override
	public String toString() {
		return "Rules [" + procs + "]";
	}
}
