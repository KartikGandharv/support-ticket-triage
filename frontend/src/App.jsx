import React, { useState, useEffect, useCallback } from 'react';
import TicketForm from './components/TicketForm';
import ResultPanel from './components/ResultPanel';
import TicketsTable from './components/TicketsTable';
import { analyzeTicket, getAllTickets } from './services/ticketService';

const App = () => {
  const [result, setResult]           = useState(null);
  const [tickets, setTickets]         = useState([]);
  const [submitting, setSubmitting]   = useState(false);
  const [loadingList, setLoadingList] = useState(true);
  const [submitError, setSubmitError] = useState('');

  const fetchTickets = useCallback(async () => {
    try {
      const data = await getAllTickets();
      setTickets(data);
    } catch (err) {
      console.error('Failed to fetch tickets', err);
    } finally {
      setLoadingList(false);
    }
  }, []);

  useEffect(() => { fetchTickets(); }, [fetchTickets]);

  const handleSubmit = async (message) => {
    setSubmitting(true);
    setSubmitError('');
    setResult(null);
    try {
      const data = await analyzeTicket(message);
      setResult(data);
      await fetchTickets();
    } catch (err) {
      const msg = err.response?.data?.message
                || err.response?.data?.details?.[0]
                || 'Something went wrong. Please try again.';
      setSubmitError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={styles.page}>
      <header style={styles.header}>
        <div style={styles.headerInner}>
          <div style={styles.logo}>🎫</div>
          <div>
            <h1 style={styles.title}>Support Ticket Triage</h1>
            <p style={styles.subtitle}>AI-powered ticket classification and prioritization</p>
          </div>
        </div>
      </header>

      <main style={styles.main}>
        <TicketForm onSubmit={handleSubmit} loading={submitting} />

        {submitError && (
          <div style={styles.errorBanner}>
            <strong>Error:</strong> {submitError}
          </div>
        )}

        {result && <ResultPanel result={result} />}

        <TicketsTable tickets={tickets} loading={loadingList} />
      </main>
    </div>
  );
};

const styles = {
  page: {
    minHeight: '100vh',
    background: '#f1f5f9',
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
  },
  header: { background: '#4f46e5', padding: '20px 0', marginBottom: '32px' },
  headerInner: { maxWidth: '900px', margin: '0 auto', padding: '0 24px', display: 'flex', alignItems: 'center', gap: '16px' },
  logo:     { fontSize: '36px' },
  title:    { color: '#ffffff', margin: 0, fontSize: '22px', fontWeight: '700' },
  subtitle: { color: '#c7d2fe', margin: '4px 0 0', fontSize: '14px' },
  main:     { maxWidth: '900px', margin: '0 auto', padding: '0 24px 48px' },
  errorBanner: {
    background: '#fee2e2', color: '#991b1b',
    border: '1px solid #fecaca', borderRadius: '8px',
    padding: '12px 16px', fontSize: '14px', marginBottom: '16px',
  },
};

export default App;