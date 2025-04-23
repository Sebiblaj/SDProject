import React from 'react';
import './Button.css';

const Button = ({
  type = 'button',
  onClick,
  disabled = false,
  className = '',
  children,
}) => (
  <button
    type={type}
    onClick={onClick}
    disabled={disabled}
    className={`btn ${className}`}
  >
    {children}
  </button>
);

export default Button;