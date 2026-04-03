import React from 'react';
import { PRIORITY_COLORS, CATEGORY_COLORS } from '../config/constants';

const Badge = ({ label, bg, text }) => (
  <span style={{ background: bg, color: text, ...styles.badge }}>{label}</span>
);

const ResultPanel = ({ result }) => {
  if (!result) return null;

  const priority   = PRIORITY_COLORS[result.priority] || PRIORITY_COLORS.P3;
  const category   = CATEGORY_COLORS[result.category] || CATEGORY_COLORS.OTHER;
  const confidence = Math.round(result.confidenceScore * 100);

  return (
    <div style={styles.card}>
      <h2 style={styles.heading}>Analysis Result</h2>

      <div style={styles.badgeRow}>
        <Badge label={result.category} bg={category.bg} text={category.text} />
        <Badge label={priority.label}  bg={priority.bg}  text={priority.text} />
        {result.urgencyDetected && (
          <Badge label="⚠ Urgent" bg="#fef3c7" text="#92400e" />
        )}
      </div>

      <div style={styles.section}>
        <div style={styles.sectionLabel}>
          Confidence Score
          <span style={styles.confidenceValue}>{confidence}%</span>
        </div>
        <div style={styles.barTrack}>
          <div style={{
            ...styles.barFill,
            width: `${confidence}%`,
            background: confidence >= 70 ? '#22c55e'
                      : confidence >= 40 ? '#f59e0b'
                      : '#ef4444',
          }} />
        </div>
      </div>

      {result.keywords?.length > 0 && (
        <div style={styles.section}>
          <div style={styles.sectionLabel}>Matched Keywords</div>
          <div style={styles.chipRow}>
            {result.keywords.map((kw, i) => (
              <span key={i} style={styles.chip}>{kw}</span>
            ))}
          </div>
        </div>
      )}

      {result.signals?.length > 0 && (
        <div style={styles.section}>
          <div style={styles.sectionLabel}>Signals</div>
          <ul style={styles.signalList}>
            {result.signals.map((s, i) => (
              <li key={i} style={styles.signalItem}>
                <span style={styles.dot} />{s}
              </li>
            ))}
          </ul>
        </div>
      )}

      <p style={styles.ticketId}>Ticket ID: #{result.id}</p>
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
    borderLeft: '4px solid #4f46e5',
  },
  heading: { fontSize: '18px', fontWeight: '600', color: '#111827', marginBottom: '16px', marginTop: 0 },
  badgeRow: { display: 'flex', gap: '8px', flexWrap: 'wrap', marginBottom: '20px' },
  badge: { padding: '4px 12px', borderRadius: '999px', fontSize: '13px', fontWeight: '600' },
  section: { marginBottom: '16px' },
  sectionLabel: {
    fontSize: '13px', fontWeight: '600', color: '#6b7280',
    marginBottom: '8px', textTransform: 'uppercase',
    letterSpacing: '0.05em', display: 'flex', justifyContent: 'space-between',
  },
  confidenceValue: { color: '#111827', fontWeight: '700' },
  barTrack: { background: '#f3f4f6', borderRadius: '999px', height: '8px', overflow: 'hidden' },
  barFill:  { height: '100%', borderRadius: '999px', transition: 'width 0.5s ease' },
  chipRow:  { display: 'flex', flexWrap: 'wrap', gap: '6px' },
  chip: {
    background: '#f3f4f6', color: '#374151',
    padding: '3px 10px', borderRadius: '6px',
    fontSize: '12px', fontWeight: '500',
  },
  signalList: { margin: 0, padding: 0, listStyle: 'none' },
  signalItem: { display: 'flex', alignItems: 'flex-start', gap: '8px', fontSize: '13px', color: '#374151', marginBottom: '4px' },
  dot: { width: '6px', height: '6px', borderRadius: '50%', background: '#4f46e5', marginTop: '5px', flexShrink: 0 },
  ticketId: { fontSize: '12px', color: '#9ca3af', marginTop: '16px', marginBottom: 0 },
};

export default ResultPanel;