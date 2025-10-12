import React, { useEffect, useState } from 'react';
import {
    Button,
    TextField,
    Paper,
    Box,
    Typography,
    Container,
    Link,
    Snackbar,
    Alert
    } from "@mui/material";
import { CiLogout } from "react-icons/ci";
import { css, keyframes } from "@emotion/react";
import { styled } from '@mui/material/styles';
import axios from 'axios';

const shakeAnimation = keyframes`
      0% { transform: translate(0); }
      25% { transform: translate(-5px); }
      50% { transform: translate(5px); }
      75% { transform: translate(-5px); }
      100% { transform: translate(0); }
    `;

const ShakeText = styled(Typography)`
      color: red;
      animation: ${props => (props.shake ? `${shakeAnimation} 0.5s` : 'none')};
    `;

const Login = () => {
    const [login, setLogin]               = useState();
    const [password, setPassword]         = useState();
    const [error, setError]               = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);

    const handleSubmit = async () => {
        const body = {
          login,
          password,
        };

        const headers = {
          'Content-Type': 'application/json',
        };

        const response = await axios.post(
            '/api/login',
                body,
                headers
                );
        localStorage.setItem('jwtToken', response.data);
        setSnackbarOpen(true);
        navigate('/home');

    };

  return (
  <Container maxWidth="lg" sx={{ mt: 3,  display: "flex", gap: "2rem",  justifyContent: "center" }}>
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          mt: 8,
          p: 3,
          borderRadius: 1,
          boxShadow: 3,
        }}
      >
        <Typography component="h1" variant="h5">
          Вход в личный кабинет
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            label="Логин"
            onChange={(e) => setLogin(e.target.value)}
            autoFocus
            autoComplete="login"
            value={login}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            label="Пароль"
            type="password"
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            value={password}
          />
          {error && <ShakeText shake>Неправильный логин или пароль. </ShakeText>}
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            sx={{ mt: 3, mb: 2 }}
          >
            Войти
          </Button>
          <Button
            fullWidth
            variant="text"
            sx={{ mb: 2 }}
            onClick={() => window.location.href = '/home'}
          >
            Назад
          </Button>
          <Typography variant="body2" align="center">
            {"У вас нет учетной записи? "}
            <Link href="/signup" variant="body2">
              Зарегистрироваться
            </Link>
          </Typography>
        </Box>
      </Box>
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={() => setSnackbarOpen(false)}
      >
        <Alert onClose={() => setSnackbarOpen(false)} severity="success" sx={{ width: '100%' }}>
          Вход успешен
        </Alert>
      </Snackbar>
  </Container>
  );
}

export default Login;
