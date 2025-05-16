import React, { useState } from 'react';
import FileSearch from './components/FileSearch';
import UploadFile from './components/UploadFile'; 
import IndexReport from './components/IndexReport'; 
import QueryLogger from './components/QueryLogger'; 
import KeywordSearch from './components/KeywordSearch';
import FileDetails from './components/Reusable/FileDetails'; 
import './App.css';

function App() {
  const [activeTool, setActiveTool] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);  
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleToolSelect = (tool) => {
    setActiveTool(prev => (prev === tool ? null : tool));
    setSelectedFile(null);
    setIsModalOpen(false); 
  };

  const handleFileSelect = (file) => {
    setSelectedFile(file);
    setIsModalOpen(true); 
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedFile(null);
  };

  return (
    <div className="app-container">
      <header>
        <h1>📂 Local File Search Engine</h1>
        <p>Search and manage your local files with ease.</p>
      </header>

      <nav className="toolbar">
        <button 
          className={activeTool === 'fileSearch' ? 'active' : ''}
          onClick={() => handleToolSelect('fileSearch')}
        >
          🔍 Search Files
        </button>
        <button 
          className={activeTool === 'keywordSearch' ? 'active' : ''} 
          onClick={() => handleToolSelect('keywordSearch')}
        >
          🔑 Keyword Search
        </button>
        <button 
          className={activeTool === 'uploadFiles' ? 'active' : ''}
          onClick={() => handleToolSelect('uploadFiles')}
        >
          📤 Upload Files
        </button>
        <button 
          className={activeTool === 'indexReport' ? 'active' : ''}
          onClick={() => handleToolSelect('indexReport')}
        >
          🛠️ Index Report
        </button>
        <button 
          className={activeTool === 'queryLogger' ? 'active' : ''} 
          onClick={() => handleToolSelect('queryLogger')}
        >
          📜 Query Logger
        </button>
      </nav>

      <main>
        <div className={`tool-section ${activeTool === 'fileSearch' ? 'open' : ''}`}>
          {activeTool === 'fileSearch' && <FileSearch onFileSelect={handleFileSelect} />}
        </div>
        <div className={`tool-section ${activeTool === 'keywordSearch' ? 'open' : ''}`}>
          {activeTool === 'keywordSearch' && <KeywordSearch />}
        </div>
        <div className={`tool-section ${activeTool === 'uploadFiles' ? 'open' : ''}`}>
          {activeTool === 'uploadFiles' && <UploadFile />}
        </div>
        <div className={`tool-section ${activeTool === 'indexReport' ? 'open' : ''}`}>
          {activeTool === 'indexReport' && <IndexReport />}
        </div>
        <div className={`tool-section ${activeTool === 'queryLogger' ? 'open' : ''}`}>
          {activeTool === 'queryLogger' && <QueryLogger />}
        </div>

        {isModalOpen && selectedFile && (
          <div className="file-details-overlay open" onClick={closeModal}>
            <div className="file-details-modal" onClick={(e) => e.stopPropagation()}>
              <FileDetails file={selectedFile} onClose={closeModal} />
            </div>
          </div>
        )}
      </main>

      <footer>
        <small>© 2025 Local Search Engine | Built with React + Spring</small>
      </footer>
    </div>
  );
}

export default App;
