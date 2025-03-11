import React, { useCallback, useRef, useState } from 'react';
import {
  Modal,
  Box,
  Typography,
  TextField,
  Button,
  Divider,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  FormHelperText,
  CircularProgress,
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
//import axios from 'axios';
//import { API_BASE_URL } from '../../constants';
import { UserData, UserFormData } from '../../types';
import profilePic from '../../assets/user.png';
import { PhotoCamera } from '@mui/icons-material';
import axios from 'axios';

interface ProfileModalProps {
  open: boolean;
  onClose: () => void;
  userInfo: UserFormData;
  setUserInfo: React.Dispatch<React.SetStateAction<UserFormData>>;
  setUserData: React.Dispatch<React.SetStateAction<UserData | null>>;
}

// Extended UserFormData interface to match backend User entity
interface ExtendedUserFormData extends UserFormData {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  birthDate: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER' | '';
  address: {
    street: string;
    city: string;
    stateOrProvince: string;
    postalCode: string;
    country: string;
  };
  profilePictureUrl: string;
  professionalSummary: string;
}

const ProfileModal: React.FC<ProfileModalProps> = ({
                                                     open,
                                                     onClose,
                                                     userInfo,
                                                     setUserInfo,
                                                     setUserData,
                                                   }) => {
  const theme = useTheme();
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [imagePreview, setImagePreview] = useState<string | null>(null); // For image preview
  const fileInputRef = useRef<HTMLInputElement>(null); // Ref for hidden file input
  const [isHovered, setIsHovered] = useState(false);

  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | { target: { name: string; value: any } }) => {
      const { name, value } = e.target;

      // Handle nested address fields
      if (name.startsWith('address.')) {
        const addressField = name.split('.')[1];
        setUserInfo((prev: ExtendedUserFormData) => ({
          ...prev,
          address: {
            ...prev.address,
            [addressField]: value,
          },
        }));
      } else {
        setUserInfo((prev: ExtendedUserFormData) => ({ ...prev, [name]: value }));
      }

      // Clear error when user starts typing
      setErrors((prev) => ({ ...prev, [name]: '' }));
    },
    [setUserInfo],
  );

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    const { firstName, lastName, email, phoneNumber } = userInfo as ExtendedUserFormData;

    if (!firstName) newErrors.firstName = 'First name is required';
    if (!lastName) newErrors.lastName = 'Last name is required';
    if (!email) newErrors.email = 'Email is required';
    if (!phoneNumber) newErrors.phoneNumber = 'Phone number is required';
    if (phoneNumber && !/^\+?[1-9]\d{1,14}$/.test(phoneNumber)) {
      newErrors.phoneNumber = 'Invalid phone number format';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Trigger file input click
  const handleImageClick = () => {
    fileInputRef.current?.click();
  };

  // Handle image selection
  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result as string); // Set preview
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSave = useCallback(async () => {
    if (!validateForm()) return;

    setLoading(true);
    try {
      const token = localStorage.getItem('access_token');
      if (!token) throw new Error('No authentication token found');

      // TODO: to be implemented
      /*const response = await axios.put(`/users/profile`, userInfo, {
        headers: { Authorization: `Bearer ${token}` },
      });*/


      setUserData((prev) => prev ? {
        ...prev,
        username: `${(userInfo as ExtendedUserFormData).firstName} ${(userInfo as ExtendedUserFormData).lastName}`,
        email: (userInfo as ExtendedUserFormData).email,
      } : null);
      onClose();
    } catch (error) {
      console.error('Failed to update profile:', error);
      setErrors({ submit: 'Failed to save profile. Please try again.' });
    } finally {
      setLoading(false);
    }
  }, [userInfo, onClose, setUserData]);


  const handleImageUpload = async () => {
    if (!imagePreview || !fileInputRef.current?.files?.[0]) return;

    setLoading(true);

    try {
      const token = localStorage.getItem('access_token');
      if (!token) throw new Error('No authentication token found');

      console.log(token);
      const formData = new FormData();
      formData.append('file', fileInputRef.current.files[0]);

      const response = await axios.post(`/users/profile-picture/`, formData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('access_token')}`,
          'Content-Type': 'multipart/form-data',
        },
      });

      const updatedUser = response.data; // Response is the User object
      const profilePictureUrl = updatedUser.profilePictureUrl;

      // Update userInfo with the new profile picture URL
      setUserInfo((prev: ExtendedUserFormData) => ({
        ...prev,
        profilePictureUrl,
      }));

      setUserData((prev) =>
        prev ? { ...prev, avatarUrl: profilePictureUrl } : null,
      );

      setImagePreview(null);
    } catch (error) {
      console.error('Failed to upload profile picture:', error);
      setErrors({ submit: 'Failed to upload image. Please try again.' });
    } finally {
      setLoading(false);
    }

  };

  return (
    <Modal
      open={open}
      onClose={loading ? undefined : onClose}
      aria-labelledby="profile-modal-title"
    >
      <Box
        sx={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          width: { xs: '90%', sm: 800 },
          maxHeight: '90vh',
          bgcolor: 'background.paper',
          borderRadius: 3,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.2)',
          p: 4,
          overflowY: 'auto',
        }}
      >
        <Typography
          id="profile-modal-title"
          variant="h5"
          sx={{
            fontWeight: 600,
            color: theme.palette.primary.main,
            mb: 2,
          }}
        >
          Edit Profile
        </Typography>

        {/* Two-column layout */}
        <Box sx={{ display: 'flex', gap: 4, flexDirection: { xs: 'column', sm: 'row' } }}>

          {/* Left Column */}
          <Box sx={{ flex: 1 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Personal Information
            </Typography>
            <Divider />
            <TextField
              fullWidth
              label="First Name"
              name="firstName"
              value={(userInfo as ExtendedUserFormData).firstName}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
              required
              error={!!errors.firstName}
              helperText={errors.firstName}
            />
            <TextField
              fullWidth
              label="Last Name"
              name="lastName"
              value={(userInfo as ExtendedUserFormData).lastName}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
              required
              error={!!errors.lastName}
              helperText={errors.lastName}
            />
            <TextField
              fullWidth
              label="Email"
              name="email"
              type="email"
              value={userInfo.email}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
              disabled
              required
              error={!!errors.email}
              helperText={errors.email}
            />
            <TextField
              fullWidth
              label="Phone Number"
              name="phoneNumber"
              value={(userInfo as ExtendedUserFormData).phoneNumber}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
              required
              error={!!errors.phoneNumber}
              helperText={errors.phoneNumber || 'Format: +1234567890'}
            />
          </Box>

          {/* Right Column */}
          <Box sx={{ flex: 1 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Additional Information
            </Typography>

            <TextField
              fullWidth
              label="Birth Date"
              name="birthDate"
              type="date"
              value={(userInfo as ExtendedUserFormData).birthDate}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
              InputLabelProps={{ shrink: true }}
            />
            <FormControl fullWidth margin="normal" variant="outlined">
              <InputLabel>Gender</InputLabel>
              <Select
                name="gender"
                value={(userInfo as ExtendedUserFormData).gender}
                onChange={handleChange}
                label="Gender"
              >
                <MenuItem value="">Not specified</MenuItem>
                <MenuItem value="MALE">Male</MenuItem>
                <MenuItem value="FEMALE">Female</MenuItem>
                <MenuItem value="OTHER">Other</MenuItem>
              </Select>
            </FormControl>

            {/* Profile Picture Section */}
            <Box sx={{ mt: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
              <Box
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
                onClick={handleImageClick}
                sx={{
                  width: 120,
                  height: 120,
                  borderRadius: '50%',
                  overflow: 'hidden',
                  cursor: 'pointer',
                  position: 'relative',
                  backgroundColor: theme.palette.grey[200],
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                {profilePic && !isHovered ? (
                  <img
                    src={
                      imagePreview ||
                      (userInfo as ExtendedUserFormData).profilePictureUrl ||
                      profilePic
                    }
                    alt="Profile"
                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  />
                ) : (
                  <PhotoCamera sx={{ fontSize: 40, color: theme.palette.grey[500] }} />
                )}
              </Box>
              <Box>
                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleImageChange}
                  accept="image/*"
                  style={{ display: 'none' }}
                />
                <Button
                  variant="outlined"
                  onClick={handleImageUpload}
                  disabled={!imagePreview || loading}
                  startIcon={loading ? <CircularProgress size={20} /> : <PhotoCamera />}
                >
                  {loading ? 'Uploading...' : 'Upload Picture'}
                </Button>
              </Box>

            </Box>
          </Box>
        </Box>

        <Divider />

        {/* Professional Information */}
        <Box>
          <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>
            Professional Information
          </Typography>
          <TextField
            fullWidth
            label="Profile Picture URL"
            name="profilePictureUrl"
            value={(userInfo as ExtendedUserFormData).profilePictureUrl || ''}
            onChange={handleChange}
            margin="normal"
            variant="outlined"
          />
          <TextField
            fullWidth
            label="Professional Summary"
            name="professionalSummary"
            value={(userInfo as ExtendedUserFormData).professionalSummary || ''}
            onChange={handleChange}
            margin="normal"
            variant="outlined"
            multiline
            rows={4}
            inputProps={{ maxLength: 500 }}
          />
          <FormHelperText>
            {((userInfo as ExtendedUserFormData).professionalSummary?.length || 0)}/500 characters
          </FormHelperText>

        </Box>


        {/* Address Information */}
        <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>
          Address
        </Typography>
        <TextField
          fullWidth
          label="Street"
          name="address.street"
          value={(userInfo as ExtendedUserFormData).address?.street || ''}
          onChange={handleChange}
          margin="normal"
          variant="outlined"
        />
        <Box sx={{ display: 'flex', gap: 4, flexDirection: { xs: 'column', sm: 'row' } }}>

          <Box sx={{ flex: 1 }}>

            <TextField
              fullWidth
              label="Country"
              name="address.country"
              value={(userInfo as ExtendedUserFormData).address?.country || ''}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
            />
            <TextField
              fullWidth
              label="City"
              name="address.city"
              value={(userInfo as ExtendedUserFormData).address?.city || ''}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
            />
          </Box>
          <Box sx={{ flex: 1 }}>
            <TextField
              fullWidth
              label="State"
              name="address.state"
              value={(userInfo as ExtendedUserFormData).address?.stateOrProvince || ''}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
            />
            <TextField
              fullWidth
              label="Postal Code"
              name="address.postalCode"
              value={(userInfo as ExtendedUserFormData).address?.postalCode || ''}
              onChange={handleChange}
              margin="normal"
              variant="outlined"
            />

          </Box>
        </Box>


        {/* Error Display */}
        {errors.submit && (
          <Typography color="error" sx={{ mt: 2 }}>
            {errors.submit}
          </Typography>
        )}

        {/* Action Buttons */}
        <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
          <Button
            onClick={onClose}
            color="secondary"
            sx={{ textTransform: 'none' }}
            disabled={loading}
          >
            Cancel
          </Button>
          <Button
            onClick={handleSave}
            variant="contained"
            color="primary"
            sx={{
              textTransform: 'none',
              borderRadius: 2,
              px: 3,
              minWidth: '100px',
            }}
            disabled={loading}
          >
            {loading ? <CircularProgress size={24} /> : 'Save'}
          </Button>
        </Box>
      </Box>
    </Modal>
  );
};

export default ProfileModal;