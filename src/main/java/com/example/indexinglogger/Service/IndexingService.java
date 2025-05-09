package com.example.indexinglogger.Service;

import com.example.indexinglogger.DTOs.*;
import com.example.indexinglogger.Executors.FileProcessors.FileEntitiesProcessor;
import com.example.indexinglogger.Executors.HTTPProcessors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexingService {


    @Value("${message.addFile.fail}")
    private String addFileFail;

    @Value("${message.addFile.success}")
    private String addFileSuccess;

    @Autowired
    private HttpGetTypesHandler httpGetTypesHandler;

    @Autowired
    private HttpPostFilesHandler httpPostFilesHandler;

    @Autowired
    private FileEntitiesProcessor fileEntitiesProcessor;

    @Autowired
    private HttpPostContentsHandler httpPostContentsHandler;

    @Autowired
    private HttpGetTagsFromOpenAI httpGetTagsFromOpenAI;

    @Autowired
    private HttpGetAllFilesHandler httpGetAllFilesHandler;

    @Value("${kafka.topic.indexing}")
    private String TOPIC;

    @Value("${request.types.getAllTypesWithoutId}")
    private String getTypesWithoutIdPath;

    @Value("${request.file.postAllFiles}")
    private String postAllFilesPath;

    @Value("${request.content.postMultipleContent}")
    private String postMultipleContentPath;

    @Value("${request.openai.getTagsFromContent}")
    private String tagsFromContentPath;

    @Value("${request.file.getAllFile}")
    private String getAllFilesPath;

    private final List<String> imageExtensions = List.of("jpg","jpeg","png","gif","bmp");

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public void KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Object message) {
        kafkaTemplate.send(TOPIC, message);
    }

    public String scanAndSendFiles(PathDTO startDir) throws IOException {

        boolean success = true;

        List<FileTypeDTO> fileTypes = httpGetTypesHandler.processList(getTypesWithoutIdPath);
        List<ReturnedFileDTO> indexedFiles = httpGetAllFilesHandler.processList(getAllFilesPath);

        if (fileTypes == null || fileTypes.isEmpty()) {
            return "Could not get file types.";
        }

        List<FileDTO> fileDTOS = new ArrayList<>();
        List<FileFullContents> files = fileEntitiesProcessor.process(startDir.getPath(), fileTypes, indexedFiles);

        List<FileContentDTO> fileContents = files.stream()
                .map(FileFullContents::getContents)
                .collect(Collectors.toList());

        List<FileContentDTO> fileContentDTOS = fileContents.stream().filter(f -> !imageExtensions.contains(f.getExtension().toLowerCase())).toList();

        List<OpenAIResponse> openAIResponses = httpGetTagsFromOpenAI.processList(tagsFromContentPath, fileContentDTOS);

        System.out.println("The response is: "+ openAIResponses.toString());
        for (FileFullContents fileFullContents : files) {
            FileDTO fileDTO = fileFullContents.getFile();

            if (!imageExtensions.contains(fileDTO.getType().toLowerCase())) {
                String tagsString = openAIResponses.stream()
                        .filter(response -> response.getFilename().equals(fileDTO.getFilename()))
                        .map(OpenAIResponse::getTags)
                        .flatMap(List::stream)
                        .collect(Collectors.joining(","));

                if (!tagsString.isBlank()) {
                    List<Tag> tags = Arrays.stream(tagsString.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Tag::new)
                            .toList();
                    fileDTO.setTags(tags.stream().map(Tag::getTag).collect(Collectors.toList()));
                }
            } else {
                fileDTO.setTags(List.of("image"));
            }

            fileDTOS.add(fileDTO);
            sendMessage(fileDTO);
        }

        String result = httpPostFilesHandler.process(postAllFilesPath, fileDTOS);
        String result2 = httpPostContentsHandler.process(postMultipleContentPath, fileContents);

        if ("Could not add files".equals(result) || "Could not load contents".equals(result2)) {
            System.out.println(result2);
            System.out.println(result);
            success = false;
        }

        return success ? addFileSuccess : addFileFail;
    }

}
