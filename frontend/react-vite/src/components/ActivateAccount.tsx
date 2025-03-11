import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios, { AxiosError } from 'axios';

interface ApiErrorResponse {
  message?: string;
  error?: string;

  [key: string]: unknown;
}

const ActivateAccount: React.FC = () => {
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>(
    'loading',
  );
  const [countdown, setCountdown] = useState(10);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token');
  const URL_AUTH = 'http://localhost:8080';

  useEffect(() => {
    const activateAccount = async () => {
      if (!token) {
        setStatus('error');
        return;
      }
      try {
        const response = await axios.get(
          `${URL_AUTH}/users/activate?token=${token}`,
        );
        if (response.status === 200) {
          setStatus('success');
          startCountdown();
        }
      } catch (error) {
        setStatus('error');

        if (axios.isAxiosError(error)) {
          const err = error as AxiosError<ApiErrorResponse>;
          console.error('Activation failed:', err.response?.data?.message || err.message);
        } else if (error instanceof Error) {
          console.error('Activation failed:', error.message);
        } else {
          console.error('Activation failed:', 'Unknown error occurred');
        }
      }
    };
    activateAccount().finally(() => setStatus('loading'));
  }, [token]);

  const startCountdown = () => {
    const interval = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          navigate('/login');
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        {status === 'loading' && (
          <div style={styles.message}>
            <h2>Activating Your Account</h2>
            <p>Please wait while we process your activation...</p>
          </div>
        )}
        {status === 'success' && (
          <div style={styles.message}>
            <h2 style={styles.successTitle}>Account Activated!</h2>
            <p style={styles.successText}>
              Congratulations! Your CareerLaunch account has been successfully
              activated.
            </p>
            <p style={styles.countdown}>
              Redirecting to login in {countdown} seconds...
            </p>
          </div>
        )}
        {status === 'error' && (
          <div style={styles.message}>
            <h2 style={styles.errorTitle}>Activation Failed</h2>
            <p style={styles.errorText}>
              There was an issue activating your account. The token may be
              invalid or expired. Please try registering again or contact
              support.
            </p>
            <button style={styles.button} onClick={() => navigate('/signup')}>
              Register Again
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    backgroundColor: '#f0f2f5',
    padding: '20px',
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
    padding: '40px',
    maxWidth: '400px',
    width: '100%',
    textAlign: 'center' as const,
  },
  message: {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: '16px',
  },
  successTitle: {
    fontSize: '24px',
    fontWeight: 'bold',
    color: '#28a745', // Green for success
  },
  successText: {
    fontSize: '16px',
    color: '#333',
  },
  countdown: {
    fontSize: '14px',
    color: '#666',
  },
  errorTitle: {
    fontSize: '24px',
    fontWeight: 'bold',
    color: '#dc3545', // Red for error
  },
  errorText: {
    fontSize: '16px',
    color: '#333',
  },
  button: {
    padding: '12px',
    fontSize: '16px',
    fontWeight: 'bold',
    color: '#fff',
    backgroundColor: '#007bff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    transition: 'background-color 0.3s',
  },
};

export default ActivateAccount;
