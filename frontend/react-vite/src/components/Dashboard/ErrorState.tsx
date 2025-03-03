import React from 'react';
import { Box, Typography, Button } from '@mui/material';

interface ErrorStateProps {
  error: string;
}

const ErrorState: React.FC<ErrorStateProps> = ({ error }) => (
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
    <Button variant="contained" color="primary" sx={{ mt: 2 }} onClick={() => window.location.reload()}>
      Retry
    </Button>
  </Box>
);

export default ErrorState;