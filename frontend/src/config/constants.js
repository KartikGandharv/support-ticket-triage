export const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const PRIORITY_COLORS = {
  P0: { bg: '#fee2e2', text: '#991b1b', label: 'P0 — Critical' },
  P1: { bg: '#ffedd5', text: '#9a3412', label: 'P1 — High' },
  P2: { bg: '#fef9c3', text: '#854d0e', label: 'P2 — Medium' },
  P3: { bg: '#dcfce7', text: '#166534', label: 'P3 — Low' },
};

export const CATEGORY_COLORS = {
  BILLING:         { bg: '#ede9fe', text: '#5b21b6' },
  TECHNICAL:       { bg: '#dbeafe', text: '#1e40af' },
  ACCOUNT:         { bg: '#d1fae5', text: '#065f46' },
  FEATURE_REQUEST: { bg: '#fce7f3', text: '#9d174d' },
  SECURITY:        { bg: '#fee2e2', text: '#7f1d1d' },
  OTHER:           { bg: '#f3f4f6', text: '#374151' },
};