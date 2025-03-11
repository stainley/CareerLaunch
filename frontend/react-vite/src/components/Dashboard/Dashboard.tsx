import React, { useState, useCallback } from 'react';
import { Box, CssBaseline, useMediaQuery } from '@mui/material';
import { useUserData } from '@hooks/useUserData.ts';
import AppHeader from './AppHeader';
import NavigationDrawer from './NavigationDrawer';
import MainContent from './MainContent';
import ProfileModal from './ProfileModal';
import LoadingState from './LoadingState';
import ErrorState from './ErrorState';

// Main Dashboard Component
const Dashboard: React.FC = () => {

  const isLargeScreen = useMediaQuery('(min-width:900px)');
  const { userData, setUserData, userInfo, setUserInfo, loading, error } = useUserData();

  const [mobileOpen, setMobileOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [activeScreen, setActiveScreen] = useState<'dashboard' | 'settings'>('dashboard');

  const handleDrawerToggle = useCallback(() => setMobileOpen((prev) => !prev), []);
  const handleProfileOpen = useCallback(() => setProfileOpen(true), []);
  const handleProfileClose = useCallback(() => setProfileOpen(false), []);

  const handleScreenChange = useCallback((screen: 'dashboard' | 'settings') => {
    setActiveScreen(screen);
    if (!isLargeScreen) setMobileOpen(false); // Close drawer on mobile after selection
  }, [isLargeScreen]);

  if (loading) return <LoadingState />;
  if (error) return <ErrorState error={error} />;


  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: '#f0f2f5' }}>
      <CssBaseline />
      <AppHeader
        userData={userData}
        onDrawerToggle={handleDrawerToggle}
        onProfileOpen={handleProfileOpen}
        isLargeScreen={isLargeScreen}
      />
      <NavigationDrawer
        mobileOpen={mobileOpen}
        onDrawerToggle={handleDrawerToggle}
        onProfileOpen={handleProfileOpen}
        onScreenChange={handleScreenChange}
        isLargeScreen={isLargeScreen}
      />
      <MainContent
        userData={userData}
        activeScreen={activeScreen}
      />
      <ProfileModal
        open={profileOpen}
        onClose={handleProfileClose}
        userInfo={userInfo}
        setUserInfo={setUserInfo}
        setUserData={setUserData}
      />
    </Box>
  );
};

export default Dashboard;