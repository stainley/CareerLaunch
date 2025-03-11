import React, { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Drawer,
  Toolbar,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Box,
  Divider,
  Tooltip,
  Typography,
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import {
  Dashboard as DashboardIcon,
  Settings as SettingsIcon,
  Person as PersonIcon,
  Logout as LogoutIcon,
  AdminPanelSettings,
} from '@mui/icons-material';
import { DrawerItem } from '../../types';
import { DRAWER_WIDTH } from '../../constants';

interface NavigationDrawerProps {
  mobileOpen: boolean,
  onDrawerToggle: () => void,
  onProfileOpen: () => void,
  isLargeScreen: boolean,
  onScreenChange: (screen: ('dashboard' | 'settings')) => void
}

const NavigationDrawer: React.FC<NavigationDrawerProps> = ({
                                                             mobileOpen,
                                                             onDrawerToggle,
                                                             onProfileOpen,
                                                             isLargeScreen,
                                                             onScreenChange,
                                                           }) => {
  const theme = useTheme();
  const navigate = useNavigate();

  const handleLogout = useCallback(() => {
    localStorage.removeItem('access_token');
    navigate('/', { replace: true });
  }, [navigate]);

  /*const handleSetting = useCallback(() => {
    navigate('/settings');
  }, [navigate]);*/

  const drawerItems: DrawerItem[] = [
    {
      id: 'dashboard',
      text: 'Dashboard',
      icon: <DashboardIcon />,
      tooltip: 'View Dashboard',
      onClick: () => onScreenChange('dashboard'),
    },
    {
      id: 'settings',
      text: 'Settings',
      icon: <SettingsIcon />,
      tooltip: 'Adjust Settings',
      onClick: () => onScreenChange('settings'),
    },
    {
      id: 'profile',
      text: 'Profile',
      icon: <PersonIcon />,
      tooltip: 'Edit Profile',
      onClick: onProfileOpen,
    },
    {
      id: 'administration',
      text: 'Administration',
      icon: <AdminPanelSettings />,
      tooltip: 'Administration',
    },
    {
      id: 'logout',
      text: 'Logout',
      icon: <LogoutIcon />,
      tooltip: 'Sign Out',
      onClick: handleLogout,
    },
  ];

  const drawerContent = (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        backdropFilter: 'blur(6px)',
      }}
    >
      <Toolbar sx={{ backgroundColor: theme.palette.primary.main, color: 'white' }}>
        <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
          {isLargeScreen ? 'CareerLaunch' : 'CL'}
        </Typography>
      </Toolbar>
      <Divider />
      <List sx={{ flexGrow: 1 }}>
        {drawerItems.map((item) => (
          <Tooltip key={item.id} title={item.tooltip} placement="right" arrow>
            <ListItem
              component="button"
              onClick={item.onClick}
              onKeyUp={(e) => e.key === 'Enter' && item.onClick?.()}
              sx={{
                py: 1.5,
                '&:hover': {
                  backgroundColor: theme.palette.primary.light,
                  color: theme.palette.primary.contrastText,
                  '& .MuiListItemIcon-root': { color: theme.palette.primary.contrastText },
                },
              }}
            >
              <ListItemIcon sx={{ color: 'inherit', minWidth: isLargeScreen ? '56px' : '40px' }}>
                {item.icon}
              </ListItemIcon>
              {isLargeScreen && (
                <ListItemText primary={item.text} />
              )}
            </ListItem>
          </Tooltip>
        ))}
      </List>
      <Divider />
    </Box>
  );

  return isLargeScreen ? (
    <Drawer
      variant="permanent"
      sx={{
        display: { xs: 'none', sm: 'block' },
        '& .MuiDrawer-paper': {
          width: DRAWER_WIDTH,
          boxSizing: 'border-box',
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          backdropFilter: 'blur(6px)',
          borderRight: '1px solid rgba(0, 0, 0, 0.12)',
          boxShadow: '2px 0 5px rgba(0, 0, 0, 0.1)',
        },
      }}
    >
      {drawerContent}
    </Drawer>
  ) : (
    <Drawer
      variant="temporary"
      open={mobileOpen}
      onClose={onDrawerToggle}
      ModalProps={{ keepMounted: true }}
      sx={{
        display: { xs: 'block', sm: 'none' },
        '& .MuiDrawer-paper': {
          width: DRAWER_WIDTH / 1.5,
          boxSizing: 'border-box',
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          backdropFilter: 'blur(6px)',
        },
      }}
    >
      {drawerContent}
    </Drawer>
  );
};

export default NavigationDrawer;