package org.andrill.conop4j.objective;

import java.util.Map;

import org.andrill.conop4j.Event;
import org.andrill.conop4j.Section;
import org.andrill.conop4j.Solution;

import com.google.common.collect.Maps;

/**
 * A test scoring function.
 * 
 * @author Josh Reed (jareed@andrill.org0
 */
public class PlacementPenalty implements ObjectiveFunction {
	protected Map<Section, SectionPlacement> placements = Maps.newHashMap();

	@Override
	public double score(final Solution solution) {
		double score = 0;
		for (final Section s : solution.getRun().getSections()) {
			SectionPlacement placement = placements.get(s);
			if (placement == null) {
				placement = new SectionPlacement(s);
				placements.put(s, placement);
			}
			placement.reset();
			for (Event e : solution.getEvents()) {
				placement.place(e);
			}
			score += placement.getPenalty();
		}
		return score;
	}
}
