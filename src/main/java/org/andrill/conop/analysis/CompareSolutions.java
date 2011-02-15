package org.andrill.conop.analysis;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.andrill.conop.analysis.SummarySpreadsheet.Summary;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Compares multiple solutions.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CompareSolutions implements Summary {

	protected Map<String, String> findEvent(final String name, final List<Map<String, String>> events) {
		for (Map<String, String> e : events) {
			if (e.get("name").equals(name)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public void generate(final Workbook workbook, final RunInfo... runs) {
		// create our sheet
		Sheet sheet = workbook.createSheet("Diff");

		// create our styles
		Font bold = sheet.getWorkbook().createFont();
		bold.setBoldweight(Font.BOLDWEIGHT_BOLD);

		CellStyle s1 = sheet.getWorkbook().createCellStyle();
		s1.setFont(bold);
		s1.setAlignment(CellStyle.ALIGN_CENTER);

		CellStyle s2 = sheet.getWorkbook().createCellStyle();
		s2.setFont(bold);
		s2.setAlignment(CellStyle.ALIGN_CENTER);
		s2.setBorderBottom(CellStyle.BORDER_THIN);

		// create our first header row
		Row h1 = sheet.createRow(0);
		h1.createCell(0).setCellValue("Event");
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
		h1.createCell(1).setCellValue("Type");
		for (int i = 0; i < runs.length; i++) {
			h1.createCell(2 + (i * 3)).setCellValue(runs[i].getName());
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 2 + (i * 3), 2 + (i * 3) + 2));
		}
		for (Cell c : h1) {
			c.setCellStyle(s1);
		}

		// create our second header row
		Row h2 = sheet.createRow(1);
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
		h2.createCell(0);
		h2.createCell(1);
		for (int i = 0; i < runs.length; i++) {
			h2.createCell(2 + (i * 3) + 0).setCellValue("Rank");
			h2.createCell(2 + (i * 3) + 1).setCellValue("Min Rank");
			h2.createCell(2 + (i * 3) + 2).setCellValue("Max Rank");
		}
		for (Cell c : h2) {
			c.setCellStyle(s2);
		}

		// get our events sorted in solution order
		List<Map<String, String>> events = runs[0].getEvents();
		Collections.sort(events, new Comparator<Map<String, String>>() {
			@Override
			public int compare(final Map<String, String> o1, final Map<String, String> o2) {
				return new Integer(o2.get("solution")).compareTo(new Integer(o1.get("solution")));
			}
		});

		// write our rows
		for (int i = 0; i < events.size(); i++) {
			Map<String, String> event = events.get(i);
			Row row = sheet.createRow(i + 2);
			String name = event.get("name");
			row.createCell(0).setCellValue(name);
			row.createCell(1).setCellValue(event.get("typename"));

			// get our ranks
			for (int j = 0; j < runs.length; j++) {
				RunInfo run = runs[j];
				Map<String, String> e = findEvent(name, run.getEvents());
				if (e != null) {
					row.createCell(2 + (j * 3) + 0).setCellValue(Integer.parseInt(e.get("solution")));
					row.createCell(2 + (j * 3) + 1).setCellValue(Integer.parseInt(e.get("rankmin")));
					row.createCell(2 + (j * 3) + 2).setCellValue(Integer.parseInt(e.get("rankmax")));
				}
			}
		}
	}
}
