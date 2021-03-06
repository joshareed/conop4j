package org.andrill.conop.analysis.summary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.andrill.conop.analysis.legacy.Solution;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Generates a summary Excel workbook for a set of CONOP runs.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SummarySpreadsheet {

	/**
	 * Generates one or more summary sheets for the specified dataset.
	 */
	public interface Summary {
		void generate(Workbook workbook, Solution... run);
	}

	protected final Summary[] summaries;

	/**
	 * Create a new summary spreadsheet with the specified summary sheets.
	 * 
	 * @param summaries
	 *            the summaries.
	 */
	public SummarySpreadsheet(final Summary... summaries) {
		this.summaries = summaries;
	}

	/**
	 * Write the summary spreadsheet for the specified runs.
	 * 
	 * @param out
	 *            the file.
	 * @param runs
	 *            the runs.
	 */
	public void write(final File out, final Solution... runs) {
		// build our summary workbook
		Workbook workbook = new HSSFWorkbook();
		for (Summary summary : summaries) {
			summary.generate(workbook, runs);
		}

		// write out our workbook
		try (FileOutputStream fos = new FileOutputStream(out)) {
			workbook.write(fos);
		} catch (IOException ioe) {
			// do nothing
		}
	}
}
