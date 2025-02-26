import { useNavigate } from 'react-router-dom';
import React, { useEffect, useState, useCallback } from 'react';
import axios, { AxiosError } from 'axios';
import {
  AppBar,
  Toolbar,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Box,
  Avatar,
  Modal,
  Typography,
  TextField,
  Button,
  useMediaQuery,
  CssBaseline,
  Divider,
  Tooltip,
  CircularProgress,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  Settings as SettingsIcon,
  Person as PersonIcon,
  Logout as LogoutIcon,
} from '@mui/icons-material';
import { useTheme } from '@mui/material/styles';

// Constants
const API_BASE_URL = import.meta.env.VITE_API_UR || 'http://localhost:8080';

//const API_BASE_URL = "http://localhost:8080";
const DRAWER_WIDTH = 240;

// Types
interface UserData {
  username: string;
  email?: string;

  [key: string]: string | undefined;
}

interface UserFormData {
  name: string;
  email: string;
}

interface DrawerItem {
  id: string;
  text: string;
  icon: React.ReactNode;
  tooltip: string;
  onClick?: () => void;
}

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const theme = useTheme();
  const isLargeScreen = useMediaQuery('(min-width:900px)');

  // State
  const [userData, setUserData] = useState<UserData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [userInfo, setUserInfo] = useState<UserFormData>({
    name: '',
    email: '',
  });

  // Handlers
  const handleDrawerToggle = useCallback(() => {
    setMobileOpen((prev) => !prev);
  }, []);

  const handleProfileOpen = useCallback(() => {
    setProfileOpen(true);
  }, []);

  const handleProfileClose = useCallback(() => {
    setProfileOpen(false);
  }, []);

  const handleUserInfoChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const { name, value } = e.target;
      setUserInfo((prev) => ({ ...prev, [name]: value }));
    },
    [],
  );

  const handleLogout = useCallback(() => {
    localStorage.removeItem('access_token');
    navigate('/', { replace: true });
  }, [navigate]);

  const handleSave = useCallback(async () => {
    try {
      const token = localStorage.getItem('access_token');
      if (!token) {
        throw new Error('No authentication token found');
      }

      await axios.put(`${API_BASE_URL}/api/v1/users/profile`, userInfo, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setUserData((prev) =>
        prev ? { ...prev, username: userInfo.name } : null,
      );
      handleProfileClose();
    } catch (error) {
      console.error('Failed to update profile:', error);
      setError('Failed to update profile. Please try again.');
    }
  }, [userInfo, handleProfileClose]);

  // Data fetching
  useEffect(() => {
    const fetchUserData = async () => {
      const token = localStorage.getItem('access_token');
      if (!token) {
        navigate('/', { replace: true });
        return;
      }

      try {
        setLoading(true);
        const response = await axios.get<UserData>(
          `${API_BASE_URL}/auth/userinfo`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        );

        setUserData(response.data);
        setUserInfo({
          name: response.data.username,
          email: response.data.email || response.data.username,
        });
      } catch (err) {
        const error = err as AxiosError;
        console.error('Failed to fetch user data:', error);

        if (error.response?.status === 401) {
          localStorage.removeItem('access_token');
          navigate('/', { replace: true });
        } else {
          setError('Failed to load user data. Please try again.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [navigate]);

  // Drawer items configuration
  const drawerItems: DrawerItem[] = [
    { id: 'drawer-item-dashboard', text: 'Dashboard', icon: <DashboardIcon />, tooltip: 'View Dashboard' },
    { id: 'drawer-item-settings', text: 'Settings', icon: <SettingsIcon />, tooltip: 'Adjust Settings' },
    {
      id: 'drawer-item-profile',
      text: 'Profile',
      icon: <PersonIcon />,
      tooltip: 'Edit Profile',
      onClick: handleProfileOpen,
    },
    {
      id: 'drawer-item-logout',
      text: 'Logout',
      icon: <LogoutIcon />,
      onClick: handleLogout,
      tooltip: 'Sign Out',
    },
  ];

  // Render drawer content
  const renderDrawerContent = () => (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        bgcolor: '#f8f9fa',
      }}
    >
      <Toolbar sx={{ bgcolor: theme.palette.primary.main, color: 'white' }}>
        <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 600 }}>
          {isLargeScreen ? 'CareerLaunch' : 'CL'}
        </Typography>
      </Toolbar>
      <Divider />
      <List sx={{
        flexGrow: 1,
      }}>
        {drawerItems.map((item) => (
          <Tooltip title={item.tooltip} placement="right" arrow key={item.text}>
            <ListItem
              key={item.text}
              onClick={item.onClick}
              role="button"
              tabIndex={0}
              data-testid={item.id}
              onKeyUp={(e) => e.key === 'Enter' && item.onClick?.()}
              sx={{
                py: 1.5,
                '&:hover': {
                  bgcolor: theme.palette.primary.light,
                  color: theme.palette.primary.contrastText,
                },
                transition: 'all 0.3s ease',
              }}
            >
              <ListItemIcon
                sx={{
                  color: 'inherit',
                  minWidth: isLargeScreen ? '56px' : '40px',
                }}
              >
                {item.icon}
              </ListItemIcon>
              {isLargeScreen && (
                <ListItemText
                  primary={item.text}
                  primaryTypographyProps={{ fontWeight: 500 }}
                />
              )}
            </ListItem>
          </Tooltip>
        ))}
      </List>
      <Divider />
    </Box>
  );

  // Loading state
  if (loading) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
        }}
      >
        <CircularProgress color="primary" />
        <Typography variant="h6" color="textSecondary" sx={{ ml: 2 }}>
          Loading...
        </Typography>
      </Box>
    );
  }

  // Error state
  if (error) {
    return (
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          height: '100vh',
        }}
      >
        <Typography variant="h4" color="error" gutterBottom>
          Error
        </Typography>
        <Typography variant="body1" color="textSecondary">
          {error}
        </Typography>
        <Button
          variant="contained"
          color="primary"
          sx={{ mt: 2 }}
          onClick={() => window.location.reload()}
        >
          Retry
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: '#f0f2f5' }}>
      <CssBaseline />

      {/* App Bar */}
      <AppBar
        position="fixed"
        elevation={4}
        sx={{
          width: { sm: `calc(100% - ${DRAWER_WIDTH}px)` },
          ml: { sm: `${DRAWER_WIDTH}px` },
          background: 'linear-gradient(90deg, #1976d2, #42a5f5)',
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
            aria-label="open drawer"
          >
            <MenuIcon />
          </IconButton>
          <Typography
            variant="h6"
            noWrap
            component="div"
            sx={{ flexGrow: 1, fontWeight: 600 }}
          >
            Dashboard
          </Typography>
          <Tooltip title="Profile" arrow>
            <IconButton
              onClick={handleProfileOpen}
              sx={{ p: 0 }}
              aria-label="open profile"
            >
              <Avatar
                alt={userData?.username}
                src="https://via.placeholder.com/40"
                sx={{ bgcolor: theme.palette.secondary.main }}
              />
            </IconButton>
          </Tooltip>
        </Toolbar>
      </AppBar>

      {/* Navigation Drawer */}
      <Box
        component="nav"
        sx={{ width: { sm: DRAWER_WIDTH }, flexShrink: { sm: 0 } }}
      >
        {/* Mobile Drawer */}
        {!isLargeScreen && (
          <Drawer
            variant="temporary"
            open={mobileOpen}
            onClose={handleDrawerToggle}
            ModalProps={{ keepMounted: true }}
            sx={{
              display: { xs: 'block', sm: 'none' },
              '& .MuiDrawer-paper': {
                boxSizing: 'border-box',
                width: DRAWER_WIDTH / 1.5,
                bgcolor: '#f8f9fa',
              },
            }}
          >
            {renderDrawerContent()}
          </Drawer>
        )}
        {/* Desktop Drawer */}
        {isLargeScreen && (
          <Drawer
            variant="permanent"
            sx={{
              display: { xs: 'none', sm: 'block' },
              '& .MuiDrawer-paper': {
                boxSizing: 'border-box',
                width: DRAWER_WIDTH,
                bgcolor: '#f8f9fa',
              },
            }}
            open
          >
            {renderDrawerContent()}
          </Drawer>
        )}

      </Box>

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { sm: `calc(100% - ${DRAWER_WIDTH}px)` },
          mt: '64px',
          bgcolor: 'white',
          borderRadius: 2,
          boxShadow: '0 2px 10px rgba(0, 0, 0, 0.1)',
          mx: 1.5,
          my: 10,
        }}
      >
        <Typography
          variant="h4"
          sx={{ fontWeight: 700, mb: 2, color: theme.palette.primary.main }}
        >
          Welcome, {userData?.username}!
        </Typography>
        <Typography variant="body1" color="textSecondary">
          Your dashboard is ready to help you explore and manage your
          CareerLaunch experience.
        </Typography>
        {/* Add other dashboard content here */}
      </Box>

      {/* Profile Modal */}
      <Modal
        open={profileOpen}
        onClose={handleProfileClose}
        aria-labelledby="profile-modal-title"
      >
        <Box
          sx={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            width: { xs: '90%', sm: 400 },
            bgcolor: 'background.paper',
            borderRadius: 3,
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.2)',
            p: 4,
          }}
        >
          <Typography
            id="profile-modal-title"
            variant="h5"
            component="h2"
            gutterBottom
            sx={{ fontWeight: 600, color: theme.palette.primary.main }}
          >
            Profile Information
          </Typography>
          <Divider sx={{ mb: 2 }} />
          <TextField
            fullWidth
            label="Name"
            name="name"
            value={userInfo.name}
            onChange={handleUserInfoChange}
            margin="normal"
            variant="outlined"
            sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
          />
          <TextField
            fullWidth
            label="Email"
            name="email"
            value={userInfo.email}
            onChange={handleUserInfoChange}
            margin="normal"
            variant="outlined"
            sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
          />
          <Box
            sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 2 }}
          >
            <Button
              onClick={handleProfileClose}
              color="secondary"
              sx={{ textTransform: 'none', fontWeight: 500 }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleSave}
              variant="contained"
              color="primary"
              sx={{
                textTransform: 'none',
                fontWeight: 500,
                borderRadius: 2,
                px: 3,
              }}
            >
              Save
            </Button>
          </Box>
        </Box>
      </Modal>
    </Box>
  );
};

export default Dashboard;
