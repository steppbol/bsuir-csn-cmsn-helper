package main.filter;

import java.awt.image.BufferedImage;

public interface FilterEngine {
    BufferedImage doFilter(BufferedImage sourceImage, FilterStrategy filterStrategy);

    BufferedImage rgbToGrayscale(BufferedImage sourceIImage);
}
