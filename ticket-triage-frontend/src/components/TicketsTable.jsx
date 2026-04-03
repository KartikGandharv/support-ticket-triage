import React from 'react';
import { PRIORITY_COLORS, CATEGORY_COLORS } from '../config/constants';

const Badge = ({ label, bg, text }) => (
  <span style={{ background: bg, color: text, ...styles.badge }}>{label}</span>
);

const TicketsTable = ({ tickets, loading }) => {
  if (loading) return <div style={styles.centered}>Loading tickets...</div>;

  if (!tickets || tickets.length === 0) {
    return (
      <div style={styles.emptyCard}>
        <p style={styles.emptyText}>No tickets yet. Submit one above!</p>
      </div>
    );
  }

  return (
    <div style={styles.card}>
      <h2 style={styles.heading}>
        Recent Tickets
        <span style={styles.count}>{tickets.length}</span>
      </h2>
      <div style={styles.tableWrapper}>
        <table style={styles.table}>
          <thead>
            <tr>
              {['ID','Message','Category','Priority','Urgency','Confidence','Created'].map(h => (
                <th key={h} style={styles.th}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {tickets.map((ticket, idx) => {
              const priority   = PRIORITY_COLORS[ticket.priority] || PRIORITY_COLORS.P3;
              const category   = CATEGORY_COLORS[ticket.category] || CATEGORY_COLORS.OTHER;
              const confidence = Math.round(ticket.confidenceScore * 100);
              const date       = new Date(ticket.createdAt).toLocaleString();

              return (
                <tr key={ticket.id} style={{ ...styles.tr, background: idx % 2 === 0 ? '#ffffff' : '#f9fafb' }}>
                  <td style={styles.td}><span style={styles.idBadge}>#{ticket.id}</span></td>
                  <td style={{ ...styles.td, maxWidth: '260px' }}>
                    <span style={styles.messageText} title={ticket.message}>
                      {ticket.message.length > 60 ? ticket.message.substring(0, 60) + '...' : ticket.message}
                    </span>
                  </td>
                  <td style={styles.td}>
                    <Badge label={ticket.category} bg={category.bg} text={category.text} />
                  </td>
                  <td style={styles.td}>
                    <Badge label={ticket.priority} bg={priority.bg} text={priority.text} />
                  </td>
                  <td style={styles.td}>
                    {ticket.urgencyDetected
                      ? <span style={styles.urgentYes}>⚠ Yes</span>
                      : <span style={styles.urgentNo}>No</span>}
                  </td>
                  <td style={styles.td}>
                    <div style={styles.confRow}>
                      <div style={styles.miniBarTrack}>
                        <div style={{
                          ...styles.miniBarFill,
                          width: `${confidence}%`,
                          background: confidence >= 70 ? '#22c55e' : confidence >= 40 ? '#f59e0b' : '#ef4444',
                        }} />
                      </div>
                      <span style={styles.confText}>{confidence}%</span>
                    </div>
                  </td>
                  <td style={{ ...styles.td, whiteSpace: 'nowrap', fontSize: '12px', color: '#6b7280' }}>
                    {date}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

const styles = {
  card: { background: '#ffffff', borderRadius: '12px', padding: '24px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' },
  heading: { fontSize: '18px', fontWeight: '600', color: '#111827', marginBottom: '16px', marginTop: 0, display: 'flex', alignItems: 'center', gap: '10px' },
  count: { background: '#e0e7ff', color: '#3730a3', fontSize: '13px', fontWeight: '700', padding: '2px 8px', borderRadius: '999px' },
  tableWrapper: { overflowX: 'auto' },
  table: { width: '100%', borderCollapse: 'collapse', fontSize: '14px' },
  th: { textAlign: 'left', padding: '10px 12px', fontSize: '12px', fontWeight: '600', color: '#6b7280', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '2px solid #f3f4f6', whiteSpace: 'nowrap' },
  tr: { transition: 'background 0.15s' },
  td: { padding: '12px', borderBottom: '1px solid #f3f4f6', color: '#111827', verticalAlign: 'middle' },
  badge: { padding: '3px 10px', borderRadius: '999px', fontSize: '12px', fontWeight: '600', whiteSpace: 'nowrap' },
  idBadge: { color: '#6b7280', fontWeight: '600', fontSize: '13px' },
  messageText: { color: '#374151', lineHeight: '1.4' },
  urgentYes: { color: '#b45309', fontWeight: '600', fontSize: '13px' },
  urgentNo:  { color: '#9ca3af', fontSize: '13px' },
  confRow:   { display: 'flex', alignItems: 'center', gap: '8px' },
  miniBarTrack: { background: '#f3f4f6', borderRadius: '999px', height: '6px', width: '60px', overflow: 'hidden' },
  miniBarFill:  { height: '100%', borderRadius: '999px' },
  confText:  { fontSize: '12px', color: '#374151', fontWeight: '600' },
  emptyCard: { background: '#ffffff', borderRadius: '12px', padding: '40px 24px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', textAlign: 'center' },
  emptyText: { color: '#9ca3af', fontSize: '14px' },
  centered:  { textAlign: 'center', padding: '40px', color: '#9ca3af' },
};

export default TicketsTable;