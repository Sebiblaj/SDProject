import API from './Paths';

function toQuery(params) {
  return new URLSearchParams(params).toString();
}

export function getAllFiles() {
  return fetch(`${API.filePath}`).then(res => res.json());
}

export function getFileByPathAndName(path, name) {
  return fetch(`${API.filePath}/search?${toQuery({ filePath: path, fileName: name })}`)
    .then(res => res.json());
}

export function searchFilesByName(filename) {
  return fetch(`${API.filePath}/search?fileName=${encodeURIComponent(filename)}`)
    .then(res => res.json());
}

export function getFilesByExtension(ext) {
  return fetch(`${API.filePath}/search?${toQuery({ extension: ext })}`)
    .then(res => res.json());
}

export function getFilesWithinInterval(min, max) {
  return fetch(`${API.filePath}/search?${toQuery({ min, max })}`)
    .then(res => res.json());
}

export function deleteFiles(namesList) {
  return fetch(`${API.filePath}/delete`, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(namesList)
  });
}

export function addFiles(payload) {
  return fetch(`${API.filePath}/add`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
}

export function deleteAll() {
  return fetch(`${API.filePath}/delete?all=all`, { method: 'DELETE' });
}

export function updateFile(path, name, payload) {
  return fetch(`${API.filePath}/update?${toQuery({ filePath: path, fileName: name })}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
}

export function getFileContentsByPathAndName(path, name, extension) {
  return fetch(`${API.contentsPath}?${toQuery({ filePath: path, fileName: name, extension })}`)
    .then(res => res.json());
}

export function getFilePreviewByPathAndName(path, name) {
  return fetch(`${API.contentsPath}/preview?${toQuery({ filePath: path, fileName: name })}`)
    .then(res => res.json());
}

export function getFileContentsByPathAndNameAndKeyword(path, name, keyword) {
  return fetch(`${API.contentsPath}/search`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      names: name ? (Array.isArray(name) ? name : [name]) : [],
      paths: path ? (Array.isArray(path) ? path : [path]) : [],
      keywords: keyword ? (Array.isArray(keyword) ? keyword : [keyword]) : []
    })
  }).then(res => res.json());
}

export function setFileContentsByPathAndName(path, filename, content) {
  return fetch(`${API.filePath}/add?${toQuery({ filePath: path, fileName: filename })}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: content
  });
}

export function deleteContentsByPathAndName(path, name) {
  return fetch(`${API.filePath}/delete?${toQuery({ filePath: path, fileName: name })}`, {
    method: 'DELETE'
  });
}


export function geAllTypes() {
  return fetch(`${API.typePath}`).then(res => res.json());
}

export function addNewType(type, weight) {
  return fetch(`${API.typePath}/add?${toQuery({ ext: type, weight })}`)
    .then(res => res.json());
}

export function updateFileTypeWeight(type, weight) {
  return fetch(`${API.typePath}/update`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ type, weight })
  });
}

export function deleteFileType(extensions) {
  return fetch(`${API.filePath}/delete`, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(extensions)
  });
}


export function getTagsForFile(path, name) {
  return fetch(`${API.filePath}/tags?${toQuery({ filePath: path, fileName: name })}`)
    .then(res => res.json());
}

export function getAllTags() {
  return fetch(`${API.filePath}tags`).then(res => res.json());
}

export function getFilesForTags(tags) {
  return fetch(`${API.filePath}/tags/file?${toQuery({ tags })}`)
    .then(res => res.json());
}

export function addTagsForFile(path, name, tags) {
  return fetch(`${API.filePath}tags/add?${toQuery({ filePath: path, fileName: name })}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(tags)
  });
}

export function deleteTagsForFile(path, name, tags) {
  return fetch(`${API.filePath}tags/delete?${toQuery({ filePath: path, fileName: name })}`, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(tags)
  });
}

export function deleteAllTagsForFile(path, name) {
  return fetch(`${API.filePath}tags/delete?${toQuery({ filePath: path, fileName: name })}`, {
    method: 'DELETE'
  });
}


export function getMetadataForFile(path, name, extension) {
  return fetch(`${API.metadataPath}?${toQuery({ filePath: path, fileName: name, extension })}`)
    .then(res => res.json());
}

export function modifyMetadataForFile(path, name, payload) {
  return fetch(`${API.metadataPath}modify?${toQuery({ filePath: path, fileName: name })}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
}

export function addMetadataForFile(path, name, payload) {
  return fetch(`${API.metadataPath}add?${toQuery({ filePath: path, fileName: name })}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
}

export function deleteMetadataForFile(path, name, keys) {
  return fetch(`${API.metadataPath}delete?${toQuery({ filePath: path, fileName: name })}`, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(keys)
  });
}


export function reindexFiles(path) {
  return fetch(`${API.indexingPath}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ path })
  });
}


export function getLatestLogsForKeyword() {
  return fetch(`${API.loggerPath}/params?${toQuery({
    activity: 'READ',
    queryType: 'TAGS', 
    status: 'SUCCESS'
  })}`).then(res => res.json());
}

export function getLatestLogsForFiles() {
  return fetch(`${API.loggerPath}/params?${toQuery({
    activity: 'READ',
    queryType: 'FILE',
    status: 'SUCCESS'
  })}`).then(res => res.json());
}

export function getLatestPostLogsForFiles() {
  return fetch(`${API.loggerPath}/params?${toQuery({
    activity: 'CREATE',
    queryType: 'FILE',
    status: 'SUCCESS'
  })}`).then(res => res.json());
}


export function getSpellingSuggestions(word) {
  return fetch(`${API.spellingPath}?word=${encodeURIComponent(word)}`)
    .then(res => res.json());
}


export function getWidgets(keyword) {
  return fetch(`${API.widgetsPath}/${keyword}`).then(res => res.json());
}

export function getWidgetsForFiles(widgetName, criteria) {
  return fetch(`${API.widgetsPath}/${widgetName}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(criteria)
  }).then(res => res.json());
}
