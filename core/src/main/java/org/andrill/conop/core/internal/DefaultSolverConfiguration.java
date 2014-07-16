package org.andrill.conop.core.internal;

import java.util.List;
import java.util.Map;

import org.andrill.conop.core.Configurable;
import org.andrill.conop.core.Configuration;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.constraints.Constraints;
import org.andrill.conop.core.constraints.NullConstraints;
import org.andrill.conop.core.listeners.Listener;
import org.andrill.conop.core.mutators.Mutator;
import org.andrill.conop.core.mutators.RandomMutator;
import org.andrill.conop.core.penalties.Penalty;
import org.andrill.conop.core.penalties.PlacementPenalty;
import org.andrill.conop.core.schedules.ExponentialSchedule;
import org.andrill.conop.core.schedules.Schedule;
import org.andrill.conop.core.solver.SolverConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DefaultSolverConfiguration implements SolverConfiguration {
	protected class ClassConfig<E> {
		Class<? extends E> clazz;
		Map<Object, Object> config;

		ClassConfig(final Class<? extends E> clazz, final Map<Object, Object> config) {
			this.clazz = clazz;
			this.config = config;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(DefaultSolverConfiguration.class);

	protected ClassConfig<? extends Constraints> constraints;
	protected Solution initial = null;
	protected List<ClassConfig<? extends Listener>> listeners = Lists.newArrayList();
	protected ClassConfig<? extends Mutator> mutator;
	protected ClassConfig<? extends Penalty> penalty;
	protected ClassConfig<? extends Schedule> schedule;

	public void configureConstraints(final Class<? extends Constraints> clazz, final Map<Object, Object> config) {
		constraints = new ClassConfig<Constraints>(clazz, config);
	}

	public void configureInitialSolution(final Solution initial) {
		this.initial = initial;
	}

	public void configureListener(final Class<? extends Listener> clazz, final Map<Object, Object> config) {
		listeners.add(new ClassConfig<Listener>(clazz, config));
	}

	public void configureMutator(final Class<? extends Mutator> clazz, final Map<Object, Object> config) {
		mutator = new ClassConfig<Mutator>(clazz, config);
	}

	public void configurePenalty(final Class<? extends Penalty> clazz, final Map<Object, Object> config) {
		penalty = new ClassConfig<Penalty>(clazz, config);
	}

	public void configureSchedule(final Class<? extends Schedule> clazz, final Map<Object, Object> config) {
		schedule = new ClassConfig<Schedule>(clazz, config);
	}

	@Override
	public Constraints getConstraints() {
		if (constraints == null) {
			configureConstraints(NullConstraints.class, Maps.newHashMap());
		}
		return instantiate(constraints);
	}

	@Override
	public Solution getInitialSolution() {
		return initial;
	}

	@Override
	public List<Listener> getListeners() {
		List<Listener> list = Lists.newArrayList();
		for (ClassConfig<? extends Listener> l : listeners) {
			list.add(instantiate(l));
		}
		return list;
	}

	@Override
	public Mutator getMutator() {
		if (mutator == null) {
			configureMutator(RandomMutator.class, Maps.newHashMap());
		}
		return instantiate(mutator);
	}

	@Override
	public Penalty getPenalty() {
		if (penalty == null) {
			configurePenalty(PlacementPenalty.class, Maps.newHashMap());
		}
		return instantiate(penalty);
	}

	@Override
	public Schedule getSchedule() {
		if (schedule == null) {
			configureSchedule(ExponentialSchedule.class, Maps.newHashMap());
		}
		return instantiate(schedule);
	}

	protected <E> E instantiate(final ClassConfig<E> classConfig) {
		try {
			E instance = classConfig.clazz.newInstance();
			if (instance instanceof Configurable) {
				Map<Object, Object> config = Maps.newHashMap();
				config.putAll(classConfig.config);

				((Configurable) instance).configure(new Configuration(config));
			}
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Unable to instantiate: " + classConfig.clazz.getCanonicalName());
		}
		return null;
	}
}
