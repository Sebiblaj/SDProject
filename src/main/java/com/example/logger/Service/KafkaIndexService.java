package com.example.logger.Service;

import com.example.logger.DTOS.FileDTO;
import com.example.logger.Entities.ActivityType;
import com.example.logger.Entities.QueryType;
import com.example.logger.Entities.Status;
import com.example.logger.Entities.SystemLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Component
public class KafkaIndexService {

    @Autowired
    private FileWriter fileWriterIndex ;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "index", groupId = "group1")
    public void listen(String messageJson) {
        try {
            FileDTO fileDTO = objectMapper.readValue(messageJson, FileDTO.class);

            writeTextLog(fileDTO);
            writeJsonLog(fileDTO);
            writeCsvLog(fileDTO);
            writeMarkdownLog(fileDTO);

        } catch (IOException e) {
            System.out.println("Error deserializing or writing log: " + e.getMessage());
        }
    }

    private void writeTextLog(FileDTO fileDTO) throws IOException {
        String log = String.format(
                """
                📁 File Indexed
                ──────────────────────────────
                📄 Filename     : %s
                📂 Path         : %s
                📃 Type         : %s
                🏷️ Tags          : %s
                📌 Metadata     : %s
                🕑 Indexed At    : %s
                ──────────────────────────────
                """,
                fileDTO.getFilename(),
                fileDTO.getPath(),
                fileDTO.getType(),
                fileDTO.getTags(),
                fileDTO.getMetadata(),
                Timestamp.from(Instant.now())
        );

        fileWriterIndex.write(log + "\n\n");
        fileWriterIndex.flush();
    }

    private void writeJsonLog(FileDTO fileDTO) throws IOException {
        String fileName = "logs/file_index_log.json";
        File file = new File(fileName);
        file.getParentFile().mkdirs();

        try (FileWriter fw = new FileWriter(file, true)) {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fileDTO);
            fw.write(json + ",\n\n");
        }
    }

    private void writeCsvLog(FileDTO fileDTO) throws IOException {
        String fileName = "logs/file_index_log.csv";
        File file = new File(fileName);
        file.getParentFile().mkdirs();

        boolean fileExists = file.exists();

        try (FileWriter fw = new FileWriter(file, true);
             PrintWriter pw = new PrintWriter(fw)) {

            if (!fileExists) {
                pw.println("Filename,Path,Type,Tags,Metadata");
            }

            String tags = String.join(" | ", fileDTO.getTags());
            String metadata = flattenMetadata(fileDTO.getMetadata());

            pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    fileDTO.getFilename(),
                    fileDTO.getPath(),
                    fileDTO.getType(),
                    tags,
                    metadata);
        }
    }

    private void writeMarkdownLog(FileDTO fileDTO) throws IOException {
        String fileName = "logs/file_index_log.md";
        File file = new File(fileName);
        file.getParentFile().mkdirs();

        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(String.format(
                    """
                    ## 📄 File Indexed: `%s`

                    - **Path:** `%s`
                    - **Type:** `%s`
                    - **Tags:** %s
                    - **Metadata:** %s
                    - **Indexed At:** %s

                    ---
                    """,
                    fileDTO.getFilename(),
                    fileDTO.getPath(),
                    fileDTO.getType(),
                    fileDTO.getTags(),
                    fileDTO.getMetadata(),
                    Timestamp.from(Instant.now())
            ));
        }
    }

    private String flattenMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        metadata.forEach((k, v) -> sb.append(k).append(": ").append(v).append(" | "));
        return sb.toString();
    }

}
