package org.andrill.conop.search;

import java.math.BigDecimal;
import java.util.IdentityHashMap;

/**
 * Represents an observation of an event in a section.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Observation {
	private static final IdentityHashMap<BigDecimal, Double> doubleMap = new IdentityHashMap<BigDecimal, Double>() {
		private static final long serialVersionUID = 1L;

		@Override
		public Double get(final Object key) {
			Double value = super.get(key);
			if (value == null) {
				BigDecimal k = (BigDecimal) key;
				value = k.doubleValue();
				put(k, value);
			}
			return value;
		};
	};
	private static final IdentityHashMap<BigDecimal, Integer> intMap = new IdentityHashMap<BigDecimal, Integer>() {
		private static final long serialVersionUID = 1L;

		@Override
		public Integer get(final Object key) {
			Integer value = super.get(key);
			if (value == null) {
				BigDecimal k = (BigDecimal) key;
				value = k.multiply(THOUSAND).intValue();
				put(k, value);
			}
			return value;
		};
	};
	private static final BigDecimal THOUSAND = new BigDecimal(1000);
	protected final Event event;
	protected final BigDecimal level;
	protected final double weightDown;
	protected final double weightUp;

	/**
	 * Creates a new observation for the specified event.
	 * 
	 * @param event
	 *            the event.
	 * @param level
	 *            the level.
	 * @param weightUp
	 *            the up-weight.
	 * @param weightDown
	 *            the down-weight.
	 */
	public Observation(final Event event, final BigDecimal level, final double weightUp, final double weightDown) {
		this.event = event;
		this.level = level;
		this.weightUp = weightUp;
		this.weightDown = weightDown;
	}

	/**
	 * Gets the event.
	 * 
	 * @return the event.
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * Gets the level.
	 * 
	 * @return the level.
	 */
	public BigDecimal getLevel() {
		return level;
	}

	/**
	 * Gets the level as a double.
	 * 
	 * @return the level.
	 */
	public double getLevelDouble() {
		return doubleMap.get(level);
	}

	/**
	 * Gets the level as an integer.
	 * 
	 * @return the level.
	 */
	public int getLevelInt() {
		return intMap.get(level);
	}

	/**
	 * Gets the down-weight.
	 * 
	 * @return the down-weight.
	 */
	public double getWeightDown() {
		return weightDown;
	}

	/**
	 * Gets the up-weight.
	 * 
	 * @return the up-weight.
	 */
	public double getWeightUp() {
		return weightUp;
	}

	@Override
	public String toString() {
		return "Observation[event=" + event + ", level=" + level + ", weights=(" + weightUp + "," + weightDown + ")]";
	}
}
