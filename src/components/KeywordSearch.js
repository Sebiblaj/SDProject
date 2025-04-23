import React, { useState, useEffect, useCallback, useRef } from 'react';
import './KeywordSearch.css';
import { getFileContentsByPathAndNameAndKeyword, getFilesForTags, getLatestLogsForKeyword } from '../Requests/HTTPRequests'; 
import debounce from 'lodash.debounce';
import FileCard from '../components/Reusable/FileCard';

const KeywordSearch = () => {
  const [files, setFiles] = useState([]);
  const [error, setError] = useState('');
  const [keyword, setKeyword] = useState('');
  const [tag, setTag] = useState('');
  const [path, setPath] = useState(''); 
  const [filename, setFilename] = useState(''); 
  const [loading, setLoading] = useState(false);
  const [searching, setSearching] = useState(false);
  const [queries, setQueries] = useState([]);

  const keywordRef = useRef(keyword);
  const tagRef = useRef(tag);
  const pathRef = useRef(path);
  const filenameRef = useRef(filename);

  useEffect(() => {
    keywordRef.current = keyword;
    tagRef.current = tag;
    pathRef.current = path;
    filenameRef.current = filename;
  }, [keyword, tag, path, filename]);

  useEffect(() => {
    fetchQueries();
  }, []);

  const fetchFiles = () => {
    setLoading(true);
    setSearching(true);

    const currentKeyword = keywordRef.current;
    const currentTag = tagRef.current;
    const currentPath = pathRef.current;
    const currentFilename = filenameRef.current;

    if (currentKeyword.trim() !== '') {
      getFileContentsByPathAndNameAndKeyword(currentPath.trim(), currentFilename.trim(), currentKeyword.trim())
        .then((data) => {
          console.log('Files found by keyword:', data);
          setFiles(data.data);
          setSearching(false); 
          setLoading(false);
        })
        .catch(() => {
          setError('Failed to search files by keyword.');
          setLoading(false);
          setSearching(false);
        });
    } else if (currentTag.trim() !== '') {
      getFilesForTags(currentTag.trim())
        .then((data) => {
          console.log('Files found by tag:', data);
          setFiles(data.data);
          setSearching(false);
          setLoading(false);
        })
        .catch(() => {
          setError('Failed to search files by tag.');
          setLoading(false);
          setSearching(false);
        });
    } else {
      setLoading(false);
      setError('Please enter a keyword or tag to search.');
      setSearching(false);
    }
  };

  const fetchQueries = () => {
    setLoading(true);
    getLatestLogsForKeyword().then((data) => {
      setQueries(data.data);
      setLoading(false);
    });
  };

  const debouncedFetchFiles = useCallback(
    debounce(() => {
      fetchFiles();
    }, 500),
    [] 
  );

  const handleKeywordChange = (e) => {
    const value = e.target.value;
    setKeyword(value);
    setError('');
    setFiles([]);  
    setSearching(true);
    debouncedFetchFiles();
  };

  const handleTagChange = (e) => {
    const value = e.target.value;
    setTag(value);
    console.log("The tag is ", value);
    setError('');
    setFiles([]);  
    setSearching(true);
    debouncedFetchFiles(); 
  };

  const handlePathChange = (e) => {
    const value = e.target.value;
    setPath(value);
    setError('');
    setFiles([]);  
    setSearching(true);
    debouncedFetchFiles(); 
  };

  const handleFilenameChange = (e) => {
    const value = e.target.value;
    setFilename(value);
    setError('');
    setFiles([]);  
    setSearching(true);
    debouncedFetchFiles(); 
  };

  useEffect(() => {
    if (files.length > 0) {
      setLoading(false);  
    }
  }, [files]);

  const highlightKeyword = (text) => {
    if (!keyword.trim()) return text;
    const regex = new RegExp(`(${keyword.trim()})`, 'gi'); 
    return text.replace(regex, (match) => `<span class="highlight">${match}</span>`);
  };

  return (
    <div className="search-container">
      <h2 className="search-title">Search Files</h2>

      {error && <div className="error">{error}</div>}

      <div className="search-bar">
        <input
          type="text"
          value={keyword}
          onChange={handleKeywordChange}
          placeholder="Search for a keyword..."
          className="search-input"
        />

        <input
          type="text"
          value={tag}
          onChange={handleTagChange}
          placeholder="Search for a tag..."
          className="search-input"
        />

        <input
          type="text"
          value={path}
          onChange={handlePathChange}
          placeholder="Enter path (optional)..."
          className="search-input"
        />

        <input
          type="text"
          value={filename}
          onChange={handleFilenameChange}
          placeholder="Enter filename (optional)..."
          className="search-input"
        />
      </div>

      {loading && files.length === 0 && <div className="loading">Loading...</div>}

      <div className="results">
        {!loading && files.length > 0 && keyword.trim() !== '' && (
          <div className="keyword-results">
            {files.map((file) => (
              <div key={file.filename} className="file-result">
                <h4>{file.filename}</h4>
                <p>Path: {file.path}</p>
                <ul>
                  {file.lineNumbers.map((lineNumber, index) => (
                    <li key={lineNumber}>
                      Line {lineNumber}: 
                      <span 
                        dangerouslySetInnerHTML={{
                          __html: highlightKeyword(file.excerpts[index]),
                        }} 
                      />
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        )}

        {!loading && files.length > 0 && tag.trim() !== '' && (
          <div className="tag-results">
            {files.map((file) => (
              <FileCard key={file.id} file={file} />
            ))}
          </div>
        )}

        {!loading && files.length === 0 && !searching && (
          <div>No files found.</div>
        )}
      </div>
      <div className="queries-list">
        <h3>Previous Queries</h3>
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
      </div>
    </div>
  );
};

export default KeywordSearch;
