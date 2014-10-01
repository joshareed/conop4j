package org.andrill.conop.analysis.charts

import org.andrill.conop.analysis.Conop4jOutput
import org.andrill.conop.core.cli.CliCommand

class CrossPlotChartCommand implements CliCommand {
	@Override
	void execute(List args) {
		if (!args || args.size() < 3) {
			println "Usage: cross-plot-chart <solution 1> <solution 2> <output>"
			System.exit(0)
		}

		def source1 = args[0].contains('http') ? new URL(args[0]).text : new File(args[0]).text
		Conop4jOutput output1 = new Conop4jOutput(source1)

		def source2 = args[1].contains('http') ? new URL(args[0]).text : new File(args[1]).text
		Conop4jOutput output2 = new Conop4jOutput(source2)

		def chart = new CrossPlotChart()
		chart.build(output1.solution, output2.solution, new File(args[2]))
	}

	@Override
	String getDescription() {
		"renders a cross plot chart of two solutions"
	}

	@Override
	String getHelp() {
		"""\tcross-plot-chart <solution 1> <solution 2> <output> - renders a cross plot chart of two solutions to a file"""
	}

	@Override
	String getName() {
		"cross-plot-chart"
	}
}
