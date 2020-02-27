package main.algorithm;

import java.util.Map;

public interface ClusterAnalysisStrategy {
    Map<Integer, Map<Integer, Map<String, Double>>> doClusterAnalysis(Map<Integer, Map<String, Double>> objectsFeatures, int clustersCount);
}
