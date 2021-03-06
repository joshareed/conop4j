package org.andrill.conop.analysis.charts

import org.andrill.conop.analysis.Conop4jOutput
import org.andrill.conop.core.cli.CliCommand

class RangeChartCommand implements CliCommand {

	@Override
	void execute(List args) {
		if (!args || args.size() < 2) {
			println "Usage: range-chart <solution> <output>"
			System.exit(0)
		}

		def source = args[0].contains('http') ? new URL(args[0]).text : new File(args[0]).text
		Conop4jOutput output = new Conop4jOutput(source)

		RangeChart chart = new RangeChart()
		chart.build(output.solution, new File(args[1]))
	}

	@Override
	String getDescription() {
		"renders a range chart of a solution"
	}

	@Override
	String getHelp() {
		"""\trange-chart <solution> <output> - renders a range chart of the specified solution to a file"""
	}

	@Override
	String getName() {
		"range-chart"
	}
}
