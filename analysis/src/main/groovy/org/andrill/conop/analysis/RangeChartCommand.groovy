package org.andrill.conop.analysis

import org.andrill.conop.core.cli.CliCommand

class RangeChartCommand implements CliCommand {

	@Override
	void execute(List args) {
		if (!args || args.size() < 2) {
			println "Usage: range-chart <url> <file>"
			System.exit(0)
		}

		println "Foo"

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
		"""\trange-chart <url> <file> - renders a range chart of the specified solution to a file"""
	}

	@Override
	String getName() {
		"range-chart"
	}
}
