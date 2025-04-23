import React from 'react';
import './FileCard.css';

const FileCard = ({ file, onClick }) => {
  const getIconForExtension = (extension) => {
    switch (extension.toLowerCase()) {
      case 'pdf': return '/icons/pdf.png';
      case 'jpg':
      case 'jpeg':
      case 'png':
      case 'gif': return '/icons/image.jfif';
      case 'doc':
      case 'docx': return '/icons/doc.jfif';
      case 'mp3':
      case 'wav': return '/icons/music.png';
      case 'mp4':
      case 'mov': return '/icons/video.png';
      case 'txt': return '/icons/txt.png';
      default: return '/icons/default.jfif';
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
