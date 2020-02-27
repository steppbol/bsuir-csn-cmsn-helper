package main.algorithm;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;

public interface ClusterAnalysisEngine {
    Map<Integer, Map<Integer, Set<Coordinate>>> doAnalysis(BufferedImage bufferedImage, ClusterAnalysisStrategy clusterAnalysisStrategy, int numOfClusters);
}
