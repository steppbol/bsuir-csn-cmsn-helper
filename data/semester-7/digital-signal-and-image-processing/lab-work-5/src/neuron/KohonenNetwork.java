package neuron;

import java.util.Random;

public class KohonenNetwork {
    private Neuron[] inputLayer;
    private Neuron[] outputLayer;
    private double[][] inputOutputLayersWeights;
    private double beta;
    private double precision;

    public KohonenNetwork(int inputLayerSize, int outputLayerSize, double beta, double precision) {
        this.inputLayer = new Neuron[inputLayerSize];
        initArray(inputLayer);
        this.outputLayer = new Neuron[outputLayerSize];
        initArray(outputLayer);
        this.inputOutputLayersWeights = new double[inputLayerSize][outputLayerSize];
        this.beta = beta;
        this.precision = precision;

        initializeWeights(inputOutputLayersWeights);
    }

    public double[] recognizeImage(int[] image) {
        double[] result = new double[outputLayer.length];

        for (int i = 0; i < inputLayer.length; i++) {
            inputLayer[i].setState(image[i]);
        }

        calculateNeuronsOutputs();

        for (int i = 0; i < outputLayer.length; i++) {
            result[i] = outputLayer[i].getState();
        }

        return result;
    }

    public void training(int[][] image) {
        boolean[] trainedImages = new boolean[image.length];
        int[] winningCount = new int[outputLayer.length];

        boolean isNotRecognizedImagesStayed;

        do {
            isNotRecognizedImagesStayed = false;

            for (int i = 0; i < image.length; i++) {
                if (trainedImages[i]) {
                    continue;
                }

                for (int j = 0; j < inputLayer.length; j++) {
                    inputLayer[j].setState(image[i][j]);
                }

                int winnerIndex = findWinnerNeuron(winningCount);

                if (precision > findDistance(winnerIndex)) {
                    trainedImages[i] = true;
                    continue;
                }

                correctWinnerNeuronWeights(winnerIndex);
            }

            for (boolean trainedImage : trainedImages) {
                if (!trainedImage) {
                    isNotRecognizedImagesStayed = true;
                    break;
                }
            }
        } while (isNotRecognizedImagesStayed);
    }

    private void initArray(Neuron[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = new Neuron();
        }
    }

    private void initializeWeights(double[][] weightsMatrix) {
        Random random = new Random();

        for (int i = 0; i < weightsMatrix.length; i++) {
            for (int j = 0; j < weightsMatrix[i].length; j++) {
                weightsMatrix[i][j] = random.nextDouble();
            }
        }
    }

    private double findDistance(int winnerNeuronIndex) {
        double distance = 0;
        for (int j = 0; j < inputLayer.length; j++) {
            distance += Math.pow(inputLayer[j].getState() - inputOutputLayersWeights[j][winnerNeuronIndex], 2);
        }

        return Math.sqrt(distance);
    }

    private int findWinnerNeuron(int[] winningCount) {
        int winnerNeuronIndex = 0;
        double winnerValue = -1;

        for (int i = 0; i < outputLayer.length; i++) {
            double distance = 0;
            for (int j = 0; j < inputLayer.length; j++) {
                distance += Math.pow(inputLayer[j].getState() - inputOutputLayersWeights[j][i], 2);
            }
            distance = Math.sqrt(distance);
            double temp = Math.abs(distance) * winningCount[i];

            if (winnerValue > temp || i == 0) {
                winnerValue = temp;
                winnerNeuronIndex = i;
            }
        }
        winningCount[winnerNeuronIndex]++;

        return winnerNeuronIndex;
    }

    private void correctWinnerNeuronWeights(int neuronIndex) {
        for (int i = 0; i < inputOutputLayersWeights.length; i++) {
            double weightValue = inputOutputLayersWeights[i][neuronIndex];
            inputOutputLayersWeights[i][neuronIndex] = weightValue + beta * (inputLayer[i].getState() - weightValue);
        }
    }

    private void calculateNeuronsOutputs() {
        for (int i = 0; i < outputLayer.length; i++) {
            double inputWeightSum = 0;
            for (int j = 0; j < inputLayer.length; j++) {
                inputWeightSum += inputLayer[j].getState() * inputOutputLayersWeights[j][i];
            }
            outputLayer[i].setState(inputWeightSum);
        }
    }
}
