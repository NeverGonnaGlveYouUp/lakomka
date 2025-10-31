import React from 'react';
import { Box, Button, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const ErrorPage = () => {

  const navigate = useNavigate();

  const handleBackHome = () => {
    history.push('/');
  };

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100vh',
        bgcolor: 'background.paper',
        textAlign: 'center'
      }}
    >
      <Typography variant="h1" component="h2" color="error">
        Ой!
      </Typography>
      <Typography variant="h5" sx={{ marginBottom: 2 }}>
        Что-то пошло не так.
      </Typography>
      <Button
        variant="contained"
        onClick={handleBackHome}
      >
        На Главную
      </Button>
    </Box>
  );
};

export default ErrorPage;
