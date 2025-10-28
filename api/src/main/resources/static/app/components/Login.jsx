import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Button,
    TextField,
    Box,
    Typography,
    Container,
    Link,
    Snackbar,
    Alert
} from "@mui/material";
import { styled } from '@mui/material/styles';
import { keyframes } from "@emotion/react";
import axios from 'axios';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';
import { postReCaptcha } from './postReCaptcha.js';

const shakeAnimation = keyframes`
    0% { transform: translate(0); }
    25% { transform: translate(-5px); }
    50% { transform: translate(5px); }
    75% { transform: translate(-5px); }
    100% { transform: translate(0); }
`;

const ShakeText = styled(Typography)(({ shake }) => ({
    color: 'red',
    animation: shake ? `${shakeAnimation} 0.5s` : 'none',
}));

const Login = () => {
    const navigate = useNavigate();
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const { executeRecaptcha } = useGoogleReCaptcha();
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!login || !password) {
            setError(true);
            return;
        }

        setIsSubmitting(true);
        setError(false);

        try {
            const token = await postReCaptcha(executeRecaptcha, 'LOGIN');
            const body = {
                login,
                password,
                token,
                "expectedAction": "LOGIN",
                "siteKey": "6Lf1gPQrAAAAAG_tjJ1Jy4QuHJjKy5uBEZZc0z3y",
            };

            const response = await axios.post('/api/login', body, {
                headers: {
                    'Content-Type': 'application/json',
                },
                transformResponse: [function (data) {
                    try {
                        return JSON.parse(data);
                    } catch (e) {
                        return data;
                    }
                }]
            });

            let jwtToken;
            if (typeof response.data === 'string') {
                jwtToken = response.data;
            } else if (response.data.token) {
                jwtToken = response.data.token;
            } else {
                jwtToken = response.data;
            }

            if (jwtToken) {
                localStorage.setItem('jwtToken', jwtToken);
                setSnackbarOpen(true);
                setTimeout(() => {
                    navigate('/');
                }, 1000);
            } else {
                throw new Error('No token received');
            }
        } catch (error) {
            console.error('Login error:', error);
            setError(true);

            if (error.response) {
                console.error('Response error:', error.response.status, error.response.data);
            } else if (error.request) {
                console.error('No response received:', error.request);
            } else {
                console.error('Request setup error:', error.message);
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleSnackbarClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setSnackbarOpen(false);
    };

    const handleAlertClose = () => {
        setSnackbarOpen(false);
    };

    return (
        <Container
            maxWidth="lg"
            sx={{
                mt: 3,
                display: "flex",
                gap: "2rem",
                justifyContent: "center"
            }}
        >
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    mt: 8,
                    p: 3,
                    borderRadius: 1,
                    boxShadow: 3,
                    maxWidth: 400,
                    width: '100%'
                }}
            >
                <Typography component="h1" variant="h5">
                    Вход в личный кабинет
                </Typography>
                <Box
                    component="form"
                    onSubmit={handleSubmit}
                    sx={{ mt: 1, width: '100%' }}
                >
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="Логин"
                        onChange={(e) => setLogin(e.target.value)}
                        autoFocus
                        autoComplete="username"
                        value={login}
                        error={error}
                        disabled={isSubmitting}
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
                        error={error}
                        disabled={isSubmitting}
                    />
                    {error && (
                        <ShakeText shake variant="body2" sx={{ mt: 1 }}>
                            Неправильный логин или пароль.
                        </ShakeText>
                    )}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        color="primary"
                        sx={{ mt: 3, mb: 2 }}
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? 'Вход...' : 'Войти'}
                    </Button>
                    <Button
                        fullWidth
                        variant="text"
                        sx={{ mb: 2 }}
                        onClick={() => navigate("/")}
                        disabled={isSubmitting}
                    >
                        Назад
                    </Button>
                    <Typography variant="body2" align="center">
                        {"У вас нет учетной записи? "}
                        <Link onClick={() => navigate("/auth/signup")} variant="body2">
                            Зарегистрироваться
                        </Link>
                    </Typography>
                </Box>
            </Box>
            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            >
                <Alert
                    onClose={handleAlertClose}
                    severity="success"
                    sx={{ width: '100%' }}
                >
                    Вход успешен
                </Alert>
            </Snackbar>
        </Container>
    );
}

export default Login;