package main.algorithm;

import main.filter.FilterStrategy;
import main.filter.ImageType;

import java.util.Arrays;
import java.util.List;

public class OtsuMethod implements FilterStrategy {
    private List<ImageType> typesOfImages = Arrays.asList(ImageType.GRAY);

    @Override
    public byte[] doFilter(byte[] imageData, int height, int width, ImageType imageType) {
        byte[] binarizedImage = null;

        switch (imageType) {
            case GRAY:
                if (imageData.length != width * height) {
                    throw new IllegalArgumentException("Size of imageData should be equal width * height");
                }

                binarizedImage = makeBinary(imageData, computeThreshold(imageData));
                break;
            default:
                throw new IllegalArgumentException("Image type " + imageType.name() + " does not support");
        }

        return binarizedImage;
    }

    @Override
    public boolean compatibleWith(ImageType imageType) {
        return typesOfImages.contains(imageType);
    }

    private byte[] makeBinary(byte[] imageData, int threshold) {
        for (int i = 0; i < imageData.length; i++) {
            if (imageData[i] > threshold) {
                imageData[i] = 0;
            } else {
                imageData[i] = (byte) 255;
            }
        }

        return imageData;
    }

    private int computeThreshold(byte[] sourceImageData) {

        int min = sourceImageData[0];
        int max = sourceImageData[0];

        for (int i = 1; i < sourceImageData.length; i++) {
            int value = sourceImageData[i];

            if (value < min)
                min = value;

            if (value > max)
                max = value;
        }

        int histSize = max - min + 1;
        int[] hist = new int[histSize];

        for (int i = 0; i < sourceImageData.length; i++)
            hist[sourceImageData[i] - min]++;

        int m = 0;
        int n = 0;
        for (int t = 0; t <= max - min; t++) {
            m += t * hist[t];
            n += hist[t];
        }

        float maxSigma = -1;
        int threshold = 0;

        int alpha1 = 0;
        int beta1 = 0;

        for (int t = 0; t < max - min; t++) {
            alpha1 += t * hist[t];
            beta1 += hist[t];

            // Считаем вероятность класса 1.
            float w1 = (float) beta1 / n;
            // Нетрудно догадаться, что w2 тоже не нужна, т.к. она равна 1 - w1

            // a = a1 - a2, где a1, a2 - средние арифметические для классов 1 и 2
            float a = (float) alpha1 / beta1 - (float) (m - alpha1) / (n - beta1);

            // Наконец, считаем sigma
            float sigma = w1 * (1 - w1) * a * a;

            // Если sigma больше текущей максимальной, то обновляем maxSigma и порог
            if (sigma > maxSigma) {
                maxSigma = sigma;
                threshold = t;
            }
        }

        // Не забудем, что порог отсчитывался от min, а не от нуля
        threshold += min;

        // Все, порог посчитан, возвращаем его наверх :)
        return threshold;
    }
}
