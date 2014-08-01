package org.andrill.conop.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.andrill.conop.core.Event;
import org.andrill.conop.core.Dataset;
import org.andrill.conop.core.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SolutionParser {
	private static final Logger log = LoggerFactory.getLogger(SolutionParser.class);

	protected final Reader reader;

	public SolutionParser(final File file) throws FileNotFoundException {
		this.reader = new FileReader(file);
	}

	public SolutionParser(final Reader reader) {
		this.reader = reader;
	}

	public Solution parse(final Dataset dataset) throws IOException {
		Solution initial = null;
		try (CSVReader csv = new CSVReader(reader, '\t')) {
			// build a quick map of our eligible events
			Map<String, Event> lookup = Maps.newHashMap();
			for (Event e : dataset.getEvents()) {
				lookup.put(e.getName(), e);
			}

			// parse the CSV
			final List<Event> events = Lists.newArrayList();
			String[] row = csv.readNext();
			while ((row = csv.readNext()) != null) {
				Event e = lookup.get(row[0]);
				if (e == null) {
					log.error("No event with name '{}' found in dataset.", row[0]);
					return null;
				} else {
					events.add(e);
				}
			}
			initial = new Solution(dataset, events);
		}
		return initial;
	}
}
