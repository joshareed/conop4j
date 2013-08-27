package org.andrill.conop.analysis;

import java.io.File;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.andrill.conop.analysis.SummarySpreadsheet.Summary;

import com.google.common.collect.Lists;

/**
 * CLI and GUI interface to the CONOP post-processing library.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class PostProcess {

	public static void main(final String[] args) {
		// define our option parser
		OptionParser parser = new OptionParser() {
			{
				accepts("in", "the run directory").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
				accepts("out", "the output file").withRequiredArg().ofType(File.class).defaultsTo(new File("out.xls"));
				accepts("summary", "a comma-separated list of summary sheets").withRequiredArg().ofType(String.class)
						.withValuesSeparatedBy(',').defaultsTo("AgeAndPlacements");
			}
		};

		// parse our options
		OptionSet options = parser.parse(args);

		// get our runs
		List<Solution> runs = Lists.newArrayList();
		for (Object f : options.valuesOf("in")) {
			File file = (File) f;
			if (file.isDirectory()) {
				runs.add(new CONOP9Solution(file));
			} else {
				runs.add(new CONOP4JSolution(file));
			}
		}

		// get the output file
		File out = (File) options.valueOf("out");

		// get our summary classes
		List<Summary> summaries = Lists.newArrayList();
		for (Object summary : options.valuesOf("summary")) {
			String name = summary.toString();
			String className = name.indexOf('.') > 0 ? name : "org.andrill.conop.analysis." + name;
			try {
				Class<?> clazz = Class.forName(className);
				summaries.add((Summary) clazz.newInstance());
			} catch (ClassNotFoundException e) {
				System.err.println("Summary class '" + className + "' not found");
				e.printStackTrace(System.err);
			} catch (InstantiationException e) {
				System.err.println("Unable to instantiate summary class '" + className + "'");
				e.printStackTrace(System.err);
			} catch (IllegalAccessException e) {
				System.err.println("Unable to instantiate summary class '" + className + "'");
				e.printStackTrace(System.err);
			}
		}

		// write our summary spreadsheet
		StringBuilder names = new StringBuilder("(");
		for (int i = 0; i < runs.size(); i++) {
			names.append(runs.get(i).getName());
			if (i < (runs.size() - 1)) {
				names.append(", ");
			}
		}
		names.append(")");

		System.out.println("Post-processing CONOP runs in '" + names + "' using the following summaries:");
		for (Summary s : summaries) {
			System.out.println("  - " + s.getClass().getSimpleName());
		}
		System.out.println("Output going to: '" + out + "'");
		SummarySpreadsheet spreadsheet = new SummarySpreadsheet(summaries.toArray(new Summary[summaries.size()]));
		spreadsheet.write(out, runs.toArray(new Solution[0]));
	}
}
