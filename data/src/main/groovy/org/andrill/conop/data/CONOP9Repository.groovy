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
	protected Dataset dataset

	CONOP9Repository(File root, boolean overrideWeights = false) {
		dataset = loadCONOP9Run(root, overrideWeights)
	}

	CONOP9Repository(File locations, File events, File observations, boolean overrideWeights = false) {
		dataset = loadCONOP9Run(locations, events, observations, overrideWeights)
	}

	List<Location> getLocations() {
		dataset.locations.asList();
	}

	Location getLocation(String locationId) {
		dataset.locations.find { it.name == locationId }
	}

	/**
	 * Load a CONOP9 dataset from the specified directory. This assumes the standard
	 * filenames of sections.sct, events.evt, and loadfile.dat.
	 *
	 * @param dir
	 *            the dataset directory.
	 * @param overrideWeights
	 *            override the specified weights.
	 * @return the dataset.
	 */
	public static Dataset loadCONOP9Run(final File dir, final boolean overrideWeights = false) {
		return loadCONOP9Run(new File(dir, "sections.sct"), new File(dir, "events.evt"), new File(dir, "loadfile.dat"),
		overrideWeights);
	}

	/**
	 * Load a CONOP9 dataset from the specified files.
	 *
	 * @param sectionFile
	 *            the sections file.
	 * @param eventFile
	 *            the events file.
	 * @param loadFile
	 *            the observations/load file.
	 * @param overrideWeights
	 *            override the specified weights.
	 * @return the dataset.
	 */
	public static Dataset loadCONOP9Run(final File sectionFile, final File eventFile, final File loadFile, final boolean overrideWeights = false) {

		// parse section names
		Map<String, String> sectionNames = Maps.newHashMap();
		for (List<String> row : parse(sectionFile)) {
			sectionNames.put(row.get(0), row.get(3));
		}

		// parse event names
		Map<String, String> eventNames = Maps.newHashMap();
		for (List<String> row : parse(eventFile)) {
			eventNames.put(row.get(0), row.get(2));
		}

		// parse our load file
		Map<String, Event> events = Maps.newHashMap();
		Multimap<String, Observation> observations = HashMultimap.create();
		for (List<String> row : parse(loadFile)) {
			String id = row.get(0);
			String type = row.get(1);
			String section = row.get(2);
			BigDecimal level = new BigDecimal(row.get(3));
			double weightUp = Double.parseDouble(row.get(6));
			double weightDn = Double.parseDouble(row.get(7));
			String key = id + "_" + type;
			Event event = events.get(key);
			if (event == null) {
				String name = eventNames.get(id).replaceAll("  ", " ");
				if (type.equals("1")) {	// 1 = FAD, 2 = LAD
					Event lad = new DefaultEvent(name + " LAD");
					event = new DefaultEvent(name + " FAD");
					events.put(id + "_2", lad);
				} else if (type.equals("3")) {
					event = new DefaultEvent(name + " MID", events.get(id + "_1"), events.get(id + "_2"));
				} else if (type.equals("4")) {
					event = new DefaultEvent(name + " ASH");
				} else if (type.equals("5")) {
					event = new DefaultEvent(name + " AGE");
				}
				events.put(key, event);
			}

			// override weights
			if (overrideWeights) {
				if ("1".equals(type)) {
					weightUp = 1000000;
				} else if ("2".equals(type)) {
					weightDn = 1000000;
				} else if ("4".equals(type) || "5".equals(type)) {
					weightUp = 1000000;
					weightDn = 1000000;
				}
			}

			// check for uniqueness
			boolean unique = true;
			for (Observation o : observations.get(section)) {
				if (event.equals(o.getEvent())) {
					System.err.println("Event " + o.getEvent().getName() + " (" + id + ") is not unique in section "
							+ section);
					unique = false;
				}
			}
			if (unique) {
				observations.put(section, new DefaultObservation(event, level, weightUp, weightDn));
			}
		}

		// create all our sections
		List<Location> sections = Lists.newArrayList();
		for (String key : observations.keySet()) {
			sections.add(new DefaultLocation(sectionNames.get(key), Lists.newArrayList(observations.get(key))));
		}

		// create the dataset
		return new DefaultDataset(sections);
	}

	/**
	 * Parse the specified CONOP formatted file.
	 *
	 * @param file
	 *            the file.
	 * @return the parsed lines.
	 */
	protected static List<List<String>> parse(final File file) {
		if (!file.exists()) {
			throw new IllegalArgumentException(file.getAbsolutePath() + " does not exist!");
		}
		List<List<String>> parsed = Lists.newArrayList();

		BufferedReader reader = new BufferedReader(new FileReader(file))
		try  {
			String line = null;
			while ((line = reader.readLine()) != null) {
				List<String> row = parseLine(line.trim());
				if (row.size() > 0) {
					parsed.add(row);
				}
			}
		} catch (IOException e) {
			// do nothing
		}

		return parsed;
	}

	/**
	 * Parse the specified line.
	 *
	 * @param line
	 *            the line.
	 * @return the parsed line.
	 */
	protected static List<String> parseLine(final String line) {
		List<String> list = new ArrayList<String>();
		boolean inQuote = false;
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if ((c == '\'') || (c == '"')) {
				if (inQuote) {
					list.add(buffer.toString());
					inQuote = false;
				} else {
					inQuote = true;
				}
				buffer = new StringBuilder();
			} else if ((c == ' ') || (c == '\t')) {
				if (inQuote) {
					buffer.append(c);
				} else if (!"".equals(buffer.toString())) {
					list.add(buffer.toString());
					buffer = new StringBuilder();
				} else {
					buffer = new StringBuilder();
				}
			} else {
				buffer.append(c);
			}
		}
		if (!"".equals(buffer.toString())) {
			list.add(buffer.toString());
		}
		return list;
	}
}
