package org.andrill.conop.data;

import org.andrill.conop.core.Location;

/**
 * Defines the interface for a Repository.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Repository {

	/**
	 * Get a {@link Location} by id.
	 *
	 * @param locationId
	 *            the location id.
	 * @return the Location or null.
	 */
	Location getLocation(String locationId);
}
