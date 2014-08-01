package org.andrill.conop.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A solution from a CONOP4J dataset.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CONOP4JSolution implements Solution {
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

	protected void parse(final File file) {
		try (CSVReader csv = new CSVReader(new BufferedReader(new FileReader(file)), '\t')) {
			// parse the header
			String[] header = csv.readNext();
			for (int i = 4; i < header.length; i += 2) {
				Map<String, String> section = Maps.newHashMap();
				section.put("id", "" + (((i - 4) / 2) + 1));
				section.put("name", header[i].substring(0, header[i].length() - 4));
				sections.add(section);
			}
			String[] row;
			while (((row = csv.readNext()) != null)) {
				if (!"Total".equals(row[0])) {
					Map<String, String> event = Maps.newHashMap();
					String name = strip(row[0]);
					event.put("name", name.substring(0, name.length() - 4));
					event.put("type", name.substring(name.length() - 3));
					event.put("solution", row[1]);
					event.put("rank", row[1]);

					if (row.length >= 4) {
						event.put("rankmin", row[2]);
						event.put("rankmax", row[3]);
					} else {
						event.put("rankmin", row[1]);
						event.put("rankmax", row[1]);
					}

					for (int i = 4; i < row.length; i += 2) {
						event.put("observed." + (((i - 4) / 2) + 1), row[i]);
						event.put("placed." + (((i - 4) / 2) + 1), row[i + 1]);
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
