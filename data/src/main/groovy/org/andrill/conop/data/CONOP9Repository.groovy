package org.andrill.conop.data

import org.andrill.conop.core.Dataset
import org.andrill.conop.core.Event
import org.andrill.conop.core.Location
import org.andrill.conop.core.Observation
import org.andrill.conop.core.internal.DefaultDataset
import org.andrill.conop.core.internal.DefaultEvent
import org.andrill.conop.core.internal.DefaultLocation
import org.andrill.conop.core.internal.DefaultObservation

import com.google.common.collect.HashMultimap
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Multimap

class CONOP9Repository implements Repository {
	public static final String LOCATIONS_FILE = 'sections.sct'
	public static final String EVENTS_FILE = 'events.evt'
	public static final String OBSERVATIONS_FILE = 'loadfile.dat'

	protected Dataset dataset

	CONOP9Repository(File root, boolean overrideWeights = false) {
		dataset = parseDataset(new File(root, LOCATIONS_FILE).text, new File(root, EVENTS_FILE).text, new File(root, OBSERVATIONS_FILE).text, overrideWeights)
	}

	CONOP9Repository(File locations, File events, File observations, boolean overrideWeights = false) {
		dataset = parseDataset(locations.text, events.text, observations.text, overrideWeights)
	}

	CONOP9Repository(URL root, boolean overrideWeights = false) {
		dataset = parseDataset(new URL(root, LOCATIONS_FILE).text, new URL(root, EVENTS_FILE).text, new URL(root, OBSERVATIONS_FILE).text, overrideWeights)
	}

	CONOP9Repository(URL locations, URL events, URL observations, boolean overrideWeights = false) {
		dataset = parseDataset(locations.text, events.text, observations.text, overrideWeights)
	}

	List<Location> getLocations() {
		dataset.locations.asList()
	}

	Location getLocation(String locationId) {
		dataset.locations.find { it.name == locationId }
	}

	Dataset getDataset() {
		dataset
	}

	protected Dataset parseDataset(String sectionsData, String eventsData, String observationsData, final boolean overrideWeights = false) {

		// parse section names
		Map<String, String> sectionNames = Maps.newHashMap()
		for (List<String> row : parseFile(sectionsData)) {
			sectionNames.put(row.get(0), row.get(3))
		}

		// parse event names
		Map<String, String> eventNames = Maps.newHashMap()
		for (List<String> row : parseFile(eventsData)) {
			eventNames.put(row.get(0), row.get(2))
		}

		// parse our load file
		Map<String, Event> events = Maps.newHashMap()
		Multimap<String, Observation> observations = HashMultimap.create()
		for (List<String> row : parseFile(observationsData)) {
			String id = row.get(0)
			String type = row.get(1)
			String section = row.get(2)
			BigDecimal level = new BigDecimal(row.get(3))
			double weightUp = Double.parseDouble(row.get(6))
			double weightDn = Double.parseDouble(row.get(7))
			String key = id + "_" + type
			Event event = events.get(key)
			if (event == null) {
				String name = eventNames.get(id).replaceAll("  ", " ")
				if (type.equals("1")) {	// 1 = FAD, 2 = LAD
					Event lad = new DefaultEvent(name + " LAD")
					event = new DefaultEvent(name + " FAD")
					events.put(id + "_2", lad)
				} else if (type.equals("3")) {
					event = new DefaultEvent(name + " MID", events.get(id + "_1"), events.get(id + "_2"))
				} else if (type.equals("4")) {
					event = new DefaultEvent(name + " ASH")
				} else if (type.equals("5")) {
					event = new DefaultEvent(name + " AGE")
				}
				events.put(key, event)
			}

			// override weights
			if (overrideWeights) {
				if ("1".equals(type)) {
					weightUp = 1000000
				} else if ("2".equals(type)) {
					weightDn = 1000000
				} else if ("4".equals(type) || "5".equals(type)) {
					weightUp = 1000000
					weightDn = 1000000
				}
			}

			// check for uniqueness
			boolean unique = true;
			for (Observation o : observations.get(section)) {
				if (event.equals(o.getEvent())) {
					System.err.println("Event " + o.getEvent().getName() + " (" + id + ") is not unique in section "
							+ section)
					unique = false
				}
			}
			if (unique) {
				observations.put(section, new DefaultObservation(event, level, weightUp, weightDn))
			}
		}

		// create all our sections
		List<Location> sections = Lists.newArrayList()
		for (String key : observations.keySet()) {
			sections.add(new DefaultLocation(sectionNames.get(key), Lists.newArrayList(observations.get(key))))
		}

		// create the dataset
		new DefaultDataset(sections)
	}

	protected List<List<String>> parseFile(String text) {
		List<List<String>> parsed = Lists.newArrayList()
		text.eachLine { line ->
			List<String> row = parseLine(line.trim())
			if (row.size() > 0) {
				parsed.add(row)
			}
		}
		parsed
	}

	protected List<String> parseLine(final String line) {
		List<String> list = new ArrayList<String>()
		boolean inQuote = false
		StringBuilder buffer = new StringBuilder()
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i)
			if ((c == '\'') || (c == '"')) {
				if (inQuote) {
					list.add(buffer.toString())
					inQuote = false
				} else {
					inQuote = true
				}
				buffer = new StringBuilder();
			} else if ((c == ' ') || (c == '\t')) {
				if (inQuote) {
					buffer.append(c)
				} else if (!"".equals(buffer.toString())) {
					list.add(buffer.toString())
					buffer = new StringBuilder()
				} else {
					buffer = new StringBuilder()
				}
			} else {
				buffer.append(c)
			}
		}
		if (!"".equals(buffer.toString())) {
			list.add(buffer.toString())
		}
		list
	}
}
