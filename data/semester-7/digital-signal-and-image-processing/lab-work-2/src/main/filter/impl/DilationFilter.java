package main.filter.impl;

import main.filter.FilterStrategy;
import main.filter.ImageType;

import java.util.Arrays;
import java.util.List;

public class DilationFilter implements FilterStrategy {
    private List<ImageType> typesOfImages = Arrays.asList(ImageType.GRAY);
    private int[][] kernel = new int[][]{{255, 255, 255}, {255, 255, 255}, {255, 255, 255}, {1, 1, 1}, {1, 1, 1}};

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

    private byte[] filterGrayscaleImage(byte[] primaryImage, int imageWidth, int imageHeight) {
        byte[] resizedImage = resizeGrayscaleImage(primaryImage, imageWidth, imageHeight);
        byte[] filteredImage = new byte[(imageHeight + 2) * (imageWidth + 2)];
        byte[] dilatedImage = new byte[imageHeight * imageWidth];

        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                if ((kernel[1][1] & Byte.toUnsignedInt(resizedImage[(i + 1) * (imageWidth + 2) + j + imageWidth + 2 + 1])) == 255) {
                    int firstRowIndex = i * (imageWidth + 2) + j + imageWidth + 2 + 1;
                    filteredImage[firstRowIndex - 1] =
                            (byte) (kernel[0][0] | Byte.toUnsignedInt(resizedImage[firstRowIndex]));
                    filteredImage[firstRowIndex] =
                            (byte) (kernel[0][1] | Byte.toUnsignedInt(resizedImage[firstRowIndex]));
                    filteredImage[firstRowIndex + 1] =
                            (byte) (kernel[0][2] | Byte.toUnsignedInt(resizedImage[firstRowIndex + 1]));

                    int secondRowIndex = (i + 1) * (imageWidth + 2) + j + imageWidth + 2 + 1;
                    filteredImage[secondRowIndex - 1] =
                            (byte) (kernel[1][0] | Byte.toUnsignedInt(resizedImage[secondRowIndex - 1]));
                    filteredImage[secondRowIndex] =
                            (byte) (kernel[1][1] | Byte.toUnsignedInt(resizedImage[secondRowIndex]));
                    filteredImage[secondRowIndex + 1] =
                            (byte) (kernel[1][2] | Byte.toUnsignedInt(resizedImage[secondRowIndex + 1]));

                    int thirdRowIndex = (i - 1) * (imageWidth + 2) + j + imageWidth + 2 + 1;
                    filteredImage[thirdRowIndex - 1] =
                            (byte) (kernel[2][0] | Byte.toUnsignedInt(resizedImage[thirdRowIndex - 1]));
                    filteredImage[thirdRowIndex] =
                            (byte) (kernel[2][1] | Byte.toUnsignedInt(resizedImage[thirdRowIndex]));
                    filteredImage[thirdRowIndex + 1] =
                            (byte) (kernel[2][2] | Byte.toUnsignedInt(resizedImage[thirdRowIndex + 1]));
                }
            }
        }

        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                dilatedImage[i * imageWidth + j] = filteredImage[(i + 1) * (imageWidth + 2) + j + 1];
            }
        }

        return dilatedImage;
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
}
