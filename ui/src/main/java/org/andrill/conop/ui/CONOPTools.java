package org.andrill.conop.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.andrill.conop.analysis.PostProcess;
import org.andrill.conop.core.CONOP;
import org.andrill.conop.core.QNOP;

/**
 * The CONOP tools main class.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class CONOPTools {

	private static class ToolFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		private JTabbedPane tabs;

		private ToolFrame() {
			super("CONOP Tools");
			initComponents();
		}

		private void initComponents() {
			tabs = new JTabbedPane();
			tabs.addTab("Analysis", new AnalysisPanel());
			tabs.addTab("Run", new RunPanel());
			getContentPane().add(tabs, BorderLayout.CENTER);
		}
	}

	protected static void cli(final String[] args) {
		// get our command and args
		String cmd = args[0];
		String[] newargs = new String[args.length - 1];
		if (newargs.length > 0) {
			System.arraycopy(args, 1, newargs, 0, newargs.length);
		}

		if ("run".equals(cmd) || "conop".equals(cmd)) {
			CONOP.main(newargs);
		} else if ("qnop".equals(cmd)) {
			QNOP.main(newargs);
		} else if ("score".equals(cmd)) {
			score(newargs);
		} else if ("process".equals(cmd)) {
			PostProcess.main(newargs);
		} else {
			System.err.println("Unknown command: '" + cmd + "'");
		}
	}

	protected static void gui() {
		ToolFrame frame = new ToolFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	/**
	 * If no args specified, open up a GUI. Otherwise fire up the command line
	 * interface.
	 *
	 * @param args
	 *            the args.
	 */
	public static void main(final String[] args) {
		if (args.length == 0) {
			gui();
		} else {
			cli(args);
		}
	}

	protected static void score(final String[] args) {
		if ((args == null) || (args.length == 0)) {
			System.out.println("Usage: <simulation> <solution 1> ... [solution n]");
			System.exit(0);
		}

		// // get our run
		// Simulation simulation = new Simulation(new File(args[0]));
		// Run run = simulation.getRun();
		//
		// DecimalFormat D = new DecimalFormat("0.00");
		//
		// // score our solutions
		// for (int i = 1; i < args.length; i++) {
		// File f = new File(args[i]);
		// Solution solution = Solution.parse(run, f);
		//
		// System.out.println("---- " + f.getName() + " ----");
		// for (String key : Simulation.OBJECTIVES.keySet()) {
		// if (!"default".equals(key)) {
		// ObjectiveFunction objective = simulation
		// .lookup(key, ObjectiveFunction.class, Simulation.OBJECTIVES);
		// System.out.println("  " + key + ": " +
		// D.format(objective.score(solution)));
		// }
		// }
		// System.out.println("");
		// }
	}
}
