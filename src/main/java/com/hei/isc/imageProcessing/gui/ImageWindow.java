package com.hei.isc.imageProcessing.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.hei.isc.imageProcessing.data.Image;

/**
 * This class was made to display images in {@link JFrame} for debugging and working
 * on desktops.
 * @author Pierre-André Mudry 2022
 * @version 1.0
 */
public class ImageWindow extends JFrame {
	static class ImgComponent extends JComponent {
		private final Image image;

		public ImgComponent(Image im) {
			this.image = im;
			this.setPreferredSize(new Dimension(im.w, im.h));
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.drawImage(image.buffer, 0, 0, null);
			g2.dispose();
		}
	}

	ImgComponent imJComp;

	private static final long serialVersionUID = 6832022057915586803L;

	public ImageWindow(Image im) {
		this(im, "An image", 0, 0);
	}

	public ImageWindow(Image im, String title, int xWindowOffset, int yWindowOffset) {
		this.imJComp = new ImgComponent(im);
		this.getContentPane().add(imJComp);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// Determine the new location of the window
		int x = (dim.width - im.w) / 2 + xWindowOffset;
		int y = (dim.height - im.h) / 2 + yWindowOffset;

		// Move the window to the given destination
		this.pack();
		this.setLocation(x, y);
		this.setTitle(title);
		this.setVisible(true);
	}

	public static ImageWindow factory(String path){
		Image im = new Image(path);
		return new ImageWindow(im);
	}

	public static void main(String[] args) {
		final String imageUsed = "resources/astronaut.png";
		ImageWindow.factory(imageUsed);
	}
}
