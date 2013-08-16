package org.andrill.conop.analysis;

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

/**
 * A solution from a CONOP9 run.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CONOP9Solution implements Solution {
	public static class Config {
		public String curve = "curv.grd";
		public String eventDictionary = "event.dic";
		public String events = "events.evt";
		public String extant = "extant.dic";
		public String observations = "loadfile.dat";
		public String output = "outevnt.txt";
		public String placements = "plcd.dat";
		public String sections = "sections.sct";
		public String solution = "soln.dat";
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
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
	public CONOP9Solution(final File dir) {
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
	public CONOP9Solution(final String name, final File dir) {
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
	public CONOP9Solution(final String name, final File dir, final Config config) {
		this.name = name;
		this.dir = dir;
		this.config = config;
		loadEvents();
		loadSections();
		loadObservations();
		loadFortranNumbers();
		loadSolution();
		loadPlacements();
		loadRanks();
		loadExtants();
	}

	private List<Map<String, String>> filter(final List<Map<String, String>> list, final String key, final String value) {
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
	private List<Map<String, String>> filterEvents(final String key, final String value) {
		return filter(events, key, value);
	}

	private Map<String, String> find(final List<Map<String, String>> list, final String key, final String value) {
		for (Map<String, String> map : list) {
			if (value.equals(map.get(key))) {
				return map;
			}
		}
		return null;
	}

	private Map<String, String> find(final List<Map<String, String>> list, final String key1, final String value1,
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
	private Map<String, String> findEvent(final String key, final String value) {
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
	private Map<String, String> findSection(final String key, final String value) {
		return find(sections, key, value);
	}

	@Override
	public List<Map<String, String>> getEvents() {
		return events;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Map<String, String>> getSections() {
		return sections;
	}

	private void loadEvents() {
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

	private void loadExtants() {
		File extants = new File(dir, config.extant);
		if (!extants.exists()) {
			return;
		}

		for (List<String> row : parse(extants)) {
			boolean extant = "1".equals(row.get(2));
			if (extant) {
				Map<String, String> event = find(events, "code", row.get(0), "type", "LAD");
				if (event != null) {
					event.put("agemin", "0");
					event.put("agemax", "0");
				}
			}
		}
	}

	private void loadFortranNumbers() {
		// look for specific output files
		File file = new File(dir, config.output);
		if (!file.exists()) {
			return;
		}

		// build our regex
		Pattern regex = Pattern.compile("\\[(.*?)\\].*\\(fortran # (\\d+)\\)");

		// read line by line and look for our regular expression
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = regex.matcher(line);
				if (matcher.find()) {
					String code = matcher.group(1);
					String fortran = matcher.group(2);
					if (line.contains("FAD")) {
						find(events, "code", code, "type", "FAD").put("fortran", fortran);
					} else if (line.contains("LAD")) {
						find(events, "code", code, "type", "LAD").put("fortran", fortran);
					} else if (line.contains("MID")) {
						find(events, "code", code, "type", "MID").put("fortran", fortran);
					} else {
						find(events, "code", code).put("fortran", fortran);
					}
				}
			}
		} catch (IOException e) {
			// ignore
		}
	}

	private void loadObservations() {
		observations = Lists.newArrayList();
		for (List<String> row : parse(new File(dir, config.observations))) {
			Map<String, String> map = Maps.newHashMap();
			String id = row.get(0);
			String type = lookupType(row.get(1));
			map.put("event.id", id);
			List<Map<String, String>> filtered = filterEvents("id", id);
			Map<String, String> event = filtered.get(0);
			map.put("event.code", event.get("code"));
			map.put("event.name", event.get("name"));
			map.put("event.type", type);
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
			} else if (find(filtered, "type", type) == null) {
				Map<String, String> cloned = Maps.newHashMap(filtered.get(0));
				cloned.put("type", type);
				events.add(cloned);
			}
		}
	}

	private void loadPlacements() {
		for (List<String> row : parse(new File(dir, config.placements))) {
			String id = row.get(0);
			String type = lookupType(row.get(1));
			Map<String, String> event = find(events, "id", id, "type", type);
			for (int i = 2; i < row.size(); i++) {
				event.put("placed." + (i - 1), row.get(i));
			}
		}
	}

	private void loadRanks() {
		File file = new File(dir, config.curve);
		if (!file.exists()) {
			return;
		}

		// figure out the max and min rank
		double min = Double.MAX_VALUE;
		String marker = null;
		int fortran = 0;
		for (List<String> row : parse(file)) {
			fortran++;
			if (marker == null) {
				for (String value : row) {
					double d = Double.parseDouble(value);
					if (d < min) {
						min = d;
						marker = value;
					}
				}
			}
			int minRank = row.indexOf(marker) + 1;
			int maxRank = row.lastIndexOf(marker) + 1;

			// update our event
			Map<String, String> event = find(events, "fortran", "" + fortran);
			if (event != null) {
				event.put("rankmin", "" + minRank);
				event.put("rankmax", "" + maxRank);
			} else {
				System.out.println("No event with fortran number of " + fortran);
			}
		}
	}

	private void loadSections() {
		sections = Lists.newArrayList();
		for (List<String> row : parse(new File(dir, config.sections))) {
			Map<String, String> map = Maps.newHashMap();
			map.put("id", row.get(0));
			map.put("code", row.get(1));
			map.put("name", row.get(3));
			sections.add(map);
		}
	}

	private void loadSolution() {
		for (List<String> row : parse(new File(dir, config.solution))) {
			find(events, "id", row.get(0), "type", lookupType(row.get(1))).put("solution", row.get(2));
		}
	}

	private String lookupType(final String type) {
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
