package org.example.querymanager.ServiceLayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.querymanager.Entities.FileDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QueryService {

    private final ObjectMapper objectMapper;

    private static final int MAX_WORKERS = 10;
    private static final String WORKER_CLASS = "org.example.queryworker.QueryWorkerApplication";
    private static final String WORKER_CLASS_PATH = "C:\\Users\\sebir\\OneDrive - Technical University of Cluj-Napoca\\Desktop\\SD\\QueryWorker\\out\\";
    private static final String SEARCH_PATH = "C:\\Users\\sebir\\OneDrive - Technical University of Cluj-Napoca\\Desktop\\TESTDIR";
    private static final String COMMAND = "query:file/";
    private final String libPath = "C:\\Users\\sebir\\OneDrive - Technical University of Cluj-Napoca\\Desktop\\SD\\QueryWorker\\lib\\*";
    private final String workerClassPath = WORKER_CLASS_PATH + ";" + libPath;

    private final Map<String, List<FileDTO>> cache = new ConcurrentHashMap<>();

    public QueryService() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public List<FileDTO> getAllFiles(String fileName) {

        if (cache.containsKey(fileName)) {
            System.out.println("Returning cached results for: " + fileName);
            return cache.get(fileName);
        }

        File rootDir = new File(SEARCH_PATH);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            return new ArrayList<>();
        }

        File[] subDirs = rootDir.listFiles(File::isDirectory);
        if (subDirs == null || subDirs.length == 0) {
            return new ArrayList<>();
        }

        int numWorkers = Math.min(MAX_WORKERS, subDirs.length);
        List<List<File>> partitions = divideSubdirectories(subDirs, numWorkers);

        ExecutorService executor = Executors.newFixedThreadPool(numWorkers);
        StringBuilder output = new StringBuilder();
        List<FileDTO> files = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(numWorkers);

        try {
            for (List<File> workerDirs : partitions) {
                executor.execute(() -> {
                    for (File dir : workerDirs) {
                        ProcessBuilder pb = new ProcessBuilder("java", "-cp", workerClassPath, WORKER_CLASS, dir.getAbsolutePath(), COMMAND.concat(fileName));
                        pb.redirectErrorStream(true);
                        try {
                            Process process = pb.start();
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    synchronized (output) {
                                        FileDTO fileInfo = objectMapper.readValue(line, FileDTO.class);
                                        System.out.println("Found file: " + fileInfo);
                                        files.add(fileInfo);
                                    }
                                }
                            }
                            process.waitFor();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    latch.countDown();
                });
            }
            executor.shutdown();

            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }

        cache.put(fileName, files);

        return files;
    }

    private List<List<File>> divideSubdirectories(File[] subdirs, int numWorkers) {
        List<List<File>> partitions = new ArrayList<>();
        for (int i = 0; i < numWorkers; i++) {
            partitions.add(new ArrayList<>());
        }
        for (int i = 0; i < subdirs.length; i++) {
            partitions.get(i % numWorkers).add(subdirs[i]);
        }
        return partitions;
    }
}
