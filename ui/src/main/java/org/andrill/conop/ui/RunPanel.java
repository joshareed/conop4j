package org.andrill.conop.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;

/**
 * A simple GUI for running a simulation.
 *
 * @author Josh Reed (jareed@andrill.org)
 */
public class RunPanel extends JPanel {
	private static final DecimalFormat DEC = new DecimalFormat("0.00");
	private static final long serialVersionUID = 1L;
	private JFileChooser fileChooser;
	private double initialTemp;
	private long iteration;
	private JLabel iterLabel;
	private JProgressBar progress;
	private boolean running = false;
	private JLabel scoreLabel;
	private File simulationFile;
	private long start;
	private JLabel tempLabel;
	private Thread thread;
	private JLabel timeLabel;

	public RunPanel() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("fill", "[][grow]", "[][][grow][][][][]"));

		fileChooser = new JFileChooser(new File("."));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(final File f) {
				return f.getName().endsWith(".sim");
			}

			@Override
			public String getDescription() {
				return "Simulation Files";
			}
		});

		progress = new JProgressBar();
		progress.setMaximum(105);

		final JButton runButton = new JButton("Run");
		final JButton fileButton = new JButton("< simulation file >");

		runButton.setEnabled(false);
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (!running) {
					running = true;
					fileButton.setEnabled(false);
					runButton.setEnabled(false);
					run();
				}
			}
		});

		add(new JLabel("Simulation:"));
		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (fileChooser.showOpenDialog(RunPanel.this) == JFileChooser.APPROVE_OPTION) {
					simulationFile = fileChooser.getSelectedFile();
					fileButton.setText(simulationFile.getName());
					runButton.setEnabled(true);
				} else {
					fileButton.setText("< simulation file >");
					simulationFile = null;
					runButton.setEnabled(false);
				}
			}
		});
		add(fileButton, "growx, wrap");

		add(runButton, "skip 1, align right, wrap");
		add(new JLabel(""), "wrap");

		add(new JLabel("Iteration:"));
		iterLabel = new JLabel("-");
		add(iterLabel, "wrap");

		add(new JLabel("Temp:"));
		tempLabel = new JLabel("-");
		add(tempLabel, "wrap");

		add(new JLabel("Time:"));
		timeLabel = new JLabel("-");
		add(timeLabel, "wrap");

		add(new JLabel("Score:"));
		scoreLabel = new JLabel("-");
		add(scoreLabel, "wrap");

		add(progress, "span 2, growx");
	}

	protected void run() {
		thread = new Thread() {
			@Override
			public void run() {
				// // get our simulation config
				// Simulation config = new Simulation(simulationFile);
				//
				// // setup CONOP
				// CONOP conop = new CONOP(config);
				// conop.addListener(new AbstractListener() {
				//
				// @Override
				// public Mode getMode() {
				// return Mode.GUI;
				// }
				//
				// @Override
				// public void tried(final double temp, final Solution current,
				// final Solution best) {
				// iteration++;
				// if (iteration == 1) {
				// initialTemp = Math.log(temp);
				// start = System.currentTimeMillis();
				// progress.setValue(5);
				// }
				// if (!running) {
				// throw new RuntimeException("user interrupt");
				// }
				// if ((iteration % 1000) == 0) {
				// int value = (int) (((initialTemp - Math.log(temp)) /
				// initialTemp) * 100) + 5;
				// progress.setValue(value);
				//
				// iterLabel.setText(iteration + "");
				//
				// tempLabel.setText(DEC.format(temp));
				//
				// long elapsed = (System.currentTimeMillis() - start) / 60000;
				// timeLabel.setText(elapsed + " min");
				//
				// scoreLabel.setText(DEC.format(current.getScore()) + " / " +
				// DEC.format(best.getScore()));
				// }
				// }
				// });
				// conop.filterMode(Mode.GUI);
				// conop.solve(config.getRun(), config.getInitialSolution());
			}
		};
		thread.start();
	}
}
