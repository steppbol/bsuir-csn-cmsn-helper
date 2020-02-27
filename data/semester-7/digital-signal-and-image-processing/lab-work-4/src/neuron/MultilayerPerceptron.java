package neuron;

import java.util.Random;

public class MultilayerPerceptron {
    private Neuron[] inputLayer;
    private Neuron[] hideLayer;
    private Neuron[] outputLayer;
    private double[][] inputHideLayersWeights;
    private double[][] hideOutputLayersWeights;
    private double alpha;
    private double precision;

    public MultilayerPerceptron(int inputLayerSize, int hideLayerSize, int outputLayerSize, double alpha, double precision) {
        this.inputLayer = new Neuron[inputLayerSize];
        initArray(inputLayer);
        this.hideLayer = new Neuron[hideLayerSize];
        initArray(hideLayer);
        this.outputLayer = new Neuron[outputLayerSize];
        initArray(outputLayer);

        this.inputHideLayersWeights = new double[inputLayerSize][hideLayerSize];
        this.hideOutputLayersWeights = new double[hideLayerSize][outputLayerSize];
        this.alpha = alpha;
        this.precision = precision;

        initializeWeights(inputHideLayersWeights);
        initializeWeights(hideOutputLayersWeights);
        initializeThreshold(hideLayer);
        initializeThreshold(outputLayer);
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
                weightsMatrix[i][j] = random.nextDouble() * (random.nextBoolean() ? -1 : 1);
            }
        }
    }

    private void initializeThreshold(Neuron[] neurons) {
        Random random = new Random();

        for (Neuron neuron : neurons) {
            neuron.setThreshold(random.nextDouble() * (random.nextBoolean() ? -1 : 1));
        }
    }

    public void training(int[][] image, int[][] trueResult) {
        boolean[] trainedImages = new boolean[image.length];
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

                calculateHideLayerOutputs();
                calculateOutputLayerOutputs();

                if (precision > findMaxMistake(outputLayer, trueResult[i])) {
                    trainedImages[i] = true;
                    continue;
                }

                recalculateOutputLayerWeights(hideLayer, outputLayer, hideOutputLayersWeights, trueResult[i]);
                recalculateHideLayerWeights(inputLayer, hideLayer, outputLayer, inputHideLayersWeights, hideOutputLayersWeights, trueResult[i]);
            }

            for (boolean trainedImage : trainedImages) {
                if (!trainedImage) {
                    isNotRecognizedImagesStayed = true;
                    break;
                }
            }
        } while (isNotRecognizedImagesStayed);
    }

    public double[] recognizeImage(int[] image) {
        double[] result = new double[outputLayer.length];

        for (int i = 0; i < inputLayer.length; i++) {
            inputLayer[i].setState(image[i]);
        }

        calculateHideLayerOutputs();
        calculateOutputLayerOutputs();

        for (int i = 0; i < outputLayer.length; i++) {
            result[i] = outputLayer[i].getState();
        }

        return result;
    }

    private double findMaxMistake(Neuron[] neurons, int[] trueResult) {
        double maxMistake = -1;

        for (int i = 0; i < neurons.length; i++) {
            double mistake = Math.abs(trueResult[i] - neurons[i].getState());

            if (maxMistake < mistake) {
                maxMistake = mistake;
            }
        }

        return maxMistake;
    }

    private void calculateHideLayerOutputs() {
        calculateLayersWeights(hideLayer, inputLayer, inputHideLayersWeights);
    }

    private void calculateOutputLayerOutputs() {
        calculateLayersWeights(outputLayer, hideLayer, hideOutputLayersWeights);
    }

    private void calculateLayersWeights(Neuron[] firstLayer, Neuron[] secondLayer, double[][] layersWeights) {
        for (int i = 0; i < firstLayer.length; i++) {
            double inputWeightSum = 0;
            for (int j = 0; j < secondLayer.length; j++) {
                inputWeightSum += secondLayer[j].getState() * layersWeights[j][i];
            }
            firstLayer[i].setState(activationFunction(inputWeightSum + firstLayer[i].getThreshold()));
        }
    }

    private void recalculateOutputLayerWeights(Neuron[] hideLayer, Neuron[] outputLayer, double[][] layersWeights, int[] trueResult) {
        for (int i = 0; i < layersWeights.length; i++) {
            for (int j = 0; j < layersWeights[i].length; j++) {
                double neuronState = outputLayer[j].getState();
                layersWeights[i][j] = layersWeights[i][j] + alpha * neuronState * (1 - neuronState)
                        * (trueResult[j] - neuronState) * hideLayer[i].getState();
            }
        }
        for (int i = 0; i < outputLayer.length; i++) {
            double neuronState = outputLayer[i].getState();
            outputLayer[i].setThreshold(outputLayer[i].getThreshold() + alpha * neuronState * (1 - neuronState)
                    * (trueResult[i] - neuronState));
        }
    }

    private void recalculateHideLayerWeights(Neuron[] inputLayer, Neuron[] hideLayer, Neuron[] outputLayer,
                                             double[][] inputHideLayersWeights, double[][] hideOutputLayersWeights, int[] trueResult) {
        for (int i = 0; i < inputHideLayersWeights.length; i++) {
            for (int j = 0; j < inputHideLayersWeights[i].length; j++) {
                double mistake = 0;
                for (int k = 0; k < outputLayer.length; k++) {
                    double neuronState = outputLayer[k].getState();
                    mistake += (trueResult[k] - neuronState) * neuronState * (1 - neuronState) * hideOutputLayersWeights[j][k];
                }

                double temp = alpha * hideLayer[j].getState() * (1 - hideLayer[j].getState()) * mistake;
                inputHideLayersWeights[i][j] = inputHideLayersWeights[i][j] + temp * inputLayer[i].getState();

                if (i == 0) {
                    hideLayer[j].setThreshold(hideLayer[j].getThreshold() + temp);
                }
            }
        }
    }

    private double activationFunction(double x) {
        return 1.0 / (1 + Math.pow(Math.E, -x));
    }
}
