package org.andrill.conop.pp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.io.Closeables;

/**
 * Generates a summary Excel workbook for a set of CONOP runs.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SummarySpreadsheet {

	public interface Summary {
		void generate(Workbook workbook, RunInfo run);
	}

	public static void main(final String[] args) {
		SummarySpreadsheet summary = new SummarySpreadsheet(new Placements());
		summary.write(new File("out.xls"), new RunInfo(new File("test")));
	}

	protected final Summary[] summaries;

	public SummarySpreadsheet(final Summary... summaries) {
		this.summaries = summaries;
	}

	public void write(final File out, final RunInfo... runs) {
		Workbook workbook = new HSSFWorkbook();
		for (RunInfo run : runs) {
			for (Summary summary : summaries) {
				summary.generate(workbook, run);
			}
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(out);
			workbook.write(fos);
		} catch (IOException ioe) {
			// do nothing
		} finally {
			Closeables.closeQuietly(fos);
		}
	}
}
