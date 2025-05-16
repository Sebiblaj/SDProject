import React from 'react';
import './MetadataSummary.css';

const MetadataSummary = ({ files }) => {
  const calculateMetadata = () => {
    const metadata = {
      fileTypes: {},
      modifiedYears: {},
      createdYears: {},
      languages: {},
      permissions: {
        'Readable': 0,
        'Writable': 0,
        'Executable': 0
      },
      sizes: {
        'Small (<1MB)': 0,
        'Medium (1-10MB)': 0,
        'Large (>10MB)': 0
      }
    };

    metadata.createdYears[2024] = 0;

    files.forEach(file => {
      const fileType = file.extension;
      metadata.fileTypes[fileType] = (metadata.fileTypes[fileType] || 0) + 1;

      const modifiedYear = new Date(file.lastmodified).getFullYear();
      if (!isNaN(modifiedYear)) {
        metadata.modifiedYears[modifiedYear] = (metadata.modifiedYears[modifiedYear] || 0) + 1;
      }

      const createdYear = new Date(file.creationtime).getFullYear();
      if (!isNaN(createdYear)) {
        metadata.createdYears[createdYear] = (metadata.createdYears[createdYear] || 0) + 1;
      }else{
        metadata.createdYears[2025] = (metadata.createdYears[2025] || 0) + 1;
      }
    

      if (file.permissions) {
        if (file.permissions.readable) metadata.permissions['Readable']++;
        if (file.permissions.writable) metadata.permissions['Writable']++;
        if (file.permissions.executable) metadata.permissions['Executable']++;
      } else {
        metadata.permissions['Readable']++;
      }

      const languageMap = {
        'js': 'JavaScript',
        'ts': 'TypeScript',
        'py': 'Python',
        'java': 'Java',
        'cpp': 'C++',
        'c': 'C',
        'cs': 'C#',
        'php': 'PHP',
        'rb': 'Ruby',
        'go': 'Go',
        'rs': 'Rust',
        'swift': 'Swift',
        'kt': 'Kotlin',
        'html': 'HTML',
        'css': 'CSS',
        'sql': 'SQL',
        'sh': 'Shell',
        'bat': 'Batch',
        'ps1': 'PowerShell',
        'md': 'Markdown',
        'json': 'JSON',
        'xml': 'XML',
        'yaml': 'YAML',
        'yml': 'YAML',
        'log': 'Log',
        'ini': 'INI',
        'cfg': 'Config',
        'conf': 'Config',
        'cfg': 'Config',
        'cfg': 'Config',
        'pl': 'Perl',
      };
      
      const extension = file.extension?.toLowerCase() || file.name?.split('.').pop()?.toLowerCase();
      const language = languageMap[extension] || 'Other';
      metadata.languages[language] = (metadata.languages[language] || 0) + 1;

      const size = file.size || 0;
      if (size < 1024 * 1024) { 
        metadata.sizes['Small (<1MB)']++;
      } else if (size < 10 * 1024 * 1024) { 
        metadata.sizes['Medium (1-10MB)']++;
      } else { 
        metadata.sizes['Large (>10MB)']++;
      }
    });

    return metadata;
  };

  const renderMetadataSection = (title, data) => {
    const sortedEntries = Object.entries(data)
      .sort(([, a], [, b]) => b - a); 

    if (sortedEntries.length === 0) return null;

    return (
      <div className="metadata-section">
        <h4>{title}</h4>
        <div className="metadata-items">
          {sortedEntries.map(([key, count]) => (
            <div key={key} className="metadata-item">
              <span className="metadata-label">{key}</span>
              <span className="metadata-count">{count}</span>
            </div>
          ))}
        </div>
      </div>
    );
  };

  const metadata = calculateMetadata();

  return (
    <div className="metadata-summary">
      <h3>File Statistics</h3>
      <div className="metadata-grid">
        {renderMetadataSection('File Types', metadata.fileTypes)}
        {renderMetadataSection('Created Year', metadata.createdYears)}
        {renderMetadataSection('Modified Year', metadata.modifiedYears)}
        {renderMetadataSection('Languages', metadata.languages)}
        {renderMetadataSection('Permissions', metadata.permissions)}
        {renderMetadataSection('File Sizes', metadata.sizes)}
      </div>
    </div>
  );
};

export default MetadataSummary; 