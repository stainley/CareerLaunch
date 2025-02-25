import { useNavigate } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
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
} from '@mui/material';
import {
    Menu as MenuIcon,
    Dashboard as DashboardIcon,
    Settings as SettingsIcon,
    Person as PersonIcon,
    Logout as LogoutIcon,
} from '@mui/icons-material';

// Define the expected user data shape
interface UserData {
    username: string;
    [key: string]: string;
}

const URL_AUTH: string = "http://localhost:8080";
const drawerWidth = 240;

const Dashboard: React.FC = () => {
    const navigate = useNavigate();
    const [userData, setUserData] = useState<UserData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [mobileOpen, setMobileOpen] = useState(false);
    const [profileOpen, setProfileOpen] = useState(false);
    const [userInfo, setUserInfo] = useState({ name: '', email: '' });
    const isLargeScreen = useMediaQuery('(min-width:900px)'); // Adjusted for better breakpoint

    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };

    const handleProfileOpen = () => {
        setProfileOpen(true);
    };

    const handleProfileClose = () => {
        setProfileOpen(false);
    };

    const handleUserInfoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUserInfo({ ...userInfo, [e.target.name]: e.target.value });
    };

    const handleSave = async () => {
        try {
            const token = localStorage.getItem('access_token');
            await axios.put(`${URL_AUTH}/api/v1/users/profile`, userInfo, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setUserData({ ...userData!, username: userInfo.name });
            handleProfileClose();
        } catch (error) {
            console.error('Failed to update profile:', error);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('access_token');
        navigate('/', { replace: true });
    };

    useEffect(() => {
        const fetchUserData = async () => {
            const token = localStorage.getItem('access_token');
            if (!token) {
                navigate('/', { replace: true });
                return;
            }

            try {
                setLoading(true);
                const response = await axios.get<UserData>(`${URL_AUTH}/auth/userinfo`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setUserData(response.data);
                setUserInfo({ name: response.data.username, email: response.data.username });
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

    const drawerItems = [
        { text: 'Dashboard', icon: <DashboardIcon />, tooltip: 'View Dashboard' },
        { text: 'Settings', icon: <SettingsIcon />, tooltip: 'Adjust Settings' },
        { text: 'Profile', icon: <PersonIcon />, tooltip: 'Edit Profile' },
        { text: 'Logout', icon: <LogoutIcon />, onClick: handleLogout, tooltip: 'Sign Out' },
    ];

    const drawer = (
        <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', bgcolor: '#f8f9fa' }}>
            <Toolbar sx={{ bgcolor: 'primary.main', color: 'white' }}>
                <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 600 }}>
                    {isLargeScreen ? 'CareerLaunch' : 'CL'}
                </Typography>
            </Toolbar>
            <Divider />
            <List sx={{ flexGrow: 1 }}>
                {drawerItems.map((item) => (
                    <Tooltip title={item.tooltip} placement="right" arrow key={item.text}>
                        <ListItem
                            button
                            onClick={item.onClick}
                            role="button"
                            tabIndex={0}
                            onKeyUp={(e) => e.key === 'Enter' && item.onClick?.()}
                            sx={{
                                py: 1.5,
                                '&:hover': { bgcolor: 'primary.light', color: 'primary.contrastText' },
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
                            {isLargeScreen && <ListItemText primary={item.text} primaryTypographyProps={{ fontWeight: 500 }} />}
                        </ListItem>
                    </Tooltip>
                ))}
            </List>
            <Divider />
        </Box>
    );

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <Typography variant="h6" color="textSecondary">
                    Loading...
                </Typography>
            </Box>
        );
    }

    if (error) {
        return (
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 8 }}>
                <Typography variant="h4" color="error" gutterBottom>
                    Error
                </Typography>
                <Typography variant="body1" color="textSecondary">
                    {error}
                </Typography>
            </Box>
        );
    }

    return (
        <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: '#f0f2f5' }}>
            <CssBaseline />
            <AppBar
                position="fixed"
                elevation={4}
                sx={{
                    width: { sm: `calc(100% - ${drawerWidth}px)` },
                    ml: { sm: `${drawerWidth}px` },
                    bgcolor: 'linear-gradient(90deg, #1976d2, #42a5f5)',
                }}
            >
                <Toolbar>
                    <IconButton
                        color="inherit"
                        edge="start"
                        onClick={handleDrawerToggle}
                        sx={{ mr: 2, display: { sm: 'none' } }}
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1, fontWeight: 600 }}>
                        Dashboard
                    </Typography>
                    <Tooltip title="Profile" arrow>
                        <IconButton onClick={handleProfileOpen} sx={{ p: 0 }}>
                            <Avatar alt={userData?.username} src="https://via.placeholder.com/40" sx={{ bgcolor: 'secondary.main' }} />
                        </IconButton>
                    </Tooltip>
                </Toolbar>
            </AppBar>
            <Box component="nav" sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}>
                <Drawer
                    variant="temporary"
                    open={mobileOpen}
                    onClose={handleDrawerToggle}
                    ModalProps={{ keepMounted: true }}
                    sx={{
                        display: { xs: 'block', sm: 'none' },
                        '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth / 3, bgcolor: '#f8f9fa' },
                    }}
                >
                    {drawer}
                </Drawer>
                <Drawer
                    variant="permanent"
                    sx={{
                        display: { xs: 'none', sm: 'block' },
                        '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, bgcolor: '#f8f9fa' },
                    }}
                    open
                >
                    {drawer}
                </Drawer>
            </Box>
            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    p: 3,
                    width: { sm: `calc(100% - ${drawerWidth}px)` },
                    mt: '64px',
                    bgcolor: 'white',
                    borderRadius: 2,
                    boxShadow: '0 2px 10px rgba(0, 0, 0, 0.1)',
                }}
            >
                <Typography variant="h4" sx={{ fontWeight: 700, mb: 2, color: 'primary.main' }}>
                    Welcome, {userData?.username}!
                </Typography>
                <Typography variant="body1" color="textSecondary">
                    Your dashboard is ready to help you explore and manage your CareerLaunch experience.
                </Typography>
                {/* Add other dashboard content here */}
            </Box>

            {/* Profile Modal */}
            <Modal open={profileOpen} onClose={handleProfileClose}>
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
                        backdropFilter: 'blur(5px)',
                    }}
                >
                    <Typography variant="h5" component="h2" gutterBottom sx={{ fontWeight: 600, color: 'primary.main' }}>
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
                    <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
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
                            sx={{ textTransform: 'none', fontWeight: 500, borderRadius: 2, px: 3 }}
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