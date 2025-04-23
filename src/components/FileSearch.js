import React, { useState, useEffect, useCallback } from 'react';
import './FileSearch.css';
import { getAllFiles, searchFilesByName, getLatestLogsForFiles } from '../Requests/HTTPRequests';
import Button from './Reusable/Button';
import debounce from 'lodash.debounce';
import FileCard from '../components/Reusable/FileCard';

const FileSearch = ({ onFileSelect }) => {
  const [files, setFiles] = useState([]);
  const [queries, setQueries] = useState([]);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(false);
  const [searching, setSearching] = useState(false);

  const fetchFiles = (term) => {
    setLoading(true);
    setError('');

    const request = term.trim() === '' ? getAllFiles() : searchFilesByName(term.trim());

    request
      .then((data) => {
        const resultFiles = term.trim() === '' ? data.data : data;
        setFiles(resultFiles);
      })
      .catch(() => {
        setError('Failed to fetch files.');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const fetchQueries = () => {
    setLoading(true);
    getLatestLogsForFiles()
      .then((data) => {
        setQueries(data.data);
      })
      .catch(() => {
        setError('Failed to load queries.');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const debouncedFetchFiles = useCallback(
    debounce((term) => {
      fetchFiles(term);
    }, 500),
    []
  );

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchTerm(value);
    setFiles([]);
    setError('');

    if (value.trim() === '') {
      setSearching(false);
    } else {
      setSearching(true);
      debouncedFetchFiles(value);
    }
  };

  const handleSearchAll = () => {
    setSearchTerm('');
    setSearching(false);
    setFiles([]);
    setError('');
    fetchFiles('');
  };

  useEffect(() => {
    fetchQueries();
  }, []);

  return (
    <div className="search-container">
      <h2 className="search-title">Search Files</h2>

      {error && <div className="error">{error}</div>}

      <div className="search-bar">
        <input
          type="text"
          value={searchTerm}
          onChange={handleSearchChange}
          placeholder="Search for a file..."
          className="search-input"
        />
        <Button onClick={handleSearchAll} disabled={loading || searchTerm.trim() !== ''}>
          Search All
        </Button>
      </div>

      <div className="results">
        {loading && <div className="loading">Loading...</div>}

        {!loading && files.length > 0 && files.map((file) => (
          <FileCard key={file.id} file={file} onClick={() => onFileSelect(file)} />
        ))}

        {!loading && files.length === 0 && !searching && (
          <div>No files found.</div>
        )}
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
                <div className="query-title">
                  {query.fileName} - {query.filePath}
                </div>
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

export default FileSearch;
