import React, { useState } from 'react';
import axios from 'axios';

function IndexReport() {
  const [format, setFormat] = useState('txt');
  const [fileContent, setFileContent] = useState('');

  const filePaths = {
    txt: 'SystemIndex.txt',
    md: 'file_index_log.md',
    json: 'file_index_log.json',
    csv: 'file_index_log.csv',
  };

  const handleFormatChange = (e) => {
    setFormat(e.target.value);
  };

  const handleFileRead = () => {
    const fileName = filePaths[format];
    const fileUrl = `http://localhost:8081/reports/${fileName}`;

    axios.get(fileUrl)
      .then(response => {
        setFileContent(response.data);
      })
      .catch(error => {
        setFileContent(`Error loading file: ${error.message}`);
      });
  };

  const renderCSVTable = () => {
    const rows = fileContent.split('\n').filter(row => row.trim() !== '');
    if (rows.length === 0) return <p>No data available.</p>;

    const headers = rows[0].split(',');
    const dataRows = rows.slice(1);

    return (
      <table border="1" cellPadding="6" cellSpacing="0" style={{ borderCollapse: 'collapse', width: '100%' }}>
        <thead>
          <tr>
            {headers.map((header, i) => (
              <th key={i}>{header.trim()}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {dataRows.map((row, i) => {
            const cells = row.split(',');
            while (cells.length < headers.length) {
              cells.push('');
            }
            return (
              <tr key={i}>
                {cells.map((cell, j) => (
                  <td key={j}>{cell.trim()}</td>
                ))}
              </tr>
            );
          })}
        </tbody>
      </table>
    );
  };

  return (
    <div className="index-report-container">
      <h2>📑 Index Report</h2>

      <label>Select report format: </label>
      <select value={format} onChange={handleFormatChange}>
        <option value="txt">TXT</option>
        <option value="md">Markdown (MD)</option>
        <option value="json">JSON</option>
        <option value="csv">CSV</option>
      </select>

      <button onClick={handleFileRead}>📥 Load Report</button>

      <div>
        <h3>File Contents:</h3>
        {format === 'csv' ? renderCSVTable() : <pre>{fileContent}</pre>}
      </div>
    </div>
  );
}

export default IndexReport;
