import React, { useState, useEffect } from 'react';
import { getMetadataForFile, getFileContentsByPathAndName, getTagsForFile } from '../../Requests/HTTPRequests';
import './FileDetails.css';

const FileDetails = ({ file, onClose }) => {
  const [metadata, setMetadata] = useState({});
  const [tags, setTags] = useState([]);
  const [fileContents, setFileContents] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [imageData, setImageData] = useState(null);

  useEffect(() => {
    const handleEscKey = (event) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    window.addEventListener('keydown', handleEscKey);
    return () => {
      window.removeEventListener('keydown', handleEscKey);
    };
  }, [onClose]);

  useEffect(() => {
    const fetchMetadata = async () => {
      try {
        setLoading(true);
        const decodedPath = decodeURIComponent(file.path);
        const decodedFilename = decodeURIComponent(file.filename);
        const decodedExtension = decodeURIComponent(file.extension);

        const metadataResponse = await getMetadataForFile(decodedPath, decodedFilename, decodedExtension);
        const tagsResponse = await getTagsForFile(decodedPath, decodedFilename);
        const contentsResponse = await getFileContentsByPathAndName(decodedPath, decodedFilename, decodedExtension);

        setMetadata(metadataResponse.data.metadata || {});
        setTags(tagsResponse.data ? tagsResponse.data.map(t => t.tag) : []);
        
        const content = contentsResponse.data || '';
        if (content.includes('--- BASE64 IMAGE CONTENT ---')) {
          const base64Data = content.split('--- BASE64 IMAGE CONTENT ---')[1].trim();
          setImageData(base64Data);
          setFileContents(content.split('--- BASE64 IMAGE CONTENT ---')[0].trim());
        } else {
          setFileContents(content);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchMetadata();
  }, [file]);

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  const displayFields = {
    "Filename": metadata.filename,
    "File Type": metadata.extension,
    "Size": metadata.filesize ? `${metadata.filesize} bytes` : undefined,
    "Readable": metadata.readable === 'true' ? 'Yes' : 'No',
    "Writable": metadata.writable === 'true' ? 'Yes' : 'No',
    "Executable": metadata.executable === 'true' ? 'Yes' : 'No',
    "Created": metadata.creationtime,
    "Last Modified": metadata.lastmodified,
    "Last Accessed": metadata.lastaccess,
    "Full Path": metadata.fullpath,
    "File Hash": metadata.filehash,
  };

  const isImageFile = ['jpg', 'jpeg', 'png', 'bmp', 'gif'].includes(file.extension?.toLowerCase());

  return (
    <div className="file-details-container">
      <button className="close-button" onClick={onClose}>×</button>
      <div className="metadata-panel">
        <h2>Properties</h2>
        <table className="metadata-table">
          <tbody>
            {Object.entries(displayFields).map(([label, value]) => (
              value && (
                <tr key={label}>
                  <td className="metadata-label">{label}</td>
                  <td className="metadata-value">{value}</td>
                </tr>
              )
            ))}
          </tbody>
        </table>

        {tags.length > 0 && (
          <div className="file-tags">
            <h3>Tags</h3>
            <div className="tag-list">
              {tags.map((tag, index) => (
                <span key={index} className="tag">{tag}</span>
              ))}
            </div>
          </div>
        )}
      </div>

      <div className="content-panel">
        <h3>Contents</h3>
        <div className="file-contents">
          {isImageFile && imageData ? (
            <div className="image-container">
              <img 
                src={`data:image/${file.extension.toLowerCase()};base64,${imageData}`}
                alt={file.filename}
                className="preview-image"
              />
              <pre className="image-metadata">{fileContents}</pre>
            </div>
          ) : (
            <pre>{fileContents}</pre>
          )}
        </div>
      </div>
    </div>
  );
};

export default FileDetails;
