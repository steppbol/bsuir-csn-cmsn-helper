package main.java;

import java.util.*;

public class Hopfield {
    private Neuron[] neurons;
    private int[] lastOutputValues;
    private int[][] weightMatrix;
    public static final int SIZE = 100;

    public Hopfield() {
        neurons = new Neuron[SIZE];
        lastOutputValues = new int[SIZE];
        weightMatrix = new int[SIZE][SIZE];

        for (int i = 0; i < neurons.length; i++) {
            neurons[i] = new Neuron(i);
        }

        initializeCoefficients(Instances.LETTER_A);
        initializeCoefficients(Instances.LETTER_N);
        initializeCoefficients(Instances.LETTER_P);
    }


    private void initializeCoefficients(int[] inputImage) {
        for (int i = 0; i < inputImage.length; i++) {
            for (int j = 0; j < inputImage.length; j++) {
                if (i == j)
                    weightMatrix[i][j] = 0;
                else
                    weightMatrix[i][j] += inputImage[i] * inputImage[j];
            }
        }
    }

    public int[] findImage(int[] inputImage) {
        int times = 10000;
        boolean isImageRecognized = true;
        int[] recognizedImage = inputImage.clone();

        while (!isImageRecognized || times-- > 0) {
            //заносим входящие значения
            for (int i = 0; i < recognizedImage.length; i++) {
                neurons[i].setX(recognizedImage[i]);
                lastOutputValues[i] = neurons[i].getY();
            }

            //сначало вычисляем S потом У
            for (int i = 0; i < recognizedImage.length; i++) {
                int newState = 0;
                for (int j = 0; j < SIZE; j++) {
                    newState += weightMatrix[neurons[i].getIndex()][j] * neurons[j].getX();
                }
                neurons[i].setState(newState);
                neurons[i].changeState();
            }
            isImageRecognized = true;

            //проверяем на равенство входного и выходного векторов
            for (int i = 0; i < lastOutputValues.length; i++) {
                if (lastOutputValues[i] != neurons[i].getY()) {
                    isImageRecognized = false;
                    break;
                }
            }

            for (int i = 0; i < recognizedImage.length; i++) {
                recognizedImage[i] = neurons[i].getY();
            }
        }

        return recognizedImage;
    }

    public int[] crashImage(int[] inputImage, int percentage) {
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
                crashedImage[index] = -1;
            else
                crashedImage[index] = 1;
        }

        return crashedImage;
    }
}
