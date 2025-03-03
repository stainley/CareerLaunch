import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Dashboard from '@components/Dashboard/Dashboard';
import { MemoryRouter } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { expect, vi, it } from 'vitest';
import axios from 'axios'; // Import the default export
import { useMediaQuery } from '@mui/material';

// Mock React Router
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom');
  return {
    ...actual,
    useNavigate: vi.fn(),
  };
});

// Mock Axios with default export
vi.mock('axios', async () => {
  const actualAxios = await vi.importActual<typeof import('axios')>('axios');
  return {
    ...actualAxios,
    default: {
      ...actualAxios.default,
      get: vi.fn(),
      put: vi.fn(),
    },
  };
});

// Mock Material-UI's useMediaQuery
vi.mock('@mui/material', async () => {
  const actual = await vi.importActual<typeof import('@mui/material')>('@mui/material');
  return {
    ...actual,
    useMediaQuery: vi.fn(),
  };
});

describe('Dashboard Component', () => {
  beforeEach(() => {
    vi.mocked(useMediaQuery).mockReturnValue(true); // Force large screen
    localStorage.setItem('access_token', 'mock-token');
  });

  afterEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('should renders welcome message after loading', async () => {
    // Mock axios.get to resolve with mock user data
    vi.mocked(axios.get).mockResolvedValueOnce({
      data: { username: 'testuser', email: 'test@example.com' },
    });

    render(
      <MemoryRouter>
        <Dashboard />
      </MemoryRouter>,
    );

    // Wait for loading to finish and welcome message to appear
    await waitFor(() => {
      expect(screen.getByText(/Welcome, testuser/i)).toBeInTheDocument();
    });

    // Verify loading state is gone
    expect(screen.queryByText('Loading...')).not.toBeInTheDocument();
  });

  it('should logs out and redirects on logout click', async () => {
    vi.mocked(axios.get).mockResolvedValueOnce({ data: { username: 'testuser' } });
    const navigate = vi.fn();
    vi.mocked(useNavigate).mockImplementation(() => navigate);

    render(
      <MemoryRouter>
        <Dashboard />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getByText(/Welcome, testuser/i)).toBeInTheDocument();
    });

    // Target the Logout button (only one exists now)
    const logoutButton = screen.getByTestId('drawer-item-logout');
    fireEvent.click(logoutButton);

    expect(localStorage.getItem('access_token')).toBeNull();
    expect(navigate).toHaveBeenCalledWith('/', { replace: true });
  });
});