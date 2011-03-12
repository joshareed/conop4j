package org.andrill.conop.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;

import org.andrill.conop.search.Run;
import org.andrill.conop.search.Simulation;
import org.andrill.conop.search.Solution;
import org.andrill.conop.search.listeners.ConsoleProgressListener;
import org.andrill.conop.search.listeners.Listener;
import org.andrill.conop.search.listeners.RanksListener;

import com.google.common.collect.Lists;

/**
 * A simple GUI for running a simulation.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class RunPanel extends JPanel implements Listener {
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

	public void configure(final Properties properties) {
		// do nothing
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
				// get our simulation config
				Simulation config = new Simulation(simulationFile);
				Run run = config.getRun();

				// clean up listeners
				List<Listener> listeners = Lists.newArrayList(config.getListeners());
				for (Iterator<Listener> iterator = listeners.iterator(); iterator.hasNext();) {
					if (iterator.next() instanceof ConsoleProgressListener) {
						iterator.remove();
					}
				}
				listeners.add(RunPanel.this);

				// find the optimal placement
				Solution solution = Simulation.runSimulation(config, run, Solution.initial(run),
						listeners.toArray(new Listener[0]));

				// write out the solution and ranks
				RanksListener ranks = null;
				for (Listener l : config.getListeners()) {
					if (l instanceof RanksListener) {
						ranks = (RanksListener) l;
					}
				}
				Simulation.writeResults(solution, ranks);
			}
		};
		thread.start();
	}

	public void tried(final double temp, final Solution current, final Solution best) {
		iteration++;
		if (iteration == 1) {
			initialTemp = Math.log10(temp);
			start = System.currentTimeMillis();
			progress.setValue(5);
		}
		if (!running) {
			throw new RuntimeException("user interrupt");
		}
		if (iteration % 500 == 0) {
			int value = (int) ((initialTemp - Math.log10(temp)) / initialTemp * 100) + 5;
			progress.setValue(value);

			iterLabel.setText(iteration + "");

			tempLabel.setText(DEC.format(temp));

			long elapsed = (System.currentTimeMillis() - start) / 60000;
			timeLabel.setText(elapsed + " min");

			scoreLabel.setText(DEC.format(current.getScore()) + " / " + DEC.format(best.getScore()));
		}
	}
}
