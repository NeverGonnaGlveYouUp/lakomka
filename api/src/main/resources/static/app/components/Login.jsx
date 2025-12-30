import React, { useState, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
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
import { SmartCaptcha } from '@yandex/smart-captcha';

const shakeAnimation = keyframes`
    0% { transform: translate(0); }
    25% { transform: translate(-5px); }
    50% { transform: translate(5px); }
    75% { transform: translate(-5px); }
    100% { transform: translate(0); }
`;

const ShakeText = styled(Typography)(() => ({
    color: 'red',
    animation: `0.5s`,
}));

const Login = () => {
    const navigate = useNavigate();
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [token, setToken] = useState('');
    const [captchaLoaded, setCaptchaLoaded] = useState(false);
    const [resetCaptcha, setResetCaptcha] = useState(0);

    const handleCaptchaSuccess = (captchaToken) => {
        setToken(captchaToken);
        setCaptchaLoaded(true);
    };

    const handleResetCaptcha = () => {
      console.log('Resetting captcha: ', resetCaptcha);
      setToken('');
      setCaptchaLoaded(false);
      setResetCaptcha((prev) => prev + 1);
    };

    const handleCaptchaError = (error) => {
        console.error("Captcha error:", error);
        setError(true);
        setCaptchaLoaded(true);
    };

    const handleCaptchaLoad = () => {
        setCaptchaLoaded(true);
    };

    const handleSubmit = async () => {

        if (!token) {
            console.error('No captcha token available');
            return;
        }

        try {

            setIsSubmitting(true);
            setError(false);

            const body = {
                login,
                password,
                token,
                "siteKey": "ysc1_Z8dzQjm3QK55PUWJk49vKy1Zhv3w8b8bbbiSBzY770f06256",
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
            handleResetCaptcha();

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
            }}>
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
                }}>
                <Typography component="h1" variant="h5">
                    Вход в личный кабинет
                </Typography>
                <Box sx={{ mt: 1, width: '100%' }}>
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
                        disabled={isSubmitting}/>
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
                        disabled={isSubmitting}/>
                    <SmartCaptcha
                        key={resetCaptcha}
                        sitekey="ysc1_Z8dzQjm3QK55PUWJk49vKy1Zhv3w8b8bbbiSBzY770f06256"
                        onJavascriptError={(e) => {
                            console.log(e.filename);
                            console.log(e.message);
                            handleCaptchaError(e);
                        }}
                        onNetworkError={() => {
                                handleCaptchaError("Network error");
                        }}
                        onSuccess={(e) => {
                            handleCaptchaSuccess(e);
                        }}
                        onLoad={handleCaptchaLoad}
                        shieldPosition={"top-left"}
                        visible={true}
                        language='ru'/>
                    {error && (
                        <ShakeText variant="body2" sx={{ mt: 1 }}>
                            Неправильный логин или пароль.
                        </ShakeText>
                    )}
                    <Button
                        onClick={handleSubmit}
                        type="submit"
                        fullWidth
                        variant="contained"
                        color="primary"
                        sx={{ mt: 3, mb: 2 }}
                        disabled={isSubmitting || !captchaLoaded || !token}>
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