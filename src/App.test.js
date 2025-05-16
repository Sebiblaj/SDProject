import { render, screen } from '@testing-library/react';
import App from './App';

describe('App component', () => {
  test('renders main header and description', () => {
    render(<App />);
    expect(screen.getByText(/local file search engine/i)).toBeInTheDocument();
    expect(screen.getByText(/search and manage your local files/i)).toBeInTheDocument();
  });

  test('renders all toolbar buttons', () => {
    render(<App />);
    const buttons = [
      /search files/i,
      /keyword search/i,
      /upload files/i,
      /index report/i,
      /query logger/i,
    ];
    buttons.forEach(text => {
      expect(screen.getByText(text)).toBeInTheDocument();
    });
  });
});
