package org.andrill.conop.pp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Loads information about a CONOP run.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class RunInfo {
	public static class Config {
		public String eventDictionary = "event.dic";
		public String events = "events.evt";
		public String extant = "extant.dic";
		public String observations = "loadfile.dat";
		public String output = "outevnt.txt";
		public String placements = "plcd.dat";
		public String sections = "sections.sct";
		public String solution = "soln.dat";
	}

	public static void main(final String[] args) {
		RunInfo run = new RunInfo(new File("test"));
		for (List<String> row : RunInfo.parse(new File("test/extant.csv"))) {
			System.out.println(run.findEvent("name", row.get(0)));
		}
	}

	/**
	 * Parse the specified file.
	 * 
	 * @param file
	 *            the file.
	 * @return the parsed lines.
	 */
	public static List<List<String>> parse(final File file) {
		List<List<String>> parsed = Lists.newArrayList();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				List<String> row = parseLine(line.trim());
				if (row.size() > 0) {
					parsed.add(row);
				}
			}
		} catch (IOException e) {
			// do nothing
		} finally {
			Closeables.closeQuietly(reader);
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

	protected final Config config;
	protected final File dir;
	protected List<Map<String, String>> events;
	protected final String name;
	protected List<Map<String, String>> observations;
	protected List<Map<String, String>> sections;

	/**
	 * Create a new RunInfo for the specified run directory.
	 * 
	 * @param dir
	 *            the run directory.
	 */
	public RunInfo(final File dir) {
		this(dir.getName(), dir, new Config());
	}

	/**
	 * Create a new RunInfo for the specified run directory.
	 * 
	 * @param name
	 *            the run name.
	 * @param dir
	 *            the run directory.
	 */
	public RunInfo(final String name, final File dir) {
		this(name, dir, new Config());
	}

	/**
	 * Create a new RunInfo for the specified run directory.
	 * 
	 * @param name
	 *            the run name.
	 * @param dir
	 *            the run directory.
	 * @param config
	 *            the config.
	 */
	public RunInfo(final String name, final File dir, final Config config) {
		this.name = name;
		this.dir = dir;
		this.config = config;
		loadEvents();
		loadSections();
		loadObservations();
		loadFortranNumbers();
		loadSolution();
		loadPlacements();
		loadExtants();
	}

	protected List<Map<String, String>> filter(final List<Map<String, String>> list, final String key,
			final String value) {
		List<Map<String, String>> found = new ArrayList<Map<String, String>>();
		for (Map<String, String> map : list) {
			if (value.equals(map.get(key))) {
				found.add(map);
			}
		}
		return found;
	}

	/**
	 * Find all events that match the specified key-value pair.
	 * 
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 * @return the list of all matching events.
	 */
	public List<Map<String, String>> filterEvents(final String key, final String value) {
		return filter(events, key, value);
	}

	/**
	 * Finds all observations that match the specified key-value pair.
	 * 
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 * @return the list of all matching observations.
	 */
	public List<Map<String, String>> filterObservations(final String key, final String value) {
		return filter(observations, key, value);
	}

	/**
	 * Finds all sections that match the specified key-value pair.
	 * 
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 * @return the list of all matching sections.
	 */
	public List<Map<String, String>> filterSections(final String key, final String value) {
		return filter(sections, key, value);
	}

	protected Map<String, String> find(final List<Map<String, String>> list, final String key, final String value) {
		for (Map<String, String> map : list) {
			if (value.equals(map.get(key))) {
				return map;
			}
		}
		return null;
	}

	protected Map<String, String> find(final List<Map<String, String>> list, final String key1, final String value1,
			final String key2, final String value2) {
		for (Map<String, String> map : list) {
			if (value1.equals(map.get(key1)) && value2.equals(map.get(key2))) {
				return map;
			}
		}
		return null;
	}

	/**
	 * Find the first event that matches the specified key-value pair.
	 * 
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 * @return the first matching event or null.
	 */
	public Map<String, String> findEvent(final String key, final String value) {
		return find(events, key, value);
	}

	/**
	 * Find the first observation that matches the specified key-value pair.
	 * 
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 * @return the first matching observation or null.
	 */
	public Map<String, String> findObservation(final String key, final String value) {
		return find(events, key, value);
	}

	/**
	 * Find the first section that matches the specified key-value pair.
	 * 
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 * @return the first matching section or null.
	 */
	public Map<String, String> findSection(final String key, final String value) {
		return find(sections, key, value);
	}

	/**
	 * Get all events.
	 * 
	 * @return the list of events.
	 */
	public List<Map<String, String>> getEvents() {
		return events;
	}

	/**
	 * Gets the name of this run.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets all observations.
	 * 
	 * @return the list of observations.
	 */
	public List<Map<String, String>> getObservations() {
		return observations;
	}

	/**
	 * Gets all sections.
	 * 
	 * @return the list of sections.
	 */
	public List<Map<String, String>> getSections() {
		return sections;
	}

	protected void loadEvents() {
		// parse events.evt file
		events = Lists.newArrayList();
		for (List<String> row : parse(new File(dir, config.events))) {
			Map<String, String> map = Maps.newHashMap();
			map.put("id", row.get(0));
			map.put("code", row.get(1));
			map.put("name", row.get(2).replaceAll("  ", " "));
			events.add(map);
		}

		// parse event.dic if it exists to extract ages
		File dict = new File(dir, config.eventDictionary);
		if (dict.exists()) {
			for (List<String> row : parse(dict)) {
				Map<String, String> map = findEvent("code", row.get(0));
				if (map != null) {
					if (Double.parseDouble(row.get(4)) > 0) {
						map.put("agemin", row.get(4));
					}

					if (Double.parseDouble(row.get(5)) > 0) {
						map.put("agemax", row.get(5));
					}
				}
			}
		}
	}

	protected void loadExtants() {
		File extants = new File(dir, config.extant);
		if (!extants.exists()) {
			return;
		}

		for (List<String> row : parse(extants)) {
			boolean extant = "1".equals(row.get(2));
			if (extant) {
				Map<String, String> event = find(events, "code", row.get(0), "typename", "LAD");
				if (event != null) {
					event.put("agemin", "0");
					event.put("agemax", "0");
				}
			}
		}
	}

	protected void loadFortranNumbers() {
		// look for specific output files
		File file = new File(dir, config.output);
		if (!file.exists()) {
			return;
		}

		// build our regex
		Pattern regex = Pattern.compile("\\[(.*?)\\].*\\(fortran # (\\d+)\\)");

		// read line by line and look for our regular expression
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = regex.matcher(line);
				if (matcher.find()) {
					String code = matcher.group(1);
					String fortran = matcher.group(2);
					if (line.contains("LAD") || line.contains("FAD")) {
						find(events, "code", code, "type", line.contains("FAD") ? "1" : "2").put("fortran", fortran);
					} else {
						find(events, "code", code).put("fortran", fortran);
					}
				}
			}
		} catch (IOException e) {
			// ignore
		} finally {
			Closeables.closeQuietly(reader);
		}
	}

	protected void loadObservations() {
		observations = Lists.newArrayList();
		for (List<String> row : parse(new File(dir, config.observations))) {
			Map<String, String> map = Maps.newHashMap();
			String id = row.get(0);
			String type = row.get(1);
			map.put("event.id", id);
			List<Map<String, String>> filtered = filterEvents("id", id);
			Map<String, String> event = filtered.get(0);
			map.put("event.code", event.get("code"));
			map.put("event.name", event.get("name"));
			map.put("event.type", type);
			map.put("event.typename", lookupType(type));
			String sid = row.get(2);
			map.put("section.id", sid);
			Map<String, String> section = findSection("id", sid);
			map.put("section.code", section.get("code"));
			map.put("section.name", section.get("name"));
			map.put("level", row.get(3));
			map.put("pos", row.get(4));
			map.put("allowed", row.get(5));
			map.put("weightup", row.get(6));
			map.put("weightdn", row.get(7));
			observations.add(map);
			if ((filtered.size() == 1) && (filtered.get(0).get("type") == null)) {
				filtered.get(0).put("type", type);
				filtered.get(0).put("typename", lookupType(type));
			} else if (find(filtered, "type", type) == null) {
				Map<String, String> cloned = Maps.newHashMap(filtered.get(0));
				cloned.put("type", type);
				cloned.put("typename", lookupType(type));
				events.add(cloned);
			}
		}
	}

	protected void loadPlacements() {
		for (List<String> row : parse(new File(dir, config.placements))) {
			String id = row.get(0);
			String type = row.get(1);
			Map<String, String> event = find(events, "id", id, "type", type);
			for (int i = 2; i < row.size(); i++) {
				event.put("placed." + (i - 1), row.get(i));
			}
		}
	}

	protected void loadSections() {
		sections = Lists.newArrayList();
		for (List<String> row : parse(new File(dir, config.sections))) {
			Map<String, String> map = Maps.newHashMap();
			map.put("id", row.get(0));
			map.put("code", row.get(1));
			map.put("name", row.get(3));
			sections.add(map);
		}
	}

	protected void loadSolution() {
		for (List<String> row : parse(new File(dir, config.solution))) {
			find(events, "id", row.get(0), "type", row.get(1)).put("solution", row.get(2));
		}
	}

	protected String lookupType(final String type) {
		switch (Integer.parseInt(type)) {
		case 1:
			return "FAD";
		case 2:
			return "LAD";
		case 3:
			return "MID";
		case 4:
			return "ASH";
		case 5:
			return "AGE";
		default:
			return null;
		}
	}
}
