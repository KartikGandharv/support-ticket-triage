import React, { useState } from 'react';

const TicketForm = ({ onSubmit, loading }) => {
  const [message, setMessage] = useState('');
  const [error, setError]     = useState('');

  const handleSubmit = () => {
    if (message.trim().length < 10) {
      setError('Message must be at least 10 characters.');
      return;
    }
    setError('');
    onSubmit(message);
  };

  return (
    <div style={styles.card}>
      <h2 style={styles.heading}>Submit Support Ticket</h2>
      <textarea
        style={{ ...styles.textarea, borderColor: error ? '#ef4444' : '#d1d5db' }}
        rows={5}
        placeholder="Describe your issue in detail... (min 10 characters)"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        disabled={loading}
      />
      {error && <p style={styles.errorText}>{error}</p>}
      <div style={styles.footer}>
        <span style={styles.charCount}>{message.length} / 5000</span>
        <button
          style={{ ...styles.button, opacity: loading ? 0.7 : 1 }}
          onClick={handleSubmit}
          disabled={loading}
        >
          {loading ? 'Analyzing...' : 'Analyze Ticket'}
        </button>
      </div>
    </div>
  );
};

const styles = {
  card: {
    background: '#ffffff',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
    marginBottom: '24px',
  },
  heading: {
    fontSize: '18px',
    fontWeight: '600',
    color: '#111827',
    marginBottom: '16px',
    marginTop: 0,
  },
  textarea: {
    width: '100%',
    padding: '12px',
    fontSize: '14px',
    borderRadius: '8px',
    border: '1px solid #d1d5db',
    resize: 'vertical',
    outline: 'none',
    fontFamily: 'inherit',
    boxSizing: 'border-box',
    color: '#111827',
  },
  errorText: {
    color: '#ef4444',
    fontSize: '13px',
    marginTop: '6px',
  },
  footer: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '12px',
  },
  charCount: {
    fontSize: '12px',
    color: '#9ca3af',
  },
  button: {
    background: '#4f46e5',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    padding: '10px 24px',
    fontSize: '14px',
    fontWeight: '600',
    cursor: 'pointer',
  },
};

export default TicketForm;