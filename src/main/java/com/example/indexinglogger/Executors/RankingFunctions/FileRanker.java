package com.example.indexinglogger.Executors.RankingFunctions;

import com.example.indexinglogger.DTOs.FileTypeDTO;
import com.example.indexinglogger.Entities.FileProperties;

import java.time.Instant;
import java.util.List;

public class FileRanker {

    private static final double RECENCY_ACCESS_WEIGHT = 0.8;
    private static final double RECENCY_CREATED_WEIGHT = 0.3;
    private static final double RECENCY_UPDATED_WEIGHT = 0.6;
    private static final double SIZE_WEIGHT = 0.1;
    private static final double DEPTH_WEIGHT = 0.25;
    private static final double EXTENSION_WEIGHT = 0.6;
    private static final double READABLE_WEIGHT = 1.0;
    private static final double WRITABLE_WEIGHT = 1.5;
    private static final double EXECUTABLE_WEIGHT = 0.1;

    public float rankingFunction(FileProperties props, List<FileTypeDTO> extensions) {
        long timeNow = Instant.now().getEpochSecond();

        long creationAge = (timeNow - props.getCreationTime().getTime());
        long lastModifiedAge = (timeNow - props.getLastModified().getTime());
        long lastAccessedAge = (timeNow - props.getLasAccessed().getTime());

        double recencyFactor = 1.0 / (1 + (double) creationAge / (60 * 60));
        double lastModifiedFactor = 1.0 / (1 + (double) lastModifiedAge / (60 * 60));
        double accessFactor = 1.0 / (1 + (double) lastAccessedAge / (60 * 60));

        double sizeFactor = Math.min(1.0, props.getFileSize() / (1024.0 * 1024.0));

        double extensionFactor = extensions.stream()
                .filter(f -> f.getType().equals(props.getExtension()))
                .findFirst()
                .map(FileTypeDTO::getWeight)
                .orElse(0.0);

        int depth = props.getDepth();
        double depthFactor = 1.0 / (1 + depth * DEPTH_WEIGHT);

        double recencyScore = RECENCY_ACCESS_WEIGHT * accessFactor + RECENCY_CREATED_WEIGHT * recencyFactor + RECENCY_UPDATED_WEIGHT * lastModifiedFactor;
        double sizeScore = SIZE_WEIGHT * sizeFactor;
        double extensionScore = EXTENSION_WEIGHT * extensionFactor;
        double depthScore = DEPTH_WEIGHT * depthFactor;
        double result = recencyScore + sizeScore + extensionScore + depthScore;
        if(props.isReadable()) result = result * READABLE_WEIGHT;
        if(props.isWritable()) result = result * WRITABLE_WEIGHT;
        if(props.isExecutable()) result = result * EXECUTABLE_WEIGHT;

        return (float) result;
    }


}
