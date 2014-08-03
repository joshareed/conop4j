package org.andrill.conop.core.internal;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.solver.SolverContext;

import com.google.common.collect.Maps;

@SuppressWarnings("unchecked")
public class DefaultSolverContext implements SolverContext {
	protected Map<Class<?>, Object> cache = Maps.newConcurrentMap();
	protected ReadWriteLock bestLock = new ReentrantReadWriteLock();
	protected Solution best = null;

	@Override
	public Solution getBest() {
		try {
			bestLock.readLock().lock();
			return best;
		} finally {
			bestLock.readLock().unlock();
		}
	}

	@Override
	public Solution setBest(Solution best) {
		try {
			bestLock.writeLock().lock();
			if (this.best == null || this.best.getScore() > best.getScore()) {
				this.best = best;
			}
			return best;
		} finally {
			bestLock.writeLock().unlock();
		}
	}

	@Override
	public Dataset getDataset() {
		return get(Dataset.class);
	}

	@Override
	public Dataset setDataset(Dataset dataset) {
		return put(Dataset.class, dataset);
	}

	@Override
	public <O> O get(Class<? super O> type) {
		return (O) cache.get(type);
	}

	@Override
	public <O> O put(Class<? super O> type, O obj) {
		return (O) cache.put(type, obj);
	}

}
