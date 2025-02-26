// src/components/Signup.tsx
import React, { useState } from 'react';
import axios from 'axios';
/*import QRCode from 'qrcode';*/
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useNavigate } from 'react-router-dom';

interface ApiError {
  message?: string;
  error?: string;

  [key: string]: unknown;
}

// Error handling utility function
const getErrorMessage = (error: unknown, defaultMessage: string): string => {
  if (axios.isAxiosError<ApiError>(error)) {
    return (
      error.response?.data?.message ||
      error.response?.data?.error ||
      error.message
    );
  }
  if (error instanceof Error) return error.message;
  return defaultMessage;
};

const Signup: React.FC = () => {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string | null>(null);
  /*const [qrCode, setQrCode] = useState<string>('');
    const [totpCode, setTotpCode] = useState<string>('');
    const [userId, setUserId] = useState<string>('');
    const [show2fa, setShow2fa] = useState<boolean>(false);*/

  const navigate = useNavigate();

  const URL_AUTH: string = 'http://localhost:8080';
  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      const response = await axios.post(
        `${URL_AUTH}/auth/signup`,
        { email, password },
        {
          headers: { 'Content-Type': 'application/json' },
          withCredentials: true,
        }
      );
      console.log('Signup response:', response.data);
      if (response.status === 201) {
        /* const qrCodeDataUrl = await QRCode.toDataURL(response.data.totpUri);
                setUserId(response.data.userId);
                setQrCode(qrCodeDataUrl);
                setShow2fa(true);
               */
        toast.success(
          'Registration successful! Please check your email for the activation link.',
          {
            onClose: () => navigate('/login'),
            autoClose: 3000,
          }
        );
      }
    } catch (err) {
      setError(
        `Signup failed: ${getErrorMessage(err, 'Unknown signup error')}`
      );
    }
  };

  /*const handleTotpVerification = async () => {
        setError(null);
        try {
            const response = await axios.post(
                `${URL_AUTH}/auth/verify-2fa`,
                {userId, code: totpCode},
                {
                    headers: {'Content-Type': 'application/json'},
                    withCredentials: true,
                }
            );
            console.log('2FA response:', response.data);
            if (response.status === 200 && response.data.startsWith("redirect:")) {
                window.location.href = 'http://localhost:5173/dashboard';
            }
        } catch (err) {
            setError(`2FA verification failed: ${getErrorMessage(err, "Unknown 2FA error")}`);
        }
    };*/

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.title}>Sign Up</h1>
        <p style={styles.subtitle}>Create your account</p>
        {error && <p style={styles.error}>{error}</p>}

        <form onSubmit={handleSignup} style={styles.form}>
          <div style={styles.inputGroup}>
            <label htmlFor="email" style={styles.label}>
              Email
            </label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              style={styles.input}
              placeholder="Enter your email"
            />
          </div>
          <div style={styles.inputGroup}>
            <label htmlFor="password" style={styles.label}>
              Password
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              style={styles.input}
              placeholder="Enter your password"
            />
          </div>
          <button type="submit" style={styles.button}>
            Sign Up
          </button>
        </form>

        <p style={styles.footerText}>
          Already have an account?{' '}
          <a href="/login" style={styles.link}>
            Log in
          </a>
        </p>
      </div>
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
      />
    </div>
  );
};

// Styles (reused from Login.tsx)
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
  title: {
    fontSize: '24px',
    fontWeight: 'bold',
    marginBottom: '8px',
    color: '#333',
  },
  subtitle: {
    fontSize: '16px',
    color: '#666',
    marginBottom: '24px',
  },
  form: {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: '16px',
  },
  inputGroup: {
    textAlign: 'left' as const,
  },
  label: {
    display: 'block',
    fontSize: '14px',
    fontWeight: 500,
    marginBottom: '8px',
    color: '#555',
  },
  input: {
    width: '100%',
    padding: '12px 16px',
    fontSize: '16px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    outline: 'none',
    transition: 'border-color 0.3s',
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
  error: {
    color: '#d9534f',
    fontSize: '14px',
    marginBottom: '16px',
  },
  footerText: {
    marginTop: '24px',
    fontSize: '14px',
    color: '#666',
  },
  link: {
    color: '#007bff',
    textDecoration: 'none',
  },
  qrContainer: {
    marginTop: '20px',
  },
  qrText: {
    fontSize: '14px',
    color: '#666',
    marginBottom: '10px',
  },
  qrImage: {
    width: '200px',
    height: '200px',
    marginBottom: '10px',
  },
};

export default Signup;
