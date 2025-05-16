import React, { useState, useEffect, useCallback } from 'react';
import './FileSearch.css';
import { getAllFiles, searchFilesByName, getLatestLogsForFiles,
  getWidgets, getSpellingSuggestions, getWidgetsForFiles
 } from '../Requests/HTTPRequests';
import Button from './Reusable/Button';
import debounce from 'lodash.debounce';
import FileCard from '../components/Reusable/FileCard';
import SpellingSuggestions from './SpellingSuggestions';
import MetadataSummary from './MetadataSummary';

const IMAGE_EXTENSIONS = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg'];
const MIN_IMAGES_FOR_GALLERY = 1;

const CODE_FILE_EXTENSIONS = [
  'js',  'py', 'java', 'c', 'cpp', 'go','html', 'css', 'json', 'xml', 'yaml', 'yml', 'md', 'pl'
];
const MIN_CODE_FILES_FOR_EDITOR = 1; 

const FileSearch = ({ onFileSelect }) => {
  const [files, setFiles] = useState([]);
  const [queries, setQueries] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(false);
  const [searching, setSearching] = useState(false);
  const [widgets, setWidgets] = useState(null);
  const [firstSearched,setFirstSearched] = useState(true);
  const [spellingSuggestions,setSpellingSuggestions] = useState(null);

  const [imageFilesForGallery, setImageFilesForGallery] = useState([]);
  const [galleryHtml, setGalleryHtml] = useState(null);
  const [isGalleryViewActive, setIsGalleryViewActive] = useState(false);

  const [codeFilesForEditor, setCodeFilesForEditor] = useState([]);
  const [codeEditorHtml, setCodeEditorHtml] = useState(null);
  const [isCodeEditorViewActive, setIsCodeEditorViewActive] = useState(false);

  const isImageFile = (file) => {
    const extension = file.extension?.toLowerCase() || file.name?.split('.').pop()?.toLowerCase();
    return extension && IMAGE_EXTENSIONS.includes(extension);
  };

  const isCodeFile = (file) => {
    const extension = file.extension?.toLowerCase() || file.name?.split('.').pop()?.toLowerCase();
    return extension && CODE_FILE_EXTENSIONS.includes(extension);
  };

  const fetchFiles = (term) => {
    setLoading(true);
    setGalleryHtml(null);
    setIsGalleryViewActive(false);
    setImageFilesForGallery([]);
    setCodeEditorHtml(null);
    setIsCodeEditorViewActive(false);
    setCodeFilesForEditor([]);

    const request = term.trim() === '' ? getAllFiles() : searchFilesByName(term.trim());

    request
      .then((data) => {
        const resultFiles = term.trim() === '' ? data.data : data;
        setFiles(resultFiles);
        
        const currentImageFiles = resultFiles.filter(isImageFile);
        setImageFilesForGallery(currentImageFiles);

        const currentCodeFiles = resultFiles.filter(isCodeFile);
        setCodeFilesForEditor(currentCodeFiles);

      }).catch(() => {
        setFiles([]);
        setImageFilesForGallery([]);
        setCodeFilesForEditor([]);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const fetchWidgets = (term) => {
    if (!term || term.trim() === '') {
      setWidgets(null);
      return;
    }
    
    getWidgets(term.trim())
      .then(widgetResponse => {
        if(widgetResponse.status === 200 && widgetResponse.data) {
          setWidgets(widgetResponse.data);
        }else{
          setWidgets(null);
        }
      })
      .catch(err =>{
        setWidgets(null);
        console.log("Error");
      });
  };

  const fetchSpellingSuggestions = (term) => {
    if (!term || term.trim() === '') {
      setSpellingSuggestions(null);
      return;
    }

    getSpellingSuggestions(term.trim())
      .then(spellingResponse => {
        if(spellingResponse.status === 200 && spellingResponse.data && spellingResponse.data.length > 0){
          setSpellingSuggestions(spellingResponse.data);
        }else{
          setSpellingSuggestions(null);
        }
      })
      .catch(err =>{
        setSpellingSuggestions(null);
      });
  }

  const fetchQueries = () => {
    setLoading(true);
    getLatestLogsForFiles()
      .then((data) => {
        setQueries(data.data);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const debouncedFetch = useCallback(
    debounce((term) => {
      if (term && term.trim() !== '') {
        fetchFiles(term);
        fetchWidgets(term);
        fetchSpellingSuggestions(term);
        setFirstSearched(false);
      }
    }, 1000),
    []
  );

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchTerm(value);
    
    if (value.trim() === '') {
      setSearching(false);
      setFiles([]);
      setWidgets(null);
      setSpellingSuggestions(null);
      setImageFilesForGallery([]);
      setGalleryHtml(null);
      setIsGalleryViewActive(false);
      setCodeFilesForEditor([]);
      setCodeEditorHtml(null);
      setIsCodeEditorViewActive(false);
      return; 
    }
    
    setSearching(true);
    debouncedFetch(value);
  };

  const handleSearchAll = () => {
    setSearchTerm('');
    setSearching(false);
    setFiles([]);
    setWidgets(null);
    setSpellingSuggestions(null);
    setImageFilesForGallery([]);
    setGalleryHtml(null);
    setIsGalleryViewActive(false);
    setCodeFilesForEditor([]);
    setCodeEditorHtml(null);
    setIsCodeEditorViewActive(false);
    fetchFiles('');
  };

  const handleSuggestionSelect = (suggestion) => {
    setSearchTerm(suggestion);
    setSpellingSuggestions(null);
    setImageFilesForGallery([]);
    setGalleryHtml(null);
    setIsGalleryViewActive(false);
    setCodeFilesForEditor([]);
    setCodeEditorHtml(null);
    setIsCodeEditorViewActive(false);
    debouncedFetch(suggestion);
  };

  const handleCloseSuggestions = () => {
    setSpellingSuggestions(null);
  };

  useEffect(() => {
    fetchQueries();
  }, []);

  const handleViewGalleryClick = () => {
    if (imageFilesForGallery.length < MIN_IMAGES_FOR_GALLERY) return;

    setIsCodeEditorViewActive(false);
    setCodeEditorHtml(null);

    setLoading(true);

    const paths = imageFilesForGallery.map(file => file.path);
    const names = imageFilesForGallery.map(file => file.filename);
    const extensions = imageFilesForGallery.map(file => file.extension || file.name?.split('.').pop()?.toLowerCase());

    const criteria = {
      paths : paths,
      names : names,
      keywords : extensions
    }

    console.log("Criteria",criteria);

    getWidgetsForFiles("gallery", criteria)
      .then(response => {
        if (response && typeof response.data === 'string') {
          setGalleryHtml(response.data);
          setIsGalleryViewActive(true);
        } else {
          setGalleryHtml(null);
          setIsGalleryViewActive(false);
        }
      })
      .catch(err => {
        setGalleryHtml(null);
        setIsGalleryViewActive(false);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleCloseGalleryClick = () => {
    setIsGalleryViewActive(false);
    setGalleryHtml(null);
  };

  const handleViewCodeEditorClick = () => {
    if (codeFilesForEditor.length < MIN_CODE_FILES_FOR_EDITOR) return;

    setIsGalleryViewActive(false);
    setGalleryHtml(null);

    setLoading(true);

    const paths = codeFilesForEditor.map(file => file.path);
    const names = codeFilesForEditor.map(file => file.filename);
    const extensions = codeFilesForEditor.map(file => file.extension || file.name?.split('.').pop()?.toLowerCase());

    const criteria = {
      paths : paths,
      names : names,
      keywords : extensions
    };


    getWidgetsForFiles("code", criteria) 
      .then(response => {
        if (response && typeof response.data === 'string') {
          setCodeEditorHtml(response.data);
          setIsCodeEditorViewActive(true);
        } else {
          setCodeEditorHtml(null);
          setIsCodeEditorViewActive(false);
        }
      })
      .catch(err => {
        setCodeEditorHtml(null);
        setIsCodeEditorViewActive(false);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleCloseCodeEditorClick = () => {
    setIsCodeEditorViewActive(false);
    setCodeEditorHtml(null);
  };

  return (
    <div className="search-container">
      <h2 className="search-title">Search Files</h2>

      <div className="search-bar">
        <input
          type="text"
          value={searchTerm}
          onChange={handleSearchChange}
          placeholder="Search for a file or widget"
          className="search-input"
        />
        <Button onClick={handleSearchAll} disabled={loading || searchTerm.trim() !== ''}>
          Search All
        </Button>
        {spellingSuggestions && (
          <SpellingSuggestions
            suggestions={spellingSuggestions}
            onSelect={handleSuggestionSelect}
            onClose={handleCloseSuggestions}
          />
        )}
      </div>

      <div className="results">
        {loading && <div className="loading">Loading...</div>}

        {!loading && isGalleryViewActive && galleryHtml && !isCodeEditorViewActive && (
          <div className="gallery-view-container">
            <Button onClick={handleCloseGalleryClick} className="close-gallery-button">
              Close Gallery
            </Button>
            <div dangerouslySetInnerHTML={{ __html: galleryHtml }} />
          </div>
        )}

        {!loading && isCodeEditorViewActive && codeEditorHtml && !isGalleryViewActive && (
          <div className="code-editor-view-container">
            <Button onClick={handleCloseCodeEditorClick} className="close-code-editor-button">
              Close Code Editor
            </Button>
            <div dangerouslySetInnerHTML={{ __html: codeEditorHtml }} />
          </div>
        )}

        {!loading && !isGalleryViewActive && !isCodeEditorViewActive && (
          <>
            {imageFilesForGallery.length >= MIN_IMAGES_FOR_GALLERY && (
              <Button 
                onClick={handleViewGalleryClick} 
                className="view-gallery-button" 
                style={{ marginBottom: '10px', marginRight: '10px' }}
              >
                View as Gallery ({imageFilesForGallery.length} images)
              </Button>
            )}

            {codeFilesForEditor.length >= MIN_CODE_FILES_FOR_EDITOR && (
              <Button 
                onClick={handleViewCodeEditorClick} 
                className="view-code-editor-button" 
                style={{ marginBottom: '10px' }}
              >
                View in Code Editor ({codeFilesForEditor.length} files)
              </Button>
            )}

            {files.length > 0 && <MetadataSummary files={files} />}
            
            {files.length > 0 && (
              <div className="files-section">
                <h3>Matching Files</h3>
                {files.map((file) => (
                  <FileCard key={file.id} file={file} onClick={() => onFileSelect(file)} />
                ))}
              </div>
            )}

            {widgets !== null && (
              <div 
                className="widget-container"
                dangerouslySetInnerHTML={{ __html: widgets }}
              />
            )}

            {!firstSearched && files.length === 0 && widgets === null && !searching && files.length === 0 && (
              <div>No files or widgets found.</div>
            )}
          </>
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
