package org.andrill.conop.pp;

import java.io.File;
import java.util.List;

import javax.swing.JFrame;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.andrill.conop.RunInfo;
import org.andrill.conop.pp.SummarySpreadsheet.Summary;
import org.andrill.conop.pp.ui.PostProcessFrame;

import com.google.common.collect.Lists;

/**
 * CLI and GUI interface to the CONOP post-processing library.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class PostProcess {

	private static void cli(final String[] args) {
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

		// get our input and output files/dirs
		File in = (File) options.valueOf("in");
		File out = (File) options.valueOf("out");

		// get our summary classes
		List<Summary> summaries = Lists.newArrayList();
		for (Object summary : options.valuesOf("summary")) {
			String name = summary.toString();
			String className = name.indexOf('.') > 0 ? name : "org.andrill.conop.pp." + name;
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
		System.out.println("Post-processing CONOP runs in '" + in + "' using the following summaries:");
		for (Summary s : summaries) {
			System.out.println("  - " + s.getClass().getSimpleName());
		}
		System.out.println("Output going to: '" + out + "'");
		SummarySpreadsheet spreadsheet = new SummarySpreadsheet(summaries.toArray(new Summary[summaries.size()]));
		spreadsheet.write(out, new RunInfo(in));
	}

	private static void gui() {
		PostProcessFrame frame = new PostProcessFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public static void main(final String[] args) {
		if (args.length == 0) {
			gui();
		} else {
			cli(args);
		}
	}
}
