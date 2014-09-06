package org.andrill.conop.data;

import java.util.List;

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

	/**
	 * Gets all {@link Location}.
	 * 
	 * Implementors may throw an {@link UnsupportedOperationException} if they
	 * cannot reliably enumerate all available locations.
	 * 
	 * @return the list of all locations in this repository.
	 */
	List<Location> getLocations();
}
