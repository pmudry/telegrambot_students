package com.hei.isc.imageProcessing.data;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.hei.isc.Utils;

/**
 * A {@link BufferedImage} backed image class
 * @author Pierre-André Mudry
 * @version 1.0
 */
public class Image {
    public BufferedImage buffer;
    public int w, h;

    /**
     * Construct an empty image
     * @param w width of the image
     * @param h height of the image
     */
    public Image(int w, int h) {
        buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        this.w = w;
        this.h = h;
    }

    /**
     * Constructor
     * @param path Construct a file from path given as a String
     */
    public Image(String path) {
        this(new File(path));
    }

    /**
     * Constructor
     * @param file Construct a file from a {@link File}
     */
    public Image(File file) {
        // Fill the frame content with the image
        try {
            BufferedImage tmp = ImageIO.read(file);
            w = tmp.getWidth();
            h = tmp.getHeight();
            // Make sure we have the correct color model
            buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            ColorConvertOp op = new ColorConvertOp(null);
            op.filter(tmp, buffer);
            Utils.logger.info("Image size " + w + "," + h);
        } catch (Exception e) {
            Utils.logger.error("Could not find image " + file.getName() + ", exiting !");
            Utils.logger.error(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Returns the image in the buffered frame as an OutputStream
     */
    public void dumpToStream(OutputStream out) throws IOException {
        ImageIO.write(buffer, "png", out);
    }


    /**
     * Sets an array of pixels of Color and displays them
     */
    public void setPixelsColor(Color[][] pixels) {
        try {
            if (pixels[0].length != h || pixels.length != w) {
                throw new Exception("Invalid size of the pixel array !");
            }

            for (int i = 0; i < w; i++)
                for (int j = 0; j < h; j++) {
                    buffer.setRGB(i, j, pixels[i][j].getRGB());
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a single pixel from the background image and returns its
     * grayscale value
     *
     * @param x the x-position of the pixel to get
     * @param y the y-position of the pixel to get
     * @return the pixel value [0.255]
     */
    public int getPixelBW(int x, int y) {
        if ((x < 0) || (y < 0) || (x >= w) || (y >= h)) {
            return 0;
        } else {
            // Inside the image. Make the gray conversion and return the value
            Color c = new Color(buffer.getRGB(x, y));
            return (int) (0.3 * c.getRed() + 0.59 * c.getGreen() + 0.11 * c.getBlue());
        }
    }

    /**
     * Sets an array of grayscale pixels (from 0 to 255) and displays them
     */
    public void setPixelsBW(int[][] pixels) {
        try {
            if (pixels[0].length != h || pixels.length != w) {
                throw new Exception("Invalid size of the pixel array !");
            }

            for (int i = 0; i < w; i++)
                for (int j = 0; j < h; j++) {
                    int c = pixels[i][j];
                    c = 0xff << 24 | c << 16 | c << 8 | c;
                    buffer.setRGB(i, j, c);
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the array of the pixels (which have been converted to grayscale
     * if required)
     *
     * @return The arrays of gray pixels, from 0-255 for each pixel (no alpha)
     */
    public int[][] getPixelsBW() {
        int[][] values = new int[w][h];

        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++) {
                Color c = new Color(buffer.getRGB(i, j));
                values[i][j] = (int) (0.3 * c.getRed() + 0.59 * c.getGreen() + 0.11 * c.getBlue());
            }

        return values;
    }

    /**
     * Gets the array of the pixels as Colors (see #Color)
     *
     * @return The arrays of pixels
     */
    public Color[][] getPixelsColor() {
        Color[][] values = new Color[w][h];

        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++) {
                int col = buffer.getRGB(i, j);
                int r = (col >> 16) & 0xff;
                int g = (col >> 8) & 0xff;
                int b = col & 0xff;
                int t = (col >> 24) & 0xff;
                values[i][j] = new Color(r, g, b, t);
            }

        return values;
    }

    /**
     * Converts a color array to a black-or-white array
     *
     * @param c The color array
     * @return The array converted to BW
     */
    public static Color[][] convertToGray(Color[][] c) {
        int w = c.length;
        int h = c[0].length;
        Color[][] values = new Color[w][h];

        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++) {
                Color col = c[i][j];
                int intColor = (int) (0.3 * col.getRed() + 0.59 * col.getGreen() + 0.11 * col.getBlue());
                values[i][j] = new Color(intColor, intColor, intColor);
            }

        return values;
    }
}
