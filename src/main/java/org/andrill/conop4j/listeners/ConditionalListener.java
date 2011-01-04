package org.andrill.conop4j.listeners;

import org.andrill.conop4j.Solution;

public class ConditionalListener implements Listener {
	interface Condition {
		boolean evaluate(State state);
	}

	class State {
		long iteration = 0;
		double score = 0;
		double temp = Double.MAX_VALUE;
	}

	public static ConditionalListener iterationGt(final long value, final Listener l) {
		return new ConditionalListener(new Condition() {
			@Override
			public boolean evaluate(final State state) {
				return state.iteration >= value;
			}
		}, l);
	}

	public static ConditionalListener scoreGt(final double value, final Listener l) {
		return new ConditionalListener(new Condition() {
			@Override
			public boolean evaluate(final State state) {
				return state.score >= value;
			}
		}, l);
	}

	public static ConditionalListener scoreLt(final double value, final Listener l) {
		return new ConditionalListener(new Condition() {
			@Override
			public boolean evaluate(final State state) {
				return state.score <= value;
			}
		}, l);
	}

	public static ConditionalListener tempLt(final double value, final Listener l) {
		return new ConditionalListener(new Condition() {
			@Override
			public boolean evaluate(final State state) {
				return state.temp <= value;
			}
		}, l);
	}

	private final Condition condition;
	private final Listener listener;
	private final State state;

	public ConditionalListener(final Condition condition, final Listener listener) {
		this.condition = condition;
		this.listener = listener;
		state = new State();
	}

	@Override
	public void tried(final double temp, final Solution current, final Solution best) {
		// update our state
		state.iteration++;
		state.temp = temp;
		state.score = best.getScore();

		// evaluate our condition
		if (condition.evaluate(state)) {
			listener.tried(temp, current, best);
		}
	}
}
