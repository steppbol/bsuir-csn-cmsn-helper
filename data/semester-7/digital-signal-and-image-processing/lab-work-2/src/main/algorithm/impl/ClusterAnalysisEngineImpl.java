package main.algorithm.impl;

import main.algorithm.ClusterAnalysisEngine;
import main.algorithm.ClusterAnalysisStrategy;
import main.algorithm.Coordinate;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClusterAnalysisEngineImpl implements ClusterAnalysisEngine {
    public static final String PERIMETER = "PERIMETER";
    public static final String SQUARE = "SQUARE";
    public static final String COMPACTNESS = "COMPACTNESS";
    public static final String ELONGATION = "ELONGATION";

    @Override
    public Map<Integer, Map<Integer, Set<Coordinate>>> doAnalysis(
            BufferedImage bufferedImage, ClusterAnalysisStrategy clusterAnalysisStrategy, int numOfClusters) {
        if (bufferedImage == null || clusterAnalysisStrategy == null) {
            throw new NullPointerException("BufferedImage or FilterStrategy cannot be null");
        }

        Raster imageRaster = bufferedImage.getData();
        byte[] imageData;

        if (imageRaster.getDataBuffer() instanceof DataBufferByte) {
            imageData = ((DataBufferByte) imageRaster.getDataBuffer()).getData();
        } else {
            throw new IllegalArgumentException("BufferedImage with type " + bufferedImage.getType() + "doesn't support (DataBuffer)");
        }

        Map<Integer, Set<Coordinate>> connectedSpaces = labeling(
                imageData, imageRaster.getWidth(), imageRaster.getHeight());
        Map<Integer, Map<String, Double>> objectsFeatures = featureExtraction(
                imageData, imageRaster.getWidth(), imageRaster.getHeight(), connectedSpaces);

        normalizeObjectsFeatures(objectsFeatures);
        Map<Integer, Map<Integer, Map<String, Double>>> clusters = clusterAnalysisStrategy.doClusterAnalysis(objectsFeatures, numOfClusters);

        Map<Integer, Map<Integer, Set<Coordinate>>> clustersWithObjects = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Map<String, Double>>> cluster : clusters.entrySet()) {
            clustersWithObjects.put(cluster.getKey(), new HashMap<>());

            for (Integer objectId : cluster.getValue().keySet()) {
                clustersWithObjects.get(cluster.getKey()).put(objectId, connectedSpaces.get(objectId));
            }
        }

        return clustersWithObjects;
    }

    private void normalizeObjectsFeatures(Map<Integer, Map<String, Double>> objectsFeatures) {
        double perimeterMax = -1;
        double squareMax = -1;
        double compactnessMax = -1;
        double elongationMax = -1;

        for (Map<String, Double> object : objectsFeatures.values()) {
            if (object.get(PERIMETER) > perimeterMax) {
                perimeterMax = object.get(PERIMETER);
            }
            if (object.get(SQUARE) > squareMax) {
                squareMax = object.get(SQUARE);
            }
            if (object.get(COMPACTNESS) > compactnessMax) {
                compactnessMax = object.get(COMPACTNESS);
            }
            if (object.get(ELONGATION) > elongationMax) {
                elongationMax = object.get(ELONGATION);
            }
        }

        for (Map<String, Double> object : objectsFeatures.values()) {
            object.put(PERIMETER, object.get(PERIMETER) / perimeterMax);
            object.put(SQUARE, object.get(SQUARE) / squareMax);
            object.put(COMPACTNESS, object.get(COMPACTNESS) / compactnessMax);
            object.put(ELONGATION, object.get(ELONGATION) / elongationMax);
        }
    }

    private Map<Integer, Map<String, Double>> featureExtraction(byte[] imageData, int width, int height, Map<Integer, Set<Coordinate>> connectedSpaces) {
        Map<Integer, Map<String, Double>> connectedSpacesFeatures = new HashMap<>();

        for (Map.Entry<Integer, Set<Coordinate>> connectedSpace : connectedSpaces.entrySet()) {
            Map<String, Double> map = new HashMap<>();
            connectedSpacesFeatures.put(connectedSpace.getKey(), map);
            double perimeter = calculatePerimeter(imageData, width, height, connectedSpace.getValue());
            double square = connectedSpace.getValue().size();

            map.put(SQUARE, square);
            map.put(PERIMETER, perimeter);
            map.put(COMPACTNESS, Math.pow(perimeter, 2) / square);
            map.put(ELONGATION, calculateElongation(connectedSpace.getValue()));
        }

        return connectedSpacesFeatures;
    }

    private double calculateElongation(Set<Coordinate> pixelsCoordinates) {
        Coordinate centerOfMass = centerOfMass(pixelsCoordinates);
        double m20 = calculateCentralMoment(2, 0, pixelsCoordinates, centerOfMass);
        double m02 = calculateCentralMoment(0, 2, pixelsCoordinates, centerOfMass);
        double m11 = calculateCentralMoment(1, 1, pixelsCoordinates, centerOfMass);

        double sqrt = Math.sqrt((Math.pow((m20 - m02), 2) + 4 * Math.pow(m11, 2)));

        return (m20 + m02 + sqrt) / (m20 + m02 - sqrt);
    }

    private double calculateCentralMoment(int i, int j, Set<Coordinate> pixelsCoordinates, Coordinate centerOfMass) {
        double centralMoment = 0;
        for (Coordinate coordinate : pixelsCoordinates) {
            centralMoment += Math.pow((coordinate.getX() - centerOfMass.getX()), i) * Math.pow((coordinate.getY() - centerOfMass.getY()), j);
        }

        return centralMoment;
    }

    private Coordinate centerOfMass(Set<Coordinate> pixelsCoordinates) {
        double sumX = 0;
        double sumY = 0;
        for (Coordinate pixelCoordinate : pixelsCoordinates) {
            sumX += pixelCoordinate.getX();
            sumY += pixelCoordinate.getY();
        }

        return new Coordinate(sumX / pixelsCoordinates.size(), sumY / pixelsCoordinates.size(), 0);
    }

    private int calculatePerimeter(byte[] imageData, int width, int height, Set<Coordinate> pixelsCoordinates) {
        int perimeter = 0;
        for (Coordinate pixelCoordinate : pixelsCoordinates) {
            int x = (int) pixelCoordinate.getX();
            int y = (int) pixelCoordinate.getY();

            if (x > 0 && imageData[y * width + x - 1] == 0) {
                perimeter++;
                continue;
            }
            if (x < width - 1 && imageData[y * width + x + 1] == 0) {
                perimeter++;
                continue;
            }
            if (y > 0 && imageData[(y - 1) * width + x] == 0) {
                perimeter++;
                continue;
            }
            if (y < height - 1 && imageData[(y + 1) * width + x] == 0) {
                perimeter++;
                continue;
            }
        }

        return perimeter;
    }

    private Map<Integer, Set<Coordinate>> labeling(byte[] imageData, int width, int height) {
        Map<Integer, Set<Coordinate>> connectedSpaces = new HashMap<>();
        byte[] labels = new byte[imageData.length];

        int label = 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                connectedSpaces.put(label, new HashSet<>());
                fill(imageData, labels, width, height, x, y, label, connectedSpaces);

                if (connectedSpaces.get(label).size() == 0) {
                    connectedSpaces.remove(label);
                }
                label++;
            }
        }

        return connectedSpaces;
    }

    private void fill(byte[] imageData, byte[] labels, int width, int height, int x, int y, int label, Map<Integer, Set<Coordinate>> connectedSpaces) {
        if (Byte.toUnsignedInt(labels[y * width + x]) == 0 && Byte.toUnsignedInt(imageData[y * width + x]) == 255) {
            labels[y * width + x] = (byte) 255;
            connectedSpaces.get(label).add(new Coordinate(x, y, y * width + x));

            if (x > 0)
                fill(imageData, labels, width, height, x - 1, y, label, connectedSpaces);
            if (x < width - 1)
                fill(imageData, labels, width, height, x + 1, y, label, connectedSpaces);
            if (y > 0)
                fill(imageData, labels, width, height, x, y - 1, label, connectedSpaces);
            if (y < height - 1)
                fill(imageData, labels, width, height, x, y + 1, label, connectedSpaces);
        }
    }
}
