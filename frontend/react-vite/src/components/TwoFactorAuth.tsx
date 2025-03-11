import React, { useRef, useState } from 'react';
import axios from 'axios';
import { Typography } from '@mui/material';

interface TwoFactorAuthProps {
  userId: string;
  onVerify: (token: string) => void;
}

const TwoFactorAuth: React.FC<TwoFactorAuthProps> = ({ userId, onVerify }) => {

  const [totpCode, setTotpCode] = useState<string[]>(['', '', '', '', '', '']);
  const [error, setError] = useState<string | null>(null);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  const handleChange = (index: number, value: string) => {
    if (!/^[0-9]?$/.test(value)) return; // Allow only digits
    const newCode = [...totpCode];
    newCode[index] = value;
    setTotpCode(newCode);

    if (value && index < 5) {
      inputRefs.current[index + 1]?.focus(); // Move to the next input
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !totpCode[index] && index > 0) {
      inputRefs.current[index - 1]?.focus(); // Move back on delete
    }
  };

  const handleSubmit = async () => {
    setError(null);
    const code = totpCode.join('');
    if (code.length !== 6) {
      setError('Please enter a 6-digit code');
      return;
    }

    try {
      const response = await axios.post(`/auth/verify-2fa`, { userId, code },
        { headers: { 'Content-Type': 'application/json' }, withCredentials: true },
      );

      if (response.status === 200 && response.data.token) {
        onVerify(response.data.token);
      } else {
        setError('Invalid verification code');
      }
    } catch (err) {
      setError(`2FA verification failed ${err}`);
    }
  };

  return (
    <div>
      <Typography variant="h5" color="textPrimary" mb="1.2rem">
        Enter 2FA Code
      </Typography>

      {error && <p style={{ color: 'red' }}>{error}</p>}
      <div style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
        {totpCode.map((digit, index) => (
          <input
            key={index}
            type="text"
            maxLength={1}
            value={digit}
            onChange={(e) => handleChange(index, e.target.value)}
            onKeyDown={(e) => handleKeyDown(index, e)}
            ref={(el) => {
              inputRefs.current[index] = el;
            }}
            style={{ width: '40px', height: '40px', textAlign: 'center', fontSize: '20px' }}
          />
        ))}
      </div>
      <button
        onClick={handleSubmit}
        style={{
          marginTop: '20px',
          padding: '10px 20px',
          fontSize: '16px',
          backgroundColor: '#007bff',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
        }}
      >
        Verify
      </button>
    </div>
  );
};

export default TwoFactorAuth;