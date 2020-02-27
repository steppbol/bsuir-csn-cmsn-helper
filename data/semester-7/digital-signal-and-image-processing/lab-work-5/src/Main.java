import data.Instances;
import neuron.KohonenNetwork;

import java.util.*;

public class Main {
    private static final String RED_BOLD = "\033[1;31m";    // RED
    private static final String RESET = "\033[0m";  // Text Reset
    private static final String WHITE_BACKGROUND = "\033[40m";  // BLACK
    private static final String WHITE = "\033[0;37m";   // WHITE
    private static final String BLUE_BACKGROUND = "\033[44m";   // BLUE

    public static void main(String[] args) {
        int[] crashedImage;
        int[][] images = {Instances.TWO, Instances.THREE, Instances.FOUR, Instances.FIVE, Instances.SEVEN};

        KohonenNetwork kohonenNetwork = new KohonenNetwork(36, 5, 0.05, 0.01);

        kohonenNetwork.training(images);

        for (int[] image : images) {
            for (int i = 0; i <= 100; i += 10) {
                System.out.println("==========" + i + "%==========");
                crashedImage = crashImage(image, i);
                printImage(crashedImage);
                System.out.println();
                printResult(kohonenNetwork.recognizeImage(crashedImage));
            }
        }
    }

    private static void printImage(int[] symbol) {
        for (int i = 0; i < symbol.length; i++) {
            if (i != 0 && i % 6 == 0) {
                System.out.println();
            }
            if (symbol[i] == 1) {
                System.out.printf(RED_BOLD + BLUE_BACKGROUND + "%2d" + RESET, symbol[i]);
            } else
                System.out.printf(WHITE + WHITE_BACKGROUND + "%2d" + RESET, symbol[i]);
        }
        System.out.println();
    }

    private static void printResult(double[] result) {
        for (double value : result) {
            System.out.printf(RED_BOLD + WHITE_BACKGROUND + "%.5f" + RESET + " ", value);
        }
        System.out.println();
    }

    private static int[] crashImage(int[] inputImage, int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage cannot be more than 100% or less than 0%");
        }

        List<Integer> indexes = new ArrayList<>(inputImage.length);
        Set<Integer> indexesForChange = new HashSet<>(inputImage.length);

        Random random = new Random();
        int[] crashedImage = inputImage.clone();

        for (int i = 0; i < inputImage.length; i++) {
            indexes.add(i);
        }

        int pixelForChange = (inputImage.length * percentage) / 100;
        for (int i = 0; i < pixelForChange; i++) {
            int randomIndex = random.nextInt(indexes.size());
            indexesForChange.add(indexes.get(randomIndex));
            indexes.remove(randomIndex);
        }

        for (Integer index : indexesForChange) {
            if (inputImage[index] == 1)
                crashedImage[index] = 0;
            else
                crashedImage[index] = 1;
        }

        return crashedImage;
    }
}
