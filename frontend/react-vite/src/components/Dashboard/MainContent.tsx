import React from 'react';
import { Box, Typography } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { UserData } from '../../types';
import { DRAWER_WIDTH } from '../../constants';
import Setting from '@components/Settings/Setting.tsx';

interface MainContentProps {
  userData: UserData | null,
  activeScreen?: 'dashboard' | 'settings'
}

const MainContent: React.FC<MainContentProps> = ({ userData, activeScreen }) => {
  const theme = useTheme();

  const renderContent = () => {
    switch (activeScreen) {
      case 'dashboard':
        return (
          <div>
            <Typography variant="h4" sx={{ fontWeight: 700, mb: 2, color: theme.palette.primary.main }}>
              Welcome, {userData?.email || userData?.username}!
            </Typography>
            <Typography variant="body1" color="textSecondary">
              Your dashboard is ready to help you explore and manage your CareerLaunch experience.
            </Typography>
          </div>
        );
      case 'settings':
        return <Setting />;
      default:
        return (
          <Typography variant="body1" color="textSecondary">
            Unknown Screen
          </Typography>
        );

    }
  };

  return (
    <Box
      component="main"
      sx={{
        flexGrow: 1,
        minHeight: 'calc(100vh - 64px)',
        p: 3,
        width: { xs: '100%', sm: `calc(100% - ${DRAWER_WIDTH}px)` },
        ml: { sm: ` ${DRAWER_WIDTH}px` },
        mt: '64px',
        bgcolor: 'white',
        borderRadius: 2,
        boxShadow: '0 2px 10px rgba(0, 0, 0, 0.1)',
        mx: 1.5,
      }}
    >
      { renderContent() }
    </Box>
  );
};

export default MainContent;

