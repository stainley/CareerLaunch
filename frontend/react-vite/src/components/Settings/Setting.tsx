import React from 'react';
import { Box, Button, TextField, Typography } from '@mui/material';
import { useTheme } from '@mui/material/styles';

const Settings: React.FC = () => {

  const theme = useTheme();

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 2, color: theme.palette.primary.main }}>
        Settings
      </Typography>
      <Typography variant="body1" color="textSecondary" sx={{ mb: 3 }}>
        Customize your CareerLaunch experience.
      </Typography>
      <TextField
        fullWidth
        label="Notification Email"
        variant="outlined"
        defaultValue="user@example.com"
        sx={{ mb: 2 }}
      />
      <TextField
        fullWidth
        label="Default Language"
        variant="outlined"
        defaultValue="English"
        sx={{ mb: 2 }}
      />
      <Button variant="contained" color="primary" sx={{ mt: 2 }}>
        Save Settings
      </Button>
    </Box>
  );
};

export default Settings;