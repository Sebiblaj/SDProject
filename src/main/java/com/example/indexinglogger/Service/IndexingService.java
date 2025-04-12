package com.example.indexinglogger.Service;

import com.example.indexinglogger.DTOs.*;
import com.example.indexinglogger.Executors.FileProcessors.FileEntitiesProcessor;
import com.example.indexinglogger.Executors.HTTPProcessors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
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
    private HttpPostMetadataHandler httpPostMetadataHandler;

    @Autowired
    private HttpGetTagsFromOpenAI httpGetTagsFromOpenAI;

    @Autowired
    private HttpPostTagsHandler httpPostTagsHandler;

    @Value("${request.types.getAllTypesWithoutId}")
    private String getTypesWithoutIdPath;

    @Value("${request.file.postAllFiles}")
    private String postAllFilesPath;

    @Value("${request.content.postMultipleContent}")
    private String postMultipleContentPath;

    @Value("${request.openai.getTagsFromContent}")
    private String tagsFromContentPath;

    @Value("${request.metadata.postMetadata}")
    private String postMetadataPath;

    @Value("${request.tags.postTags}")
    private String postTagsPath;

    public String scanAndSendFiles(PathDTO startDir) throws IOException {

        boolean success = true;

        List<FileTypeDTO> fileTypes = httpGetTypesHandler.processList(getTypesWithoutIdPath);

        if(fileTypes != null && !fileTypes.isEmpty()){

            List<FileDTO> fileDTOS = new ArrayList<>();
            List<FileContentDTO> fileContentDTOS = new ArrayList<>();

            List<FileFullContents> files = fileEntitiesProcessor.process(startDir.getPath(), fileTypes);

            System.out.println("File processed successfully");

            for(FileFullContents fileFullContents:files){
                FileDTO fileDTO = fileFullContents.getFile();
                fileContentDTOS.add(fileFullContents.getContents());

                String tagsString = httpGetTagsFromOpenAI.processList(tagsFromContentPath,fileFullContents.getContents()).getFirst();
                System.out.println("Tags processed successfully");

                if (tagsString != null && !tagsString.isBlank()) {
                    List<Tag> tags = Arrays.stream(tagsString.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Tag::new)
                            .toList();
                    fileDTO.setTags(tags.stream().map(Tag::getTag).collect(Collectors.toList()));
                }
                fileDTOS.add(fileFullContents.getFile());
            }

            String result =  httpPostFilesHandler.process(postAllFilesPath,fileDTOS);
            System.out.println(result);
            String result2 = httpPostContentsHandler.process(postMultipleContentPath, fileContentDTOS);
            System.out.println(result2);


          if(result.equals("Could not add files") || result2.equals("Could not load contents")){
               success = false;
          }
        }

        return success ? addFileFail : addFileSuccess;
    }

}
