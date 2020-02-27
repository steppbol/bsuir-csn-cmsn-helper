package main.algorithm.impl;

import main.algorithm.ClusterAnalysisStrategy;

import java.util.*;

public class Kmeans implements ClusterAnalysisStrategy {
    @Override
    public Map<Integer, Map<Integer, Map<String, Double>>> doClusterAnalysis(Map<Integer, Map<String, Double>> objectsFeatures, int clustersCount) {
        Map<Integer, Map<Integer, Map<String, Double>>> clusters;
        // номер кластера : значение координат центра
        Map<Integer, Map<String, Double>> kMeans = randomClusterCentres(objectsFeatures, clustersCount);

        Map<Integer, Map<String, Double>> objectsForCalculateDistance = new HashMap<>(objectsFeatures);
        int j = 0;
        do {
            double minDistance = 100;
            clusters = getNewClusterMap(clustersCount);

            for (Map.Entry<Integer, Map<String, Double>> object : objectsForCalculateDistance.entrySet()) {
                int clusterNum = 0;

                for (int i = 0; i < clustersCount; i++) {
                    double calculatedDistance = calculateDistance(kMeans.get(i), object.getValue());

                    if (i == 0 || calculatedDistance < minDistance) {
                        minDistance = calculatedDistance;
                        clusterNum = i;
                    }
                }
                clusters.get(clusterNum).put(object.getKey(), object.getValue());
            }

            Map<Integer, Map<String, Double>> kMeansNew = calculateNewClustersCentres(clusters);

            if(kMeansNew == null) {
                kMeans = randomClusterCentres(objectsFeatures, clustersCount);
                continue;
            }

            boolean equal = isClustersCentersEquals(kMeans, kMeansNew);

            if (equal) {
                break;
            } else {
                kMeans = kMeansNew;
            }
        } while (true);

        return clusters;
    }

    private Map<Integer, Map<Integer, Map<String, Double>>> getNewClusterMap(int clustersCount) {
        Map<Integer, Map<Integer, Map<String, Double>>> clusters = new HashMap<>(); // номер кластера : номера объектов
        for (int i = 0; i < clustersCount; i++) {
            clusters.put(i, new HashMap<>());
        }

        return clusters;
    }

    private boolean isClustersCentersEquals(Map<Integer, Map<String, Double>> kMeansOld, Map<Integer, Map<String, Double>> kMeansNew) {
        for (Map.Entry<Integer, Map<String, Double>> oldCluster : kMeansOld.entrySet()) {
            Map<String, Double> newParameters = kMeansNew.get(oldCluster.getKey());

            for (Map.Entry<String, Double> oldParameter : oldCluster.getValue().entrySet()) {
                if (!oldParameter.getValue().equals(newParameters.get(oldParameter.getKey()))) {
                    return false;
                }
            }
        }

        return true;
    }

    private Map<Integer, Map<String, Double>> calculateNewClustersCentres(Map<Integer, Map<Integer, Map<String, Double>>> clusters) {
        Map<Integer, Map<String, Double>> kMeans = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, Map<String, Double>>> cluster : clusters.entrySet()) {
            Map<String, Double> parameters = new HashMap<>();
            if (cluster.getValue().size() == 0) {
                return null;
            }

            for (Map.Entry<Integer, Map<String, Double>> object : cluster.getValue().entrySet()) {
                for (Map.Entry<String, Double> parameter : object.getValue().entrySet()) {
                    if (!parameters.containsKey(parameter.getKey())) {
                        parameters.put(parameter.getKey(), parameter.getValue());
                    } else {
                        double previousValue = parameters.get(parameter.getKey());
                        parameters.put(parameter.getKey(), previousValue + parameter.getValue());
                    }
                }
            }
            for (Map.Entry<String, Double> parameter : parameters.entrySet()) {
                double parameterValue = parameter.getValue();
                parameters.put(parameter.getKey(), parameterValue / cluster.getValue().entrySet().size());
            }

            kMeans.put(cluster.getKey(), parameters);
        }

        return kMeans;
    }

    private double calculateDistance(Map<String, Double> clusterKernel, Map<String, Double> object) {
        double distance = 0;

        for (Map.Entry<String, Double> kernelEntry : clusterKernel.entrySet()) {
            distance += Math.pow(Math.abs(kernelEntry.getValue() - object.get(kernelEntry.getKey())), 2);
        }

        return Math.sqrt(distance);
    }

    private Map<Integer, Map<String, Double>> randomClusterCentres(Map<Integer, Map<String, Double>> objectsFeatures, int clustersCount) {
        List<Integer> keyList = new ArrayList<>(objectsFeatures.keySet());

        Map<Integer, Map<String, Double>> kMeans = new HashMap<>(); // номер кластера : номер обьекта
        Random random = new Random();

        for (int i = 0; i < clustersCount; i++) {
            int randomCluster = random.nextInt(keyList.size());
            kMeans.put(i, new HashMap<>(objectsFeatures.get(keyList.get(randomCluster))));
            keyList.remove(i);
        }

        return kMeans;
    }
}
