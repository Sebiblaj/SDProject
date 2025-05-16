import React from 'react';
import './FileCard.css';

const FileCard = ({ file, onClick }) => {
  const getIconForExtension = (extension) => {
    switch (extension.toLowerCase()) {
      case 'pdf': return '/icons/pdf.png';
      case 'c' : return '/icons/C.png';
      case 'java' : return '/icons/java.png'
      case 'jpg': return '/icons/image.png'
      case 'jpeg': return '/icons/image.png'
      case 'png': return '/icons/image.png'
      case 'bmp': return '/icons/image.png'
      case 'gif': return '/icons/image.png';
      case 'doc': return 'icons/doc.png'
      case 'docx': return 'icons/doc.png'
      case 'docx': return '/icons/doc.png';
      case 'mov': return '/icons/video.png';
      case 'txt': return '/icons/txt.png';
      default: return '/icons/default.png';
    }
  };

  return (
    <div className="file-card" onClick={()=>onClick()}>
      <div className="file-icon">
        <img
          src={getIconForExtension(file.extension)}
          alt={file.extension}
          className="file-icon-img"
        />
      </div>
      <div>
        <h3 className="file-name">{file.filename}</h3>
        <p className="file-path">📂 {file.path}</p>
      </div>
    </div>
  );
};

export default FileCard;
