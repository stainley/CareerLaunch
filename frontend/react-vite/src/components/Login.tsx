// src/components/Login.tsx
import React, {useState} from 'react';
import QRCode from "qrcode";
import axios from 'axios';
import {useNavigate} from "react-router-dom";

interface ApiError {
    message?: string;
    error?: string;

    [key: string]: unknown;
}

// Error handling utility function
const getErrorMessage = (error: unknown, defaultMessage: string): string => {
    if (axios.isAxiosError<ApiError>(error)) {
        return error.response?.data?.message ||
            error.response?.data?.error ||
            error.message;
    }
    if (error instanceof Error) return error.message;
    return defaultMessage;
};

const Login: React.FC = () => {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [error, setError] = useState<string | null>(null);
    const [qrCode, setQrCode] = useState<string>('');
    const [totpCode, setTotpCode] = useState<string>('');
    const [userId, setUserId] = useState<string>('');
    const [show2fa, setShow2fa] = useState<boolean>(false);

    const navigate = useNavigate();

    const URL_AUTH: string = "http://localhost:8080";

    const handleGoogleLogin = () => {
        window.location.href = 'http://localhost:8081/oauth2/authorize/google';
    };

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null); // Clear previous errors
        try {
            const response = await axios.post(
                `${URL_AUTH}/auth/login`,
                {username, password},
                {
                    headers: {'Content-Type': 'application/json'},
                    withCredentials: true,
                }
            );
            console.log('Login response:', response.data); // Debug log
            if (response.status === 200) {
                if (response.data.qrCode) {
                    // First-time login, show QR code for 2FA setup
                    QRCode.toDataURL(response.data.qrCode).then((r) => {
                        setQrCode(r);
                    });
                    setUserId(response.data.userId);

                    setShow2fa(true);
                } else if (response.data.status === '2fa_required') {
                    // 2FA already enabled, show TOTP input
                    setUserId(response.data.userId);
                    setShow2fa(true);
                }
            }
        } catch (err) {
            setError(`Login failed: ${getErrorMessage(err, 'Unknown authentication error')}`);
        }
    };

    const handleTotpVerification = async () => {
        setError(null);
        console.log('Sending 2FA verification:', {userId, code: totpCode});
        try {
            const response = await axios.post(
                `${URL_AUTH}/auth/verify-2fa`,
                {userId, code: totpCode},
                {
                    headers: {'Content-Type': 'application/json'},
                    withCredentials: true,
                }
            );
            console.log('Raw 2FA response:', response);
            console.log('Response data:', response.data);
            console.log('Response status:', response.status);
            if (response.status === 200) {
                const responseData = response.data;
                if (responseData.token) {
                    localStorage.setItem('access_token', responseData.token);
                    console.log('Navigating to dashboard');
                    navigate('/dashboard');
                } else {
                    setError('No token received');
                }
            }
        } catch (err) {
            setError(`2FA verification failed: ${getErrorMessage(err, 'Unknown verification error')}`);
            console.error('2FA error:', err);
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h1 style={styles.title}>Welcome Back</h1>
                <p style={styles.subtitle}>Please log in to your account</p>
                {error && <p style={styles.error}>{error}</p>}
                {show2fa ? (
                    <div style={styles.qrContainer}>
                        {qrCode && (
                            <>
                                <p style={styles.qrText}>
                                    Scan this QR code with your authenticator app (e.g., Google Authenticator):
                                </p>
                                <img src={qrCode} alt="QR Code" style={styles.qrImage}/>
                            </>
                        )}
                        <input
                            value={totpCode}
                            onChange={(e) => setTotpCode(e.target.value)}
                            placeholder="Enter 6-digit code"
                            style={styles.input}
                        />
                        <button onClick={handleTotpVerification} style={styles.button}>
                            Verify 2FA
                        </button>
                    </div>
                ) : (
                    <form onSubmit={handleLogin} style={styles.form}>
                        <div style={styles.inputGroup}>
                            <label htmlFor="username" style={styles.label}>
                                Username
                            </label>
                            <input
                                id="username"
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                                style={styles.input}
                                placeholder="Enter your username"
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
                            Log in
                        </button>
                        <p>or</p>
                        <button onClick={handleGoogleLogin} style={styles.googleButton}>
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="18"
                                height="18"
                                viewBox="0 0 488 512"
                                style={styles.googleLogo}
                            >
                                <path
                                    fill="#4285F4"
                                    d="M488 261.8C488 403.3 391.1 504 248 504 110.8 504 0 393.2 0 256S110.8 8 248 8c66.8 0 123 24.5 166.3 64.9l-67.5 64.9C258.5 52.6 94.3 116.6 94.3 256c0 86.5 69.1 156.6 153.7 156.6 98.2 0 135-70.4 140.8-106.9H248v-85.3h236.1c2.3 12.7 3.9 24.9 3.9 41.4z"
                                />
                            </svg>
                            <span style={styles.googleButtonText}>Sign in with Google</span>
                        </button>
                    </form>
                )}
                <p style={styles.footerText}>
                    Don't have an account? <a href="/signup" style={styles.link}>Sign up</a>
                </p>
            </div>
        </div>
    );
};

// Styles (unchanged)
const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        minWidth: '100vh',
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
    googleButton: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '10px 16px',
        fontSize: '16px',
        fontWeight: 500,
        color: '#000',
        backgroundColor: '#fff',
        border: '1px solid #ccc',
        borderRadius: '8px',
        cursor: 'pointer',
        transition: 'background-color 0.3s, border-color 0.3s',
        width: '100%',
        marginTop: '10px',
    },
    googleLogo: {
        marginRight: '10px',
    },
    googleButtonText: {
        color: '#000',
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

export default Login;