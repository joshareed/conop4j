package org.andrill.conop.analysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RangeChart {
	private static final int PADDING = 6;
	private static final int W = 10;

	void build(final AnnotatedSolution solution, final File file) throws IOException {
		Map<String, List<AnnotatedEvent>> coalesced = coalesceEvents(solution);

		// find our longest event name
		String longest = "";
		for (String name : coalesced.keySet()) {
			if (name.length() > longest.length()) {
				longest = name;
			}
		}

		// calculate metrics
		FontMetrics metrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics().getFontMetrics();
		int rowHeight = metrics.getHeight() + PADDING;
		int labelWidth = metrics.stringWidth(longest) + PADDING;

		// figure out size
		int width = (solution.getEvents().size() * W) + (4 * PADDING) + labelWidth;
		int height = (coalesced.size() * rowHeight);

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
		for (int j = 0; j < (coalesced.size() - 1); j++) {
			graphics.translate(0, rowHeight);
			graphics.drawLine(0, 0, width, 0);
		}

		// reset
		graphics.setTransform(initial);

		// draw our vertical hairlines
		graphics.translate(labelWidth + PADDING, 0);
		for (int j = 10; j < solution.events.size(); j += 10) {
			graphics.translate(10 * W, 0);
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
		int i = 0;
		for (Entry<String, List<AnnotatedEvent>> e : coalesced.entrySet()) {
			graphics.setColor(Color.black);
			graphics.translate(0, rowHeight);
			graphics.drawString(e.getKey(), 0, 0);
			graphics.translate(labelWidth + PADDING, -PADDING);
			drawEvents(e.getValue(), i, graphics);
			graphics.translate(-labelWidth - PADDING, PADDING);
			i++;
		}

		ImageIO.write(image, "png", file);
	}

	protected Map<String, List<AnnotatedEvent>> coalesceEvents(final AnnotatedSolution solution) {
		Map<String, List<AnnotatedEvent>> coalesced = Maps.newLinkedHashMap();
		for (AnnotatedEvent e : solution.getEvents()) {
			String key = e.getName();
			if (key.indexOf(' ') > 0) {
				key = key.substring(0, key.lastIndexOf(' '));
			}
			if (coalesced.containsKey(key)) {
				coalesced.get(key).add(e);
			} else {
				coalesced.put(key, Lists.newArrayList(e));
			}
		}
		return coalesced;
	}

	protected void drawEvents(final List<AnnotatedEvent> events, final int i, final Graphics2D graphics) {
		int offsetY = 0;
		AnnotatedEvent e = events.get(0);
		int e1 = (int) e.getAnnotation(AnnotatedEvent.POS, 0);
		int e1min = (int) e.getAnnotation(AnnotatedEvent.MIN_POS, 0);
		int e1max = (int) e.getAnnotation(AnnotatedEvent.MAX_POS, 0);
		int e2 = e1;
		int e2min = e1min;
		int e2max = e2min;

		graphics.translate(0, -2);
		graphics.setStroke(new BasicStroke(3.0f));

		if (events.size() > 1) {
			e = events.get(1);
			e2 = (int) e.getAnnotation(AnnotatedEvent.POS, 0);
			e2min = (int) e.getAnnotation(AnnotatedEvent.MIN_POS, 0);
			e2max = (int) e.getAnnotation(AnnotatedEvent.MAX_POS, 0);

			// draw the last event range
			graphics.setColor(Color.lightGray);
			graphics.drawLine((e2min * W) + PADDING, 6, (e2max * W) + W + PADDING, 6);
		}

		// draw the first event range
		graphics.setColor(Color.lightGray);
		graphics.drawLine((e1min * W) + PADDING, 0, (e1max * W) + W + PADDING, 0);

		// draw the position range
		graphics.setColor(Color.black);
		graphics.drawLine((e1 * W) + PADDING, 0, (e1 * W) + PADDING, 6);
		graphics.drawLine((e1 * W) + PADDING, offsetY + 3, (e2 * W) + W + PADDING, offsetY + 3);
		graphics.drawLine((e2 * W) + W + PADDING, 0, (e2 * W) + W + PADDING, 6);

		graphics.translate(0, 2);
	}
}
