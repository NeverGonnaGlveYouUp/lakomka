import React, { useState, useEffect } from 'react';
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

const ChangePassword = () => {
    const navigate = useNavigate();
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [newPasswordRepeat, setNewPasswordRepeat] = useState('');
    const [error, setError] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [loggedUsername, setLoggedUsername] = useState('');

    // Fetch username when component mounts
    useEffect(() => {
        checkJWTExpiration();
        const fetchUsername = async () => {
            try {
                const response = await axios.get('/api/current-user', {
                    headers: {
                        'Authorization': localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null
                    }
                });
                if (response.data && response.data.userName) {
                    setLoggedUsername(response.data.userName);
                }
            } catch (error) {
                console.error('Error fetching username:', error);
            }
        };
        fetchUsername();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!currentPassword || !newPassword || !newPasswordRepeat) {
            setError(true);
            return;
        }

        setIsSubmitting(true);
        setError(false);

        try {
            const body = {
                currentPassword,
                newPassword,
                newPasswordRepeat,
                token,
                "expectedAction": "CHANGEPASSWORD",
                "siteKey": "6Lf1gPQrAAAAAG_tjJ1Jy4QuHJjKy5uBEZZc0z3y",
            };

            checkJWTExpiration();
            const response = await axios.post('/api/change-password', body, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null
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
                    navigate('/private/profile');
                }, 1000);
            } else {
                throw new Error('No token received');
            }
        } catch (error) {
            console.error('ChangePassword error:', error);
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
                    Изменение пароля для "{loggedUsername}"
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
                        label="Текущий пароль"
                        type="password"
                        onChange={(e) => setCurrentPassword(e.target.value)}
                        autoComplete="password"
                        value={currentPassword}
                        error={error}
                        disabled={isSubmitting}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="Новый пароль"
                        type="password"
                        onChange={(e) => setNewPassword(e.target.value)}
                        autoComplete="password"
                        value={newPassword}
                        error={error}
                        disabled={isSubmitting}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="Повтор нового пароля"
                        type="password"
                        onChange={(e) => setNewPasswordRepeat(e.target.value)}
                        autoComplete="password"
                        value={newPasswordRepeat}
                        error={error}
                        disabled={isSubmitting}
                    />
                    {error && (
                        <ShakeText shake variant="body2" sx={{ mt: 1 }}>
                            Неправильный пароль или Сначала нужно залогиниться или еще что то не так.
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
                        {isSubmitting ? 'Смена пароля...' : 'Сменить пароль'}
                    </Button>
                    <Button
                        fullWidth
                        variant="text"
                        sx={{ mb: 2 }}
                        onClick={() => navigate("/private/profile")}
                        disabled={isSubmitting}
                    >
                        Назад
                    </Button>
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
                    Смена пароля успешна
                </Alert>
            </Snackbar>
        </Container>
    );
}

export default ChangePassword;