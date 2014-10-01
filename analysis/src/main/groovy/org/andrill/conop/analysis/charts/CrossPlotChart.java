package org.andrill.conop.analysis.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.andrill.conop.analysis.AnnotatedEvent;
import org.andrill.conop.analysis.AnnotatedSolution;

import com.google.common.collect.Maps;

public class CrossPlotChart {
	private static final int PADDING = 6;
	protected int rowHeight = 10;

	void build(final AnnotatedSolution solution1, final AnnotatedSolution solution2, final File file)
			throws IOException {

		// find our longest event name
		String longest = "";
		for (AnnotatedEvent e : solution1.getEvents()) {
			if (e.getName().length() > longest.length()) {
				longest = e.getName();
			}
		}

		// calculate metrics
		FontMetrics metrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics().getFontMetrics();
		rowHeight = metrics.getHeight() + PADDING;
		int labelWidth = metrics.stringWidth(longest) + PADDING;

		// figure out size
		int width = (solution1.getEvents().size() * rowHeight) + (4 * PADDING) + labelWidth;
		int height = (solution1.getEvents().size() * rowHeight);

		// create our image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform initial = graphics.getTransform();
		graphics.setBackground(Color.white);
		graphics.fillRect(0, 0, width, height);

		// reset
		graphics.setTransform(initial);

		// draw our horizontal hairlines
		graphics.setColor(Color.lightGray);
		graphics.setStroke(new BasicStroke(1.0f));
		for (int j = 10; j < solution1.getEvents().size(); j += 10) {
			graphics.translate(0, 10 * rowHeight);
			graphics.drawLine(0, 0, width, 0);
		}

		// reset
		graphics.setTransform(initial);

		// draw our vertical hairlines
		graphics.translate(labelWidth + PADDING, 0);
		for (int j = 10; j < solution1.getEvents().size(); j += 10) {
			graphics.translate(10 * rowHeight, 0);
			graphics.drawLine(0, 0, 0, height);
		}

		// reset
		graphics.setTransform(initial);

		// draw our axes
		graphics.setColor(Color.black);
		graphics.setStroke(new BasicStroke(2.0f));
		graphics.drawLine(labelWidth + PADDING, 0, labelWidth + PADDING, height);

		// reset
		graphics.setTransform(initial);
		graphics.translate(PADDING, -PADDING);

		// draw the labels and bars
		for (AnnotatedEvent e : solution1.getEvents()) {
			graphics.setColor(Color.black);
			graphics.translate(0, rowHeight);
			graphics.drawString(e.getName(), 0, 0);
			graphics.translate(labelWidth + PADDING, -PADDING);
			drawHorizontalEvent(e, graphics);
			graphics.translate(-labelWidth - PADDING, PADDING);
		}

		// draw the vertical events
		Map<String, AnnotatedEvent> events = Maps.newHashMap();
		for (AnnotatedEvent e : solution2.getEvents()) {
			events.put(e.getName(), e);
		}

		graphics.setTransform(initial);
		graphics.translate(labelWidth - PADDING, PADDING);

		for (AnnotatedEvent e : solution1.getEvents()) {
			graphics.translate(rowHeight, 0);
			drawVerticalEvent(events.get(e.getName()), graphics);
		}

		ImageIO.write(image, "png", file);
	}

	protected void drawHorizontalEvent(final AnnotatedEvent e, final Graphics2D graphics) {
		int min = (int) e.getAnnotation(AnnotatedEvent.MIN_POS, e.getAnnotation(AnnotatedEvent.POS, 0));
		int max = (int) e.getAnnotation(AnnotatedEvent.MAX_POS, e.getAnnotation(AnnotatedEvent.POS, 0));
		int pos = (int) e.getAnnotation(AnnotatedEvent.POS, 0);

		graphics.setStroke(new BasicStroke(8));
		graphics.setColor(new Color(0, 0, 0, 64));
		graphics.drawLine(min * rowHeight, 0, ((max + 1) * rowHeight) - PADDING - 4, 0);
		graphics.setColor(new Color(0, 0, 0, 128));
		graphics.drawLine(pos * rowHeight, 0, ((pos + 1) * rowHeight) - PADDING - 4, 0);
	}

	protected void drawVerticalEvent(final AnnotatedEvent e, final Graphics2D graphics) {
		int min = (int) e.getAnnotation(AnnotatedEvent.MIN_POS, e.getAnnotation(AnnotatedEvent.POS, 0));
		int max = (int) e.getAnnotation(AnnotatedEvent.MAX_POS, e.getAnnotation(AnnotatedEvent.POS, 0));
		int pos = (int) e.getAnnotation(AnnotatedEvent.POS, 0);

		graphics.setStroke(new BasicStroke(8));
		graphics.setColor(new Color(0, 0, 0, 64));
		graphics.drawLine(0, min * rowHeight, 0, ((max + 1) * rowHeight) - PADDING - 4);
		graphics.setColor(new Color(0, 0, 0, 128));
		graphics.drawLine(0, pos * rowHeight, 0, ((pos + 1) * rowHeight) - PADDING - 4);
	}
}
