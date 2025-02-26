// src/components/Callback.tsx
import { useEffect } from 'react';
import axios, { AxiosError } from 'axios';
import { useNavigate } from 'react-router-dom';

const Callback = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const exchangeCodeForToken = async () => {
      const code = new URLSearchParams(window.location.search).get('code');

      if (!code) {
        console.error('No authorization code found in URL');
        navigate('/', { replace: true }); // Redirect to login if no code
        return;
      }

      try {
        // Prepare the request body as URLSearchParams
        const params = new URLSearchParams({
          grant_type: 'authorization_code',
          code,
          redirect_uri: 'http://localhost:5173/callback', // Fixed redirect URI
        });

        const response = await axios.post(
          'http://localhost:8081/oauth2/token',
          params, // Pass URLSearchParams directly
          {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
              Authorization: 'Basic ' + btoa('job-tracker-client:secret'),
            },
          }
        );

        // Store tokens
        localStorage.setItem('access_token', response.data.access_token);
        localStorage.setItem('id_token', response.data.id_token);

        // Redirect to dashboard
        navigate('/dashboard', { replace: true });
      } catch (error) {
        const axiosError = error as AxiosError;
        console.error('Token exchange failed with error: ', axiosError);

        // Check if it’s a 400/401 error (invalid code or client credentials)
        if (
          axiosError.response?.status === 400 ||
          axiosError.response?.status === 401
        ) {
          navigate('/', { replace: true }); // Redirect to login
        } else {
          // Handle other errors (e.g., network issues) differently if needed
          navigate('/', { replace: true }); // Adjust as needed
        }
      }
    };

    exchangeCodeForToken();
  }, [navigate]);

  return <div>Processing Login...</div>;
};

export default Callback;
