package main.filter.impl;

import main.filter.FilterStrategy;
import main.filter.ImageType;

import java.util.Arrays;
import java.util.List;

public class ContrastFilter implements FilterStrategy {
    private List<ImageType> typesOfImages = Arrays.asList(ImageType.GRAY, ImageType.RGB);
    private int gMin;
    private int gMax;
    private final static int RGB_SIZE = 3;

    public ContrastFilter(int gMin, int gMax) {
        if (gMin < 1) {
            gMin = 1;
        }
        if (gMax < 1) {
            gMax = 1;
        }

        this.gMin = gMin;
        this.gMax = gMax;
    }

    @Override
    public byte[] doFilter(byte[] imageData, int height, int width, ImageType imageType) {
        if ((imageType.equals(ImageType.GRAY) && imageData.length != width * height)
                || (imageType.equals(ImageType.RGB) && imageData.length != width * height * RGB_SIZE)) {
            throw new IllegalArgumentException("imageData and image size different");
        }

        return contrast(imageData);
    }

    @Override
    public boolean compatibleWith(ImageType imageType) {
        return typesOfImages.contains(imageType);
    }

    private byte[] contrast(byte[] sourceImageData) {
        byte[] contrastedImageData = new byte[sourceImageData.length];

        for (int i = 0; i < sourceImageData.length; i++) {
            contrastedImageData[i] = (byte) ((Byte.toUnsignedInt(sourceImageData[i]) / 255.0) * (this.gMax - this.gMin) + this.gMin);
        }
        return contrastedImageData;
    }
}
