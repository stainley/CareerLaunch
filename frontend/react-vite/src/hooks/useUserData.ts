import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserData, UserFormData } from '../types';
import axios, { AxiosError } from 'axios';

export const useUserData = () => {

  const navigate = useNavigate();
  const [userData, setUserData] = useState<UserData | null>(null);
  const [userInfo, setUserInfo] = useState<UserFormData>({
    email: '',
    firstName: '',
    lastName: '',
    gender: 'MALE',
    birthDate: '',
    phoneNumber: '',
    professionalSummary: '',
    profilePictureUrl: '',
    address: {
      street: '',
      stateOrProvince: '',
      city: '', country: '',
      postalCode: '',
    },
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {

    const fetchUserData = async () => {
      const token = localStorage.getItem('access_token');
      console.log(`Fetching user data from ${token}`);
      if (!token) {
        navigate('/', { replace: true });
        return;
      }

      try {
        setLoading(true);

        const response = await axios.get<UserFormData>(`  /users/profile/info`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        console.log(`RESPONSE USER INFO: ${JSON.stringify(response)}`);

        //setUserData(response.data);
        setUserInfo({
          firstName: response.data.firstName,
          lastName: response.data.lastName,
          email: response.data.email || response.data.firstName,
          profilePictureUrl: response.data.profilePictureUrl,
          gender: response.data.gender || '',
          birthDate: response.data.birthDate,
          address: {
            stateOrProvince: response.data.address.stateOrProvince,
            postalCode: response.data.address.postalCode,
            country: response.data.address.country,
            city: response.data.address.city,
            street: response.data.address.street,
          },
          phoneNumber: response.data.phoneNumber,
          professionalSummary: response.data.professionalSummary,
        });
      } catch (err) {
        const error = err as AxiosError;
        console.error(`Failed to fetch user data: ${error}`);

        if (error.response?.status === 401) {
          localStorage.removeItem('access_token');
          navigate('/', { replace: true });
        } else {
          setError(`Failed to fetch user data. Please try again later`);
        }
      } finally {
        setLoading(false);
      }
    };

    fetchUserData().finally(() => setLoading(false));

  }, [navigate]);

  return { userData, setUserData, userInfo, setUserInfo, loading, error };
};