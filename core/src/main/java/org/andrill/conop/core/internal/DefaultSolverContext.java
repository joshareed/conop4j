package org.andrill.conop.core.internal;

import java.util.Map;

import org.andrill.conop.core.solver.SolverContext;

import com.google.common.collect.Maps;

@SuppressWarnings("unchecked")
public class DefaultSolverContext implements SolverContext {
	protected Map<Class<?>, Object> cache = Maps.newConcurrentMap();

	@Override
	public <O> O get(Class<? super O> type) {
		return (O) cache.get(type);
	}

	@Override
	public <O> O put(Class<? super O> type, O obj) {
		return (O) cache.put(type, obj);
	}

}
