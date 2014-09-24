package org.andrill.conop.analysis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RangeChart {

	void build(final AnnotatedSolution solution, final File file) throws IOException {
		int width = 1024;
		int height = 768;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setBackground(Color.white);
		graphics.fillRect(0, 0, width, height);

		graphics.setColor(Color.black);
		graphics.drawLine(4, 4, 4, 760);

		ImageIO.write(image, "png", file);
	}
}
