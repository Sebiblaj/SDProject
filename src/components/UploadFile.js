import React, { useEffect, useState } from 'react';
import './UploadFile.css';
import { addFiles, geAllTypes,setFileContentsByPathAndName,getLatestPostLogsForFiles } from '../Requests/HTTPRequests';
import { rankingFunction } from '../helpers/RankingFunction';

const UploadFiles = () => {
  const [filePath, setFilePath] = useState('');
  const [fileName, setFileName] = useState('');
  const [fileExtension, setFileExtension] = useState('');
  const [tags, setTags] = useState('');
  const [content, setContent] = useState('');
  const [metadata, setMetadata] = useState([{ key: '', value: '' }]);
  const [isUploading, setIsUploading] = useState(false);
  const [error, setError] = useState('');
  const [allowedExt, setAllowedExt] = useState([]);
  const [queries, setQueries] = useState([]);  
  

  useEffect(() => {
    const fetchExtensions = async () => {
      try {
        const extensions = await geAllTypes();
        setAllowedExt(extensions);
      } catch (error) {
        console.error('Failed to fetch extensions:', error);
        setError('Failed to load allowed extensions.');
      }
    };

    fetchExtensions();
    fetchQueries();
  }, []);

    const fetchQueries = () => {
      setIsUploading(true);
      getLatestPostLogsForFiles()
        .then((data) => {
          console.log('Queries fetched:', data);
          setQueries(data.data); 
          setIsUploading(false);
        })
        .catch(() => {
          setError('Failed to load queries.');
          setIsUploading(false);
        });
    };

  const handleTagsChange = (e) => {
    setTags(e.target.value);
  };

  const handleContentChange = (e) => {
    setContent(e.target.value);
  };

  const handleMetadataChange = (index, field, value) => {
    const updatedMetadata = [...metadata];
    updatedMetadata[index][field] = value;
    setMetadata(updatedMetadata);
  };

  const addMetadataField = () => {
    setMetadata([...metadata, { key: '', value: '' }]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsUploading(true);

    const userMetadata = metadata.reduce((acc, pair) => {
      if (pair.key) acc[pair.key] = pair.value;
      return acc;
    }, {});

    const defaultMetadata = {
      readable: 'true',
      writable: 'true',
      executable: 'false',
      extension: fileExtension,
      filename: fileName,
      filepath: filePath,
      fullpath: `${filePath}\\${fileName}.${fileExtension}`,
      lastmodified: Date.now().toString(),
      creationtime: Date.now().toString(),
      lastaccess: Date.now().toString(),
      filesize: '0',
      depth: filePath.split('\\').length.toString(),
      mimetype: 'text/plain',
      filehash: await generateHash(`${filePath}\\${fileName}.${fileExtension}-${Date.now()}`),
      weight: '0.0',
    };

    const finalMetadata = { ...defaultMetadata, ...userMetadata };

    const rankingInput = {
      filesize: parseFloat(finalMetadata.filesize),
      depth: parseInt(finalMetadata.depth),
      extension: finalMetadata.extension,
      creationtime: parseInt(finalMetadata.creationtime),
      lastmodified: parseInt(finalMetadata.lastmodified),
      lastaccess: parseInt(finalMetadata.lastaccess),
      readable: finalMetadata.readable === 'true',
      writable: finalMetadata.writable === 'true',
      executable: finalMetadata.executable === 'true',
    };

    const computedWeight = rankingFunction(rankingInput, allowedExt.data);
    finalMetadata.weight = computedWeight.toFixed(3);

    const fileDTO = {
      filename: fileName,
      path: filePath,
      type: fileExtension,
      tags: tags.split(',').map((tag) => tag.trim()),
      metadata: finalMetadata,
    };

    console.log("File : ",fileDTO)

    try {
        const response = await addFiles(fileDTO);
        const response2 = await setFileContentsByPathAndName(fileDTO.path, fileDTO.filename, content);
      
        if (response.status === 200 && response2.status === 200) {
          alert('File added successfully!');
          setError(null);
        } else {
          setError('Failed to upload file.');
        }
      } catch (err) {
        console.error('Error during file upload:', err);
        setError('An error occurred while uploading.');
      } finally {
        setIsUploading(false);
      }
    }
      

  async function generateHash(input) {
    const encoder = new TextEncoder();
    const data = encoder.encode(input);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map((b) => b.toString(16).padStart(2, '0')).join('');
    return hashHex;
  }

  return (
    <div>
    <div className="upload-container">
      <h2>📤 Upload a New File</h2>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="upload-form">
        <div className="form-group">
          <label>File Path</label>
          <input type="text" value={filePath} onChange={(e) => setFilePath(e.target.value)} placeholder="Enter file path" required />
        </div>

        <div className="form-group">
          <label>Filename</label>
          <input type="text" value={fileName} onChange={(e) => setFileName(e.target.value)} placeholder="Enter file name" required />
        </div>

        <div className="form-group">
          <label>Extension</label>
          <input
            type="text"
            value={fileExtension}
            onChange={(e) => setFileExtension(e.target.value)}
            placeholder="Enter file extension"
            required
          />
        </div>

        <div className="form-group">
          <label>Tags (comma-separated)</label>
          <input type="text" value={tags} onChange={handleTagsChange} placeholder="tag1, tag2, tag3" />
        </div>

        <div className="form-group">
          <label>Content</label>
          <textarea value={content} onChange={handleContentChange} placeholder="Write or paste content here..." rows="5" />
        </div>

        <div className="form-group">
          <label>Metadata (key-value pairs)</label>
          {metadata.map((pair, index) => (
            <div key={index} className="metadata-row">
              <input
                type="text"
                placeholder="Key"
                value={pair.key}
                onChange={(e) => handleMetadataChange(index, 'key', e.target.value)}
              />
              <input
                type="text"
                placeholder="Value"
                value={pair.value}
                onChange={(e) => handleMetadataChange(index, 'value', e.target.value)}
              />
            </div>
          ))}
          <button type="button" onClick={addMetadataField} className="add-btn">
            + Add Metadata
          </button>
        </div>

        <div className="form-group">
          <button type="submit" disabled={isUploading}>
            {isUploading ? 'Uploading...' : 'Upload File'}
          </button>
        </div>
      </form>
    </div>
    <div className="queries-list">
        <h3>Previous Queries</h3>
        {queries.length > 0 ? (
          <ul>
            {queries.map((query, index) => (
              <li key={index} className="query-item">
                <div className="query-header">
                  <span>{query.timestamp}</span>
                </div>
                <div className="query-title">{query.fileName} - {query.filePath}</div>
                <div className="query-message">{query.message}</div>
              </li>
            ))}
          </ul>
        ) : (
          <div>No queries found.</div>
        )}
      </div>
    </div>
    
  );
};

export default UploadFiles;
