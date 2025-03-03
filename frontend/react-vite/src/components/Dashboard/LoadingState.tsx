import React from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';
const LoadingState: React.FC = () => (
  <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
    <CircularProgress color="primary" />
    <Typography variant="h6" color="textSecondary" sx={{ ml: 2 }}>
      Loading...
    </Typography>
  </Box>
);

export default LoadingState;