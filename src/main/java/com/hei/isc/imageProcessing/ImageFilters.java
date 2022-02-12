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
     * @param in The array to work on, remains untouched*
     * @return The transformed array
     */
    public static Color[][] oddLines(Color[][] in) {
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
     * Generates a MEME type file
     * @see <a href="https://en.wikipedia.org/wiki/Internet_meme">...</a>
     * @param im The original {@link Image} to work on
     * @param txt The text to be embedded (if required, with \n as markers for multiple lines)
     * @return An {@link Image} with the meme embedded
     */
    public static Image memeGenerator(Image im, String txt) {
        Graphics2D g = im.buffer.createGraphics();

        final Font font = new Font("Impact", Font.BOLD, 96);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.setFont(font);

        // Center drawing
        int w = im.w;

        FontMetrics fm = g.getFontMetrics();

        String[] lines = txt.split("\n");
        int nLines = lines.length;

        // Compute origin of first line coordinates
        int y = (int) ((nLines) * fm.getHeight() * 0.8) / (nLines);

        for (String line : txt.split("\n")) {
            g.setColor(Color.WHITE);
            int x = (w - fm.stringWidth(line)) / 2;
            g.drawString(line, x, y);

            // Draws the outline of text
            FontRenderContext frc = new FontRenderContext(null, true, false);
            TextLayout tl = new TextLayout(line, font, frc);
            AffineTransform af = new AffineTransform();
            af.translate(x, y);
            Shape outline = tl.getOutline(af);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(4));
            g.draw(outline);

            y += (int) (fm.getHeight() * 0.7);
        }

        return im;
    }

    /**
     * Make a picture look old by changing its color to sepia tones
     * @param in The array to work on, remains untouched
     * @return The transformed {@link Color} array
     */
    public static Color[][] sepia(Color[][] in) {
        return in;
    }

    /**
     * Embeds a logo to an image
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
        final String ricePath = "resources/rice.jpg";

         // Create or loads images
        Image imAstronaut = new Image(astronautPath);
        Image imRice = new Image(ricePath);

        int[][] riceArray = imRice.getPixelsBW();

        Color[][] originalBW = imAstronaut.getPixelsColor();
        Image im2 = new Image(imAstronaut.w / 2, imAstronaut.h / 2);
//        Color[][] modifiedBW = ImageFilters.oddLines(riceArray);

        //Color[][] originalWithLogo = ImageFilters.embedLogo(im1.getPixelsColor(), imLogo.getPixelsColor(), 0.85, 0.15);

        // Sets the array values to the image
//        im2.setPixelsColor(modifiedBW);
   //     im1.setPixelsColor(originalWithLogo);

        Image im3 = new Image(astronautPath);
        im3 = memeGenerator(im3, "The moon!");

        // Show the pictures on two different windows
        new ImageWindow(imAstronaut, "Original", -450, 200);
        new ImageWindow(im2, "Modified", 0, 200);
        new ImageWindow(im3, "Meme", 450, 200);

        new ImageWindow(ImagePipeline.
            create(astronautPath).
            oddLines().
            threshold(128).
            getImage(), "Meme", -450, -230);

//        Image pipe1 = ImagePipeline.
//            create(astronautPath).
//            derivative().
//            blur(3).
//            embedLogo(imLogo, 0.9, 0.9).
//            memeGenerator("It\nrocks!").
//            getImage();

//        new ImageWindow(pipe1, "Multi-filters", 0, -250);

        try {
            FileOutputStream os = new FileOutputStream("resized.png");
            im2.dumpToStream(os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
