package com.hei.isc.imageProcessing;

import com.hei.isc.imageProcessing.gui.ImageWindow;
import com.hei.isc.imageProcessing.data.Image;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.FileOutputStream;

/**
 * This class implements various image filters
 *
 * @author Pierre-André Mudry
 * @version 1.3
 */
public class ImageFilters {

    /**
     * Copies every second line of the image and leaves the rest black
     *
     * @param in The array to work on, remains untouched
     * @return The transformed array
     */
    public static int[][] oddLines(int[][] in) {
        // TODO Complete me
        return in;
    }

    /**
     * Dichotomy of the image
     *
     * @param in The array to work on, remains untouched
     * @param threshold Pixels values above that values should be white (255), under that are black (0)
     * @return The transformed array
     */
    public static int[][] threshold(int[][] in, int threshold) {
        // TODO Complete me
        return in;
    }

    /**
     * Mean filter that blurs the image a bit
     *
     * @param in The array to work on, remains untouched
     * @return The blurred array
     */
    public static int[][] blur(int[][] in, int radius) {
        // TODO Complete me
        return in;
    }

    /**
     * Derivative of the image
     *
     * @param in The array to work on, remains untouched
     * @return The transformed array
     */
    public static int[][] derivative(int[][] in) {
        // TODO Complete here
        return in;
    }

    /**
     * Generates a MEME type file. To be used optionnaly in Task 3.3
     * @see <a href="https://en.wikipedia.org/wiki/Internet_meme">...</a>
     * @param im The original {@link Image} to work on
     * @param txt The text to be embedded (if required, with \n as markers for multiple lines)
     * @return An {@link Image} with the meme embedded
     */
    public static Image memeGenerator(Image im, String txt) {
        Graphics2D g = im.buffer.createGraphics();
        g.setColor(Color.WHITE);

        // Choose a nice font for the meme text
        final Font font = new Font("Impact", Font.BOLD, 96);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        // Center drawing
        int w = im.w;

        String[] lines = txt.split("\n");
        int nLines = lines.length;

        // Compute origin of first line coordinates
        int y = (int) ((nLines) * fm.getHeight() * 0.8) / (nLines);

        for (String line : lines) {
            g.setColor(Color.WHITE);

            // Center the text according to its length (in pixel coordinates)
            int x = (w - fm.stringWidth(line)) / 2;
            g.drawString(line, x, y);

            // Draws text outline
            FontRenderContext frc = new FontRenderContext(null, true, false);
            TextLayout tl = new TextLayout(line, font, frc);
            AffineTransform af = new AffineTransform();
            af.translate(x, y);
            Shape outline = tl.getOutline(af);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(4));
            g.draw(outline);

            // Increase height for each line of text
            y += (int) (fm.getHeight() * 0.7);
        }

        return im;
    }

    /**
     * Embeds a logo to an image using proper alpha blending. To be used in Task 3.3 if wanted
     *
     * @param image The {@link Color} original array, remains untouched
     * @param logo The {@link Color} logo array, remains untouched
     * @param percx x-location, in percentage of original image (0-1)
     * @param percy y-location, in percentage of original image (0-1)
     * @return A color array with the logo embedded
     */
    public static Color[][] embedLogo(Color[][] image, Color[][] logo, double percx, double percy) {
        int w = image.length;
        int h = image[0].length;

        int wlogo = logo.length;
        int hlogo = logo[0].length;

        Color[][] result = new Color[w][h];

        // Where are we going to put the logo on the image
        int posx = (int) (w * percx) - wlogo / 2;
        int posy = (int) (h * percy) - hlogo / 2;

        // Copy the base pixels to the destination array
        for (int i = 0; i < w; i++) {
            System.arraycopy(image[i], 0, result[i], 0, h);
        }

        // Do the alpha blending with the logo
        // i.e. for each pixel in the logo image, put it in the original image
        // and blend it
        for (int i = 0; i < wlogo; i++) {
            for (int j = 0; j < hlogo; j++) {

                int destx = i + posx;
                int desty = j + posy;

                // If we try to write outside the array, skip
                if (destx < 0 || destx >= w || desty < 0 || desty >= h)
                    continue;

                Color current = result[destx][desty];
                Color logoColor = logo[i][j];

                double alpha = logoColor.getAlpha() / 255.0;

                Color newColor = new Color(
                    (int) (current.getRed() * (1 - alpha) + logoColor.getRed() * (alpha)),
                    (int) (current.getGreen() * (1 - alpha) + logoColor.getGreen() * (alpha)),
                    (int) (current.getBlue() * (1 - alpha) + logoColor.getBlue() * (alpha)));

                result[destx][desty] = newColor;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        final String astronautPath = "resources/astronaut.png";

        // Load picture and create Image from it
        Image imAstronaut = new Image(astronautPath);

        // Gets pixels as an int[][]
        int[][] astronautBW_a = imAstronaut.getPixelsBW();

        // Image for storing the modifications
        Image imModified = new Image(imAstronaut.w, imAstronaut.h);

        // Sets the pixels of this image from a filter
        imModified.setPixelsBW(ImageFilters.oddLines(astronautBW_a));

        Image imMeme = new Image(astronautPath);
        imMeme = memeGenerator(imMeme, "To the\nmoon!");

        // Show the pictures on two different windows
        new ImageWindow(imAstronaut, "Original", -600, 0);
        new ImageWindow(imModified, "Modified", 0, 0);
        new ImageWindow(imMeme, "Meme", 600, 0);

        // What we want to obtain with the fluent API
        Image pipe1 = ImagePipeline.
            create(astronautPath).
            derivative().
            blur(3).
            getImage();
    }
}
