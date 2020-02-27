package main.filter.impl;

import main.filter.FilterStrategy;
import main.filter.ImageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MedianFilter implements FilterStrategy {
    private List<ImageType> typesOfImages = Arrays.asList(ImageType.GRAY);

    @Override
    public byte[] doFilter(byte[] imageData, int height, int width, ImageType imageType) {
        byte[] filteredImageData = null;

        switch (imageType) {
            case GRAY:
                if (imageData.length != width * height) {
                    throw new IllegalArgumentException("Size of imageData should be equal width * height");
                }

                filteredImageData = filterGrayscaleImage(imageData, width, height);
                break;
            default:
                throw new IllegalArgumentException("Image type " + imageType.name() + " does not support");
        }

        return filteredImageData;
    }

    @Override
    public boolean compatibleWith(ImageType imageType) {
        return typesOfImages.contains(imageType);
    }

    private byte[] resizeGrayscaleImage(byte[] primaryImage, int imageWidth, int imageHeight) {
        byte[] resizedImage = new byte[(imageHeight + 2) * (imageWidth + 2)];

        for (int i = 0, n = 0; i < imageHeight; i++, n++) {
            for (int j = 0, m = 0; j < imageWidth; j++, m++) {
                resizedImage[n * (imageWidth + 2) + m] = primaryImage[i * imageWidth + j];

                if (j == 0 || j == imageWidth - 1) {
                    m++;
                    resizedImage[n * (imageWidth + 2) + m] = primaryImage[i * imageWidth + j];
                }
            }

            if (n == 0 || n == imageHeight) {
                i--;
            }
        }

        return resizedImage;
    }

    private byte[] filterGrayscaleImage(byte[] primaryImage, int imageWidth, int imageHeight) {
        byte[] resizedImage = resizeGrayscaleImage(primaryImage, imageWidth, imageHeight);
        byte[] filteredImage = new byte[imageHeight * imageWidth];

        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                List<Integer> pixels = new ArrayList<>(9);

                pixels.add(Byte.toUnsignedInt(resizedImage[i * (imageWidth + 2) + j - 1 + imageWidth + 2 + 1]));
                pixels.add(Byte.toUnsignedInt(resizedImage[i * (imageWidth + 2) + j + imageWidth + 2 + 1]));
                pixels.add(Byte.toUnsignedInt(resizedImage[i * (imageWidth + 2) + j + 1 + imageWidth + 2 + 1]));

                pixels.add(Byte.toUnsignedInt(resizedImage[(i + 1) * (imageWidth + 2) + j - 1 + imageWidth + 2 + 1]));
                pixels.add(Byte.toUnsignedInt(resizedImage[(i + 1) * (imageWidth + 2) + j + imageWidth + 2 + 1]));
                pixels.add(Byte.toUnsignedInt(resizedImage[(i + 1) * (imageWidth + 2) + j + 1 + imageWidth + 2 + 1]));

                pixels.add(Byte.toUnsignedInt(resizedImage[(i - 1) * (imageWidth + 2) + j - 1 + imageWidth + 2 + 1]));
                pixels.add(Byte.toUnsignedInt(resizedImage[(i - 1) * (imageWidth + 2) + j + imageWidth + 2 + 1]));
                pixels.add(Byte.toUnsignedInt(resizedImage[(i - 1) * (imageWidth + 2) + j + 1 + imageWidth + 2 + 1]));

                Collections.sort(pixels);

                filteredImage[i * imageWidth + j] = pixels.get(4).byteValue();
            }
        }

        return filteredImage;
    }
}
