package org.andrill.conop.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A solution from a CONOP4J run.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CONOP4JSolution implements Solution {
	public static void main(final String[] args) {
		Solution solution = new CONOP4JSolution(new File("test/foo/keepers/solution1.csv"));
		for (Map<String, String> e : solution.getEvents()) {
			System.out.println(e);
		}
	}

	protected final List<Map<String, String>> events;
	protected final String name;

	protected final List<Map<String, String>> sections;

	public CONOP4JSolution(final File csv) {
		name = csv.getName();
		events = Lists.newArrayList();
		sections = Lists.newArrayList();
		parse(csv);
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

	protected void parse(final File csv) {
		String line = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
			// parse the header
			line = reader.readLine();
			if (line == null) {
				return;
			}
			String[] header = line.split("\t");
			for (int i = 4; i < header.length; i += 2) {
				Map<String, String> section = Maps.newHashMap();
				section.put("id", "" + (((i - 4) / 2) + 1));
				section.put("name", header[i].substring(0, header[i].length() - 4));
				sections.add(section);
			}
			while (((line = reader.readLine()) != null)) {
				String[] split = line.split("\t");
				if (!"Total".equals(split[0])) {
					Map<String, String> event = Maps.newHashMap();
					String name = strip(split[0]);
					event.put("name", name.substring(0, name.length() - 4));
					event.put("type", name.substring(name.length() - 3));
					event.put("solution", split[1]);
					event.put("rank", split[1]);
					event.put("rankmin", split[2]);
					event.put("rankmax", split[3]);
					for (int i = 4; i < split.length; i += 2) {
						event.put("observed." + (((i - 4) / 2) + 1), split[i]);
						event.put("placed." + (((i - 4) / 2) + 1), split[i + 1]);
					}
					events.add(event);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String strip(final String str) {
		if (str.startsWith("'") || str.startsWith("\"")) {
			return str.substring(1, str.length() - 1);
		} else {
			return str;
		}
	}
}
