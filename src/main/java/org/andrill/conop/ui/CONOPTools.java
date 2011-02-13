package org.andrill.conop.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

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
			getContentPane().add(tabs, BorderLayout.CENTER);
		}
	}

	protected static void cli(final String[] args) {

	}

	protected static void gui() {
		ToolFrame frame = new ToolFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
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
}
