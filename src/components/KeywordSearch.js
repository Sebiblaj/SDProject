import React, { useState, useEffect, useCallback, useRef } from 'react';
import './KeywordSearch.css';
import { getFileContentsByPathAndNameAndKeyword, getFilesForTags, getLatestLogsForKeyword, getSpellingSuggestions } from '../Requests/HTTPRequests'; 
import debounce from 'lodash.debounce';
import FileCard from '../components/Reusable/FileCard';
import SpellingSuggestions from './SpellingSuggestions';

const KeywordSearch = () => {
  const [files, setFiles] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [tag, setTag] = useState('');
  const [path, setPath] = useState(''); 
  const [filename, setFilename] = useState(''); 
  const [loading, setLoading] = useState(false);
  const [searching, setSearching] = useState(false);
  const [queries, setQueries] = useState([]);
  const [keywordSpellingSuggestions, setKeywordSpellingSuggestions] = useState(null);
  const [filenameSpellingSuggestions, setFilenameSpellingSuggestions] = useState(null);

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
          setFiles(data.data);
          setSearching(false); 
          setLoading(false);
        })
        .catch(() => {
          setLoading(false);
          setSearching(false);
        });
    } else if (currentTag.trim() !== '') {
      getFilesForTags(currentTag.trim())
        .then((data) => {
          setFiles(data.data);
          setSearching(false);
          setLoading(false);
        })
        .catch(() => {
          setLoading(false);
          setSearching(false);
        });
    } else {
      setLoading(false);
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

  const fetchSpellingSuggestions = (term, setterFunction) => {
    if (!term || term.trim() === '') {
      setterFunction(null);
      return;
    }

    getSpellingSuggestions(term.trim())
      .then(spellingResponse => {
        if(spellingResponse.status === 200 && spellingResponse.data && spellingResponse.data.length > 0){
          setterFunction(spellingResponse.data);
        }else{
          setterFunction(null);
        }
      })
      .catch(err =>{
        setterFunction(null);
        console.log("Error");
      });
  };

  const debouncedFetchSpellingKeyword = useCallback(
    debounce((term) => {
      fetchSpellingSuggestions(term, setKeywordSpellingSuggestions);
    }, 1000),
    []
  );

  const debouncedFetchSpellingFilename = useCallback(
    debounce((term) => {
      fetchSpellingSuggestions(term, setFilenameSpellingSuggestions);
    }, 1000),
    []
  );

  const debouncedFetchFiles = useCallback(
    debounce(() => {
      fetchFiles();
    }, 500),
    [] 
  );

  const handleKeywordChange = (e) => {
    const value = e.target.value;
    setKeyword(value);
    setFiles([]);
    
    if (value.trim() === '') {
      setSearching(false);
      setKeywordSpellingSuggestions(null);
      return;
    }
    
    setSearching(true);
    debouncedFetchFiles();
    debouncedFetchSpellingKeyword(value);
  };

  const handleTagChange = (e) => {
    const value = e.target.value;
    setTag(value);
    setFiles([]);  
    setSearching(true);
    debouncedFetchFiles(); 
  };

  const handlePathChange = (e) => {
    const value = e.target.value;
    setPath(value);
    setFiles([]);  
    setSearching(true);
    debouncedFetchFiles(); 
  };

  const handleFilenameChange = (e) => {
    const value = e.target.value;
    setFilename(value);
    setFiles([]);
    
    if (value.trim() === '') {
      setSearching(false);
      setFilenameSpellingSuggestions(null);
      return;
    }
    
    setSearching(true);
    debouncedFetchFiles();
    debouncedFetchSpellingFilename(value);
  };

  const handleKeywordSuggestionSelect = (suggestion) => {
    setKeyword(suggestion);
    setKeywordSpellingSuggestions(null);
    debouncedFetchFiles();
  };

  const handleFilenameSuggestionSelect = (suggestion) => {
    setFilename(suggestion);
    setFilenameSpellingSuggestions(null);
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

      <div className="search-bar">
        <div className="input-container">
          <input
            type="text"
            value={keyword}
            onChange={handleKeywordChange}
            placeholder="Search for a keyword..."
            className="search-input"
          />
          {keywordSpellingSuggestions && (
            <SpellingSuggestions
              suggestions={keywordSpellingSuggestions}
              onSelect={handleKeywordSuggestionSelect}
              onClose={() => setKeywordSpellingSuggestions(null)}
            />
          )}
        </div>

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

        <div className="input-container">
          <input
            type="text"
            value={filename}
            onChange={handleFilenameChange}
            placeholder="Enter filename (optional)..."
            className="search-input"
          />
          {filenameSpellingSuggestions && (
            <SpellingSuggestions
              suggestions={filenameSpellingSuggestions}
              onSelect={handleFilenameSuggestionSelect}
              onClose={() => setFilenameSpellingSuggestions(null)}
            />
          )}
        </div>
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
