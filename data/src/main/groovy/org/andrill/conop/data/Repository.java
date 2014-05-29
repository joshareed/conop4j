package org.andrill.conop.data;

import java.util.List;
import java.util.Map;

public interface Repository {

	Map<?,?> getLocation(String locationId);
	
	List<Map<?,?>> getObservations(String locationId);
}
