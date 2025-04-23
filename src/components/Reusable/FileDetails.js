import React, { useState, useEffect } from 'react';
import { getMetadataForFile, getFileContentsByPathAndName, getTagsForFile } from '../../Requests/HTTPRequests';

const FileDetails = ({ file }) => {
  const [metadata, setMetadata] = useState({});
  const [tags, setTags] = useState([]);
  const [fileContents, setFileContents] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMetadata = async () => {
      try {
        setLoading(true);
        const decodedPath = decodeURIComponent(file.path);
        const decodedFilename = decodeURIComponent(file.filename);

        const metadataResponse = await getMetadataForFile(decodedPath, decodedFilename);
        const tagsResponse = await getTagsForFile(decodedPath, decodedFilename);
        const contentsResponse = await getFileContentsByPathAndName(decodedPath, decodedFilename);

        console.log("Metadata is ",metadataResponse)

        setMetadata(metadataResponse.data.metadata || {});
        setTags(tagsResponse.data ? tagsResponse.data.map(t => t.tag) : []);
        setFileContents(contentsResponse.data || '');
      } catch (err) {
        console.error(err);
        setError('Error fetching file details');
      } finally {
        setLoading(false);
      }
    };

    fetchMetadata();
  }, [file]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

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

  return (
    <div className="file-details-container">
      <h2>File Properties</h2>

      <table className="metadata-table">
        <tbody>
          {Object.entries(displayFields).map(([label, value]) => (
            value && (
              <tr key={label}>
                <td className="metadata-label"><strong>{label}:</strong></td>
                <td className="metadata-value">{value}</td>
              </tr>
            )
          ))}
        </tbody>
      </table>

      {tags.length > 0 && (
        <div className="file-tags">
          <h3>Tags:</h3>
          <p>{tags.join(', ')}</p>
        </div>
      )}

      {fileContents && (
        <div className="file-contents">
          <h3>File Contents:</h3>
          <pre>{fileContents}</pre>
        </div>
      )}
    </div>
  );
};

export default FileDetails;
