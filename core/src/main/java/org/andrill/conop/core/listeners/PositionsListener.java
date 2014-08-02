package org.andrill.conop.core.listeners;

import org.andrill.conop.core.Solution;

public class PositionsListener extends AbstractListener {
	protected PositionsMatrix matrix = null;

	@Override
	public void tried(double temp, Solution current, Solution best) {
		if (matrix == null) {
			matrix = new PositionsMatrix(current.getDataset());
			context.put(PositionsMatrix.class, matrix);
		}

		matrix.update(current);
	}
}
