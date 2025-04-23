import axios from 'axios';
import API from './Paths';

// files requests
export function getAllFiles() {
  return axios.get(`${API.filePath}`);
}

export function getFileByPathAndName(path, name) {
  return axios.get(`${API.filePath+'/search'}`, {
    params: {
      filePath: path,
      fileName: name
    }
  });
}

export function searchFilesByName(filename) {
  return axios.get(`${API.filePath+'/search'}?fileName=${encodeURIComponent(filename)}`)
    .then(response => response.data);
}


export function getFilesByExtension(ext) {
  return axios.get(`${API.filePath+'/search'}`, {
    params: {
      extension : ext
    }
  });
}

export function getFilesWithinInterval(min,max) {
  return axios.get(`${API.filePath+'/search'}`, {
    params: {
      min : min ,
      max : max
    }
  });
}

export function deleteFiles(namesList) {
  return axios.delete(`${API.filePath + '/delete'}`, namesList) ;
}

export function addFiles(payload) {
  return axios.post(`${API.filePath + '/add'}`, payload);
}

export function deleteAll() {
  return axios.delete(`${API.filePath + '/delete'}`,{
    params : {
      all : 'all'
    }
  }) ;
}

export function updateFile(path, name, payload) {
  return axios.put(`${API.filePath}/update`, payload, {
    params: {
      filePath: path,
      fileName: name
    }
  });
}


//contents requests
export function getFileContentsByPathAndName(path, name) {
  return axios.get(`${API.contentsPath}`, {
    params: {
      filePath: path,
      fileName: name
    }
  });
}

export function getFilePreviewByPathAndName(path, name) {
  return axios.get(`${API.contentsPath+'/preview'}`, {
    params: {
      filePath: path,
      fileName: name
    }
  });
}

export function getFileContentsByPathAndNameAndKeyword(path, name,keyword) {
  return axios.get(`${API.contentsPath+'/search'}`, {
    params: {
      filePath: path,
      fileName: name,
      content : keyword
    }
  });
}

export function setFileContentsByPathAndName(path,filename,content) {
  return axios.post(`${API.filePath+'/add'}`,JSON.parse(content), {
    params: {
      filePath: path,
      fileName: filename
    }
  });
}

export function deleteContentsByPathAndName(path, name) {
  return axios.delete(`${API.filePath+'/delete'}`, {
    params: {
      filePath: path,
      fileName: name
    }
  });
}


//requests for types
export function geAllTypes() {
  return axios.get(`${API.typePath}`);
}

export function addNewType(type,weight) {
  return axios.get(`${API.typePath+'/add'}`, {
    params: {
      ext : type,
      weight : weight
    }
  });
}

export function updateFileTypeWeight(type,weight) {
  return axios.put(`${API.typePath+'/update'}`, {
    type : type ,
    weight : weight
  });
}

export function deleteFileType(extensions) {
  return axios.delete(`${API.filePath+'/delete'}`, extensions);
}



//requests for tags

export function getTagsForFile(path, name) {
  return axios.get(`${API.filePath+'/tags'}`, {
    params: {
      filePath: path,
      fileName: name
    }
  });
}

export function getAllTags() {
  return axios.get(`${API.filePath+'tags'}`);
}

export function getFilesForTags(tags) {
  return axios.get(`${API.filePath+'/tags/file'}`, {
    params: {
      tags : tags
    }
  });
}

export function addTagsForFile(path, name,tags) {
  return axios.post(`${API.filePath+'tags/add'}`,tags, {
    params: {
      filePath: path,
      fileName: name
    }
  });
}

export function deleteTagsForFile(path, name,tags) {
  return axios.delete(`${API.filePath+'tags/delete'}`, tags,{
    params: {
      filePath: path,
      fileName: name
    }
  });
}

export function deleteAllTagsForFile(path, name) {
  return axios.delete(`${API.filePath+'tags/delete'}`,{
    params: {
      filePath: path,
      fileName: name
    }
  });
}


//requests for metadata
export function getMetadataForFile(path, name) {
  return axios.get(`${API.metadataPath}`,{
    params: {
      filePath: path,
      fileName: name
    }
  });
}

export function modifyMetadataForFile(path, name,payload) {
  return axios.put(`${API.metadataPath+'modify'}`,payload,{
    params: {
      filePath: path,
      fileName: name
    }
  });
}

export function addMetadataForFile(path, name,payload) {
  return axios.post(`${API.metadataPath+'add'}`,payload,{
    params: {
      filePath: path,
      fileName: name
    }
  });
}

export function deleteMetadataForFile(path, name,keys) {
  return axios.delete(`${API.metadataPath+'delete'}`,keys,{
    params: {
      filePath: path,
      fileName: name
    }
  });
}


//request for indexing
export function reindexFiles(path) {
  return axios.post(`${API.indexingPath}`,{
    path : path
  });
}


//request for logger
export function getLatestLogsForKeyword(){
  return axios.get(`${API.loggerPath+'/params'}`,{
    params : {
        activity : 'READ',
        queryType : 'CONTENTS',
        queryType : 'TAGS',
        status : 'SUCCESS'
    }
  });
}

export function getLatestLogsForFiles(){
  return axios.get(`${API.loggerPath+'/params'}`,{
    params : {
        activity : 'READ',
        queryType : 'FILE',
        status : 'SUCCESS'
    }
  });
}

export function getLatestPostLogsForFiles(){
  return axios.get(`${API.loggerPath+'/params'}`,{
    params : {
        activity : 'CREATE',
        queryType : 'FILE',
        status : 'SUCCESS'
    }
  });
}