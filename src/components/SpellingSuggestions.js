const SpellingSuggestions = ({ suggestions, onSelect, onClose }) => {
    if (!suggestions) return null;
  
    return (
      <div className="spelling-popup">
        <div className="spelling-popup-content">
          <h4>Did you mean:</h4>
          <ul>
            {suggestions.map((suggestion, index) => (
              <li key={index}>
                <button onClick={() => onSelect(suggestion)}>{suggestion}</button>
              </li>
            ))}
          </ul>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
      </div>
    );
  };

  export default SpellingSuggestions;