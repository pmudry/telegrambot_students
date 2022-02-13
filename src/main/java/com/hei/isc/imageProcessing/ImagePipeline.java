package com.hei.isc.imageProcessing;
import com.hei.isc.imageProcessing.data.Image;

/**
 * A fluent API for image manipulations using {@link ImageFilters}
 */
public class ImagePipeline {
    private Image im;

    /**
     * Factory pattern to create a pipeline
     * @param im The original image
     * @return The pipeline to apply transforms
     */
    public static ImagePipeline create(Image im) {
        return new ImagePipeline(im);
    }

    /**
     * Factory pattern to create a pipeline
     * @param path Path where to create the original Image from
     * @return The pipeline to apply transforms
     */
    public static ImagePipeline create(String path) {
        return ImagePipeline.create(new Image(path));
    }

    /**
     * A hidden constructor
     */
    private ImagePipeline(Image im) {
        this.im = im;
    }

    /**
     * Gets the image result from the pipeline
     * @return The processed {@link Image}
     */
    public Image getImage() {
        return this.im;
    }

    /**
     * Utility conversion method to create images and set the pixels in BW
     * @param array The original int[][] array to create an {@link Image} from
     * @return The transformed pipeline
     */
    private Image createAndSetBW(int[][] array) {
        Image bw = new Image(im.w, im.h);
        bw.setPixelsBW(array);
        return bw;
    }

    /**
     * Makes the image 4 times smallers
     * @return The transformed pipeline
     */
    public ImagePipeline oddLines() {
        Image smaller = new Image(im.w, im.h);
        smaller.setPixelsBW(ImageFilters.oddLines(im.getPixelsBW()));
        im = smaller;
        return this;
    }


    /**
     * Apply a value threshold to the image (all pixels
     * with value higher than #thres are white, others
     * are black)
     * @param thres The value for which the pixels above are white
     * @return The transformed pipeline
     */
    public ImagePipeline threshold(int thres) {
        im = createAndSetBW(ImageFilters.threshold(im.getPixelsBW(), thres));
        return this;
    }

    /**
     * Discards color information from the image
     * @return The transformed pipeline
     */
    public ImagePipeline bw(){
        im = createAndSetBW(im.getPixelsBW());
        return this;
    }

    /**
     * Blurs the image
     * @param radius The amount of blur (1-10 is reasonable)
     * @return The transformed pipeline
     */
    public ImagePipeline blur(int radius) {
        im = createAndSetBW(ImageFilters.blur(im.getPixelsBW(), radius));
        return this;
    }

    /**
     * Makes a Sobel-like derivative on the image
     * @return The transformed pipeline
     */
    public ImagePipeline derivative() {
        im = createAndSetBW(ImageFilters.derivative(im.getPixelsBW()));
        return this;
    }

    /**
     * Embeds a text atop the image, meme style
     * @param txt The text to be embedded
     * @return The transformed pipeline
     */
    public ImagePipeline memeGenerator(String txt) {
        im = ImageFilters.memeGenerator(im, txt);
        return this;
    }

    /**
     * Embeds a logo in an image, on the bottom-right side of the picture
     * {@link #embedLogo(Image, double, double)}
     * @param logo The logo to embed
     * @return The pipeline to apply transforms
     */
    public ImagePipeline embedLogo(Image logo) {
        return embedLogo(logo, 0.8, 0.8);
    }

    /**
     * Embeds a logo in an image, on the bottom-right side of the picture
     * @param logo The logo to embed
     * @param percx x-location of the logo position, in percent of the original image
     * @param percy y-location of the logo position, in percent of the original image
     * @return The pipeline to apply transforms
     */
    public ImagePipeline embedLogo(Image logo, double percx, double percy) {
        Image copy = new Image(im.w, im.h);
        copy.setPixelsColor(ImageFilters.embedLogo(im.getPixelsColor(), logo.getPixelsColor(), percx, percy));
        im = copy;
        return this;
    }
}
