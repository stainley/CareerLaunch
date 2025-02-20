// src/components/Login.tsx
import React, { useState } from 'react';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);

    const handleLogin = (e: React.FormEvent) => {
        e.preventDefault();
        try {
            // Redirect to OAuth2 authorize endpoint
            const authUrl = `http://localhost:8081/oauth2/authorize?response_type=code&client_id=job-tracker-client&redirect_uri=${encodeURIComponent('http://localhost:5173/callback')}&scope=openid%20read%20write`;
            window.location.href = authUrl;
        } catch (err) {
            setError('Failed to initiate login: ' + err);
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h1 style={styles.title}>Welcome Back</h1>
                <p style={styles.subtitle}>Please log in to your account</p>
                {error && <p style={styles.error}>{error}</p>}
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
                </form>
                <p style={styles.footerText}>
                    Don't have an account? <a href="/signup" style={styles.link}>Sign up</a>
                </p>
            </div>
        </div>
    );
};

// Styles for a modern, clean design
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
        fontWeight: '500',
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
};

export default Login;
