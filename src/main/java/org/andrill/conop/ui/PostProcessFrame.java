package org.andrill.conop.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;

import org.andrill.conop.analysis.AgeAndPlacements;
import org.andrill.conop.analysis.RunInfo;
import org.andrill.conop.analysis.SummarySpreadsheet;
import org.andrill.conop.analysis.SummarySpreadsheet.Summary;

import com.google.common.collect.Lists;

/**
 * A simple GUI for performing post processing.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class PostProcessFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Class<?>[] SUMMARIES = new Class<?>[] { AgeAndPlacements.class };
	protected File run;
	private final List<Class<?>> summaries = Lists.newArrayList(SUMMARIES);

	public PostProcessFrame() {
		super("CONOP Post Processor");
		initComponents();
	}

	private void initComponents() {
		final Container content = getContentPane();
		content.setLayout(new MigLayout("fill"));

		final JLabel runLabel = new JLabel("Run:");
		final JFileChooser fileChooser = new JFileChooser(new File("."));
		final JButton browseButton = new JButton();
		final JButton processButton = new JButton();

		content.add(runLabel, "split");

		fileChooser.setAcceptAllFileFilterUsed(false);

		browseButton.setAction(new AbstractAction("< select run >") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setDialogTitle("Choose Run Directory");
				if (fileChooser.showOpenDialog(PostProcessFrame.this) == JFileChooser.APPROVE_OPTION) {
					run = fileChooser.getSelectedFile();
				}
				if (run == null) {
					browseButton.setText("< select run >");
				} else {
					browseButton.setText(run.getName());
				}
				processButton.setEnabled((run != null) && (summaries.size() > 0));
			}
		});
		content.add(browseButton, "wmin 150px, align right, wrap");

		content.add(new JLabel("Summaries:"), "span, wrap");

		for (final Class<?> summary : SUMMARIES) {
			final JCheckBox checkbox = new JCheckBox(summary.getSimpleName(), true);
			checkbox.setAction(new AbstractAction(summary.getSimpleName()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (checkbox.isSelected()) {
						summaries.add(summary);
					} else {
						summaries.remove(summary);
					}
					processButton.setEnabled((run != null) && (summaries.size() > 0));
				}
			});
			content.add(checkbox, "span, wrap");
		}

		processButton.setAction(new AbstractAction("Process") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogTitle("Save");
				if (fileChooser.showSaveDialog(PostProcessFrame.this) == JFileChooser.APPROVE_OPTION) {
					File out = fileChooser.getSelectedFile();

					// build our summary spreadsheet
					List<Summary> list = Lists.newArrayList();
					for (Class<?> c : summaries) {
						try {
							list.add((Summary) c.newInstance());
						} catch (InstantiationException e1) {
							// should never happen
						} catch (IllegalAccessException e1) {
							// should never happen
						}
					}

					SummarySpreadsheet spreadsheet = new SummarySpreadsheet(list.toArray(new Summary[list.size()]));
					spreadsheet.write(out, new RunInfo(run));

					JOptionPane.showMessageDialog(PostProcessFrame.this,
							"Summary spreadsheet written to: " + out.getName());
				}
			}
		});
		processButton.setEnabled(false);
		content.add(processButton, "span, align right");
	}
}
