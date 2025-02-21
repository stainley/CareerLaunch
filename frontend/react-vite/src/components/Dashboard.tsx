// src/components/Dashboard.tsx
import {useNavigate} from 'react-router-dom';
import {useEffect, useState} from 'react';
import axios, {AxiosError} from 'axios';

// Define the expected user data shape
interface UserData {
    username: string; // Adjust based on your API response
    [key: string]: any; // For flexibility if additional fields exist
}

const Dashboard = () => {
    const navigate = useNavigate();
    const [userData, setUserData] = useState<UserData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchUserData = async () => {
            const token = localStorage.getItem('access_token');

            // Check token presence before making the request
            if (!token) {
                navigate('/', {replace: true}); // Redirect to login, replace history
                return;
            }

            try {
                setLoading(true);
                const response = await axios.get<UserData>('http://localhost:8081/api/userinfo', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setUserData(response.data);
                console.log('User data:', response.data);
            } catch (err) {
                const error = err as AxiosError;
                console.error('Failed to fetch user data:', error);

                // Handle 401 Unauthorized specifically
                if (error.response?.status === 401) {
                    localStorage.removeItem('access_token'); // Clear invalid token
                    navigate('/', {replace: true});
                } else {
                    setError('Failed to load user data. Please try again.');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, [navigate]); // navigate is stable, so this is fine

    const handleLogout = () => {
        localStorage.removeItem('access_token'); // Only remove the token
        navigate('/', {replace: true}); // Redirect to login
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return (
            <div>
                <h1>Error</h1>
                <p>{error}</p>
            </div>
        );
    }

    return (
        <div style={{textAlign: 'center', marginTop: '50px'}}>
            <h1>Dashboard</h1>
            {userData && <p>Welcome, {userData.username}!</p>}
            <button onClick={handleLogout}>Logout</button>
        </div>
    );
};

export default Dashboard;
