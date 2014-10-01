package org.andrill.conop.analysis;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class AnnotatedSolution {
	public final ImmutableList<AnnotatedEvent> events;
	public double score = -1.0;

	public AnnotatedSolution(final List<AnnotatedEvent> events) {
		this.events = ImmutableList.copyOf(events);
	}

	public AnnotatedEvent getEvent(final int position) {
		return events.get(position);
	}

	public AnnotatedEvent getEvent(final String name) {
		for (AnnotatedEvent event : events) {
			if (event.getName().equals(name)) {
				return event;
			}
		}
		return null;
	}

	public ImmutableList<AnnotatedEvent> getEvents() {
		return this.events;
	}

	public double getScore() {
		return score;
	}

	public void setScore(final double score) {
		this.score = score;
	}
}
