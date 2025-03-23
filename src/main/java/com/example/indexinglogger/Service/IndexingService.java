package com.example.indexinglogger.Service;

import com.example.indexinglogger.DTOs.*;
import com.example.indexinglogger.Entities.IndexingLog;
import com.example.indexinglogger.Executors.FileProcessors.FileEntitiesProcessor;
import com.example.indexinglogger.Executors.HTTPProcessors.HttpGetTypesHandler;
import com.example.indexinglogger.Executors.HTTPProcessors.HttpPostContentsHandler;
import com.example.indexinglogger.Executors.HTTPProcessors.HttpPostFilesHandler;
import com.example.indexinglogger.Persistence.IndexingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexingService {


    @Value("${message.addFile.fail}")
    private String addFileFail;

    @Value("${message.addFile.success}")
    private String addFileSuccess;

    @Autowired
    private IndexingRepository indexingRepository;

    @Autowired
    private HttpGetTypesHandler httpGetTypesHandler;

    @Autowired
    private HttpPostFilesHandler httpPostFilesHandler;

    @Autowired
    private FileEntitiesProcessor fileEntitiesProcessor;

    @Autowired
    private HttpPostContentsHandler httpPostContentsHandler;

    @Value("${request.types.getAllTypesWithoutId}")
    private String getTypesWithoutIdPath;

    @Value("${request.file.postAllFiles}")
    private String postAllFilesPath;

    @Value("${request.content.postMultipleContent}")
    private String postMultipleContentPath;

    public String scanAndSendFiles(PathDTO startDir) throws IOException {

        List<FileTypeDTO> fileTypes = httpGetTypesHandler.processList(getTypesWithoutIdPath);

        if(fileTypes != null && !fileTypes.isEmpty()){

            List<FileDTO> fileDTOS = new ArrayList<>();
            List<FileContentDTO> fileContentDTOS = new ArrayList<>();

          List<FileFullContents> files = fileEntitiesProcessor.process(startDir.getPath(), fileTypes);

            for(FileFullContents fileFullContents:files){
                fileDTOS.add(fileFullContents.getFile());
                fileContentDTOS.add(fileFullContents.getContents());
            }


            String result =  httpPostFilesHandler.process(postAllFilesPath,fileDTOS);
            String result2 = httpPostContentsHandler.process(postMultipleContentPath, fileContentDTOS);

          List<IndexingLog> indexingLogs = new ArrayList<>();

          if(result.equals("Could not add files") || result2.equals("Could not load contents")){
             for(FileFullContents file : files){
                 IndexingLog log = new IndexingLog();
                 log.setFilename(file.getFile().getFilename());
                 log.setStatus("Failed");
                 log.setMessage(addFileFail);
                 log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                 indexingLogs.add(log);
             }
          }else{
              for(FileFullContents file : files){
                  IndexingLog log = new IndexingLog();
                  log.setFilename(file.getFile().getFilename());
                  log.setStatus("Success");
                  log.setMessage(addFileSuccess);
                  log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                  log.setPath(file.getFile().getPath());
                  indexingLogs.add(log);
              }
          }
          indexingRepository.saveAll(indexingLogs);
          return  result;
        }

        return "Could not index files";
    }

    public List<IndexingLog> getIndexingLog(){return indexingRepository.findAll();}

}
