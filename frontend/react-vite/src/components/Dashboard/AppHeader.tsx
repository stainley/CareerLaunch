import React from 'react';

import { AppBar, Toolbar, IconButton, Typography, Avatar, Tooltip, Box } from '@mui/material';
import { Menu as MenuIcon } from '@mui/icons-material';
import { UserFormData } from '../../types';
import { DRAWER_WIDTH } from '../../constants';

import profilePic from '../../assets/user.png';

interface AppHeaderProps {
  userData: UserFormData | null;
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
      <Box
        sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
        <Typography variant="h6" noWrap sx={{ fontWeight: 600, marginX: 1 }} color={'primary'}>
          {userData?.firstName} {userData?.lastName}
        </Typography>
        <Tooltip title="Profile">
          <IconButton onClick={onProfileOpen} sx={{ p: 0 }} aria-label="open profile">
            <Avatar
              alt={userData != null ? `${userData?.firstName} ${userData.lastName}` : ''}
              src={userData?.profilePictureUrl != null ? userData?.profilePictureUrl : profilePic}
              sx={{ bgcolor: 'secondary.main' }}
            />
          </IconButton>
        </Tooltip>
      </Box>

    </Toolbar>
  </AppBar>
);

export default AppHeader;