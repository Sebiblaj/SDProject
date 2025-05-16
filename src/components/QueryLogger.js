import React, { useState, useEffect } from 'react';
import './QueryLogger.css'

function QueryLogger() {
  const [fileContent, setFileContent] = useState('');

  const handleFileRead = () => {
    const fileUrl = 'http://localhost:8081/reports/SystemLogger.txt';
  
    fetch(fileUrl)
      .then(response => {
        return response.text(); 
      })
      .then(data => {
        setFileContent(data);
      })
      .catch(error => {
        setFileContent(`Error loading file: ${error.message}`);
      });
  };
  

  useEffect(() => {
    handleFileRead();
  }, []); 

  return (
    <div className="index-report-container">
      <h2>📑 Query Report</h2>

      <button onClick={handleFileRead}>📥 Reload Report</button>

      <div>
        <h3>File Contents:</h3>
        <pre>{fileContent}</pre>
      </div>
    </div>
  );
}

export default QueryLogger;
