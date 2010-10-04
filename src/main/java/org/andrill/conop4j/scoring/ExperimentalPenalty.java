package org.andrill.conop4j.scoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.andrill.conop4j.Event;
import org.andrill.conop4j.Run;
import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;
import org.andrill.conop4j.constraints.ConstraintChecker;
import org.andrill.conop4j.constraints.EventChecker;
import org.andrill.conop4j.mutation.ConstrainedMutator;
import org.andrill.conop4j.mutation.MutationStrategy;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

/**
 * A test scoring function.
 * 
 * @author Josh Reed (jareed@andrill.org0
 */
public class ExperimentalPenalty implements ScoringFunction {

	public static void main(final String[] args) {
		// load the run
		Run run = Run.loadCONOP9Run(new File("/Users/jareed/Desktop/riley"));

		// load the solution
		List<Event> events = Lists.newArrayList();
		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new FileReader("/Users/jareed/Desktop/riley.csv"));
			while ((line = reader.readLine()) != null) {
				for (Event e : run.getEvents()) {
					if (line.equals(e.getName())) {
						events.add(0, e);
					}
				}
			}
		} catch (IOException ioe) {
			// ignore
		} finally {
			Closeables.closeQuietly(reader);
		}

		ScoringFunction scorer = new ExperimentalPenalty();
		MutationStrategy mutator = new ConstrainedMutator();
		ConstraintChecker constraints = new EventChecker();

		// calculate our best solution
		Solution best = new Solution(run, events);
		best.setScore(scorer.score(best));
		System.out.println("Best: " + best.getScore());

		for (int i = 0; i < 100000; i++) {
			Solution next = mutator.mutate(best);
			while (!constraints.isValid(next)) {
				next = mutator.mutate(best);
			}
			next.setScore(scorer.score(next));
			if (next.getScore() < best.getScore()) {
				best = next;
			}
		}

		System.out.println("Best: " + best.getScore());
	}

	@Override
	public Type getType() {
		return Type.PENALTY;
	}

	@Override
	public double score(final Solution solution) {
		double score = 0;
		for (Section s : solution.getRun().getSections()) {
			ExperimentalPlacement topDown = new ExperimentalPlacement(s);
			for (Event e : solution.getEvents()) {
				topDown.place(e);
			}
			score += topDown.getPenalty();
		}
		return score;
	}
}
