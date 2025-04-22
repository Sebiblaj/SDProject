package com.example.localsearchengine.ServiceExecutors.Convertors;

import com.example.localsearchengine.DTOs.MetadataDTOS.MetadataDTO;
import com.example.localsearchengine.Entites.Metadata;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MetadataConvertor implements Convertor<Metadata, MetadataDTO> {

    @Override
    public MetadataDTO convert(Metadata payload) {
        Map<String, Object> metadata = new HashMap<>(payload.getValues());

        String lastAccessedTimestamp = (String) metadata.get("lastaccess");
        if (lastAccessedTimestamp != null) {
            String formattedLastAccessed = formatTimestampToReadable(lastAccessedTimestamp);
            metadata.put("lastaccess", formattedLastAccessed);
        }

        String lastModifiedTimestamp = (String) metadata.get("lastmodified");
        if (lastAccessedTimestamp != null) {
            String formattedLastAccessed = formatTimestampToReadable(lastModifiedTimestamp);
            metadata.put("lastmodified", formattedLastAccessed);
        }

        String creation = (String) metadata.get("creationtime");
        if (lastAccessedTimestamp != null) {
            String formattedLastAccessed = formatTimestampToReadable(creation);
            metadata.put("creationtime", formattedLastAccessed);
        }

        return new MetadataDTO(metadata);
    }

    private String formatTimestampToReadable(String timestamp) {
        Instant instant = Instant.ofEpochMilli(Long.parseLong(timestamp));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }
}
