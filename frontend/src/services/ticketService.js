import axios from 'axios';
import { API_BASE_URL } from '../config/constants';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

export const analyzeTicket = async (message) => {
  const response = await api.post('/tickets/analyze', { message });
  return response.data;
};

export const getAllTickets = async () => {
  const response = await api.get('/tickets');
  return response.data;
};