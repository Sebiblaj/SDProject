package org.example.queryworker;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class QueryWorkerApplication {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Usage: QueryWorkerApplication <path> <command>");
            return;
        }

        String searchPath = args[0];
        String command = args[1];


        File dir = new File(searchPath);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Invalid directory: " + searchPath);
            return;
        }

        String query = command.replaceFirst("query:","");

        if(query.startsWith("file/")){
            String filename = query.replaceFirst("file/", "");
            List<FileDTO> fileDTOS= ServiceExecutors.findFilesByFileName(dir, filename);
            for(FileDTO fileDTO : fileDTOS){
                try {
                    String jsonOutput = mapper.writeValueAsString(fileDTO);
                    System.out.println(jsonOutput);
                }catch (Exception e){
                    System.err.println("Could not convert fileDTO to JSON");
                }
            }
        }
    }

}
