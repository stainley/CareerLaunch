import { AppBar, Toolbar, IconButton, Typography, Avatar, Tooltip, Box } from '@mui/material';
import { Menu as MenuIcon } from '@mui/icons-material';
import { UserData } from '../../types';
import { DRAWER_WIDTH } from '../../constants';

import React from 'react';

import profilePic from '../../assets/user.png';

interface AppHeaderProps {
  userData: UserData | null;
  onDrawerToggle: () => void;
  onProfileOpen: () => void;
  isLargeScreen: boolean;
}

const AppHeader: React.FC<AppHeaderProps> = ({
                                               userData,
                                               onDrawerToggle,
                                               onProfileOpen,
                                               isLargeScreen,
                                             }) => (
  <AppBar
    position="fixed"
    elevation={2}
    sx={{
      width: { sm: `calc(100% - ${DRAWER_WIDTH}px)` },
      ml: { sm: `${DRAWER_WIDTH}px` },
      backgroundColor: 'rgba(255, 255, 255, 0.8)',
      backdropFilter: 'blur(10px)',
      borderBottom: '1px solid rgba(0, 0, 0, 0.12)',
    }}
  >

    <Toolbar sx={{ justifyContent: 'space-between' }}>
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        {!isLargeScreen && (
          <IconButton
            color="inherit"
            edge="start"
            onClick={onDrawerToggle}
            sx={{ mr: 2 }}
            aria-label="open drawer"
          >
            <MenuIcon />
          </IconButton>
        )}
        <Typography variant="h6" noWrap sx={{ fontWeight: 600 }} color={'primary'}>
          Dashboard
        </Typography>
      </Box>
      <Tooltip title="Profile" arrow>
        <IconButton onClick={onProfileOpen} sx={{ p: 0 }} aria-label="open profile">
          <Avatar
            alt={userData?.username}
            src={profilePic}
            sx={{ bgcolor: 'secondary.main' }}
          />
        </IconButton>
      </Tooltip>
    </Toolbar>
  </AppBar>
);

export default AppHeader;