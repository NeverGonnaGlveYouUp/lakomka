import React, { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { IMaskInput } from 'react-imask';
import {
    Button,
    TextField,
    Box,
    Typography,
    Container,
    Link,
    Snackbar,
    Alert,
    Stack
} from "@mui/material";
import { styled } from '@mui/material/styles';
import { keyframes } from "@emotion/react";
import axios from 'axios';
import InputMask from 'react-input-mask'
import PropTypes from 'prop-types';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';
import { postReCaptcha } from './postReCaptcha.js';

const PhoneMask = React.forwardRef(function PhoneMask(props, ref) {
    const { onChange, ...other } = props
    return (
        <IMaskInput
            {...other}
            mask='+7(###)###-##-##'
            definitions={{
                '#': /[0-9]/,
            }}
            placeholder={'+7(___)___-__-__'}
            inputRef={ref}
            onAccept={useCallback(value =>
                onChange({ target: { name: props.name, value } })
            )}
            overwrite
        />
    )
})

PhoneMask.propTypes = {
    name: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
}

const InnMask = React.forwardRef(function InnMask(props, ref) {
    const { onChange, ...other } = props
    return (
        <IMaskInput
            {...other}
            mask='####-#####-#'
            definitions={{
                '#': /[0-9]/,
            }}
            placeholder={'____-_____-_'}
            inputRef={ref}
            onAccept={useCallback(value =>
                onChange({ target: { name: props.name, value } })
            )}
            overwrite
        />
    )
})

InnMask.propTypes = {
    name: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
}

const KppMask = React.forwardRef(function KppMask(props, ref) {
    const { onChange, ...other } = props
    return (
        <IMaskInput
            {...other}
            mask='#########'
            definitions={{
                '#': /[0-9]/,
            }}
            inputRef={ref}
            placeholder={'_________'}
            onAccept={useCallback(value =>
                onChange({ target: { name: props.name, value } })
            )}
            overwrite
        />
    )
})

KppMask.propTypes = {
    name: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
}

const OgrnMask = React.forwardRef(function OgrnMask(props, ref) {
    const { onChange, ...other } = props
    return (
        <IMaskInput
            {...other}
            mask='#############'
            definitions={{
                '#': /[0-9]/,
            }}
            inputRef={ref}
            placeholder={'_____________'}
            onAccept={useCallback(value =>
                onChange({ target: { name: props.name, value } })
            )}
            overwrite
        />
    )
})

OgrnMask.propTypes = {
    name: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
}

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

const Signup = () => {
    const navigate                              = useNavigate();
    const [login, setLogin]                     = useState('');
    const [password, setPassword]               = useState('');
    const [repeatPassword, setRepeatPassword]   = useState('');

    const [inn, setInn]                         = useState('');
    const [kpp, setKpp]                         = useState('');
    const [ogrn, setOgrn]                       = useState('');
    const [deliveryAddress, setDeliveryAddress] = useState('');
    const [jurAddress, setJurAddress]           = useState('');
    const [name, setName]                       = useState('');
    const [nameFull, setNameFull]               = useState('');
    const [contact, setContact]                 = useState('');
    const [phone, setPhone]                     = useState('');
    const [dpAgreement, setDpAgreement]         = useState(false);

    const [snackbarOpen, setSnackbarOpen]       = useState(false);
    const [isSubmitting, setIsSubmitting]       = useState(false);

    const [errors, setErrors]                   = useState({});
    const [reCaptchaError, setReCaptchaError]   = useState(false);

    const { executeRecaptcha } = useGoogleReCaptcha();
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        const token = await postReCaptcha(executeRecaptcha, 'SIGNUP');
        const body = {
            login,
            password,
            repeatPassword,
            inn,
            kpp,
            ogrn,
            deliveryAddress,
            jurAddress,
            name,
            nameFull,
            contact,
            phone,
            dpAgreement,
            token,
            "expectedAction": "SIGNUP",
            "siteKey": "6Lf1gPQrAAAAAG_tjJ1Jy4QuHJjKy5uBEZZc0z3y",
        }
        try {
            const response = await axios.post('/api/signup', body, {
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

            let token;
            if (typeof response.data === 'string') {
                token = response.data;
            } else if (response.data.token) {
                token = response.data.token;
            } else {
                token = response.data;
            }

            if (token) {
                localStorage.setItem('jwtToken', token);
                setSnackbarOpen(true);
                setTimeout(() => {
                    navigate('/');
                }, 1000);
            } else {
                throw new Error('No token received');
            }

        } catch (error) {
            if (error.response && error.response.status === 400) {
                const validationErrors = error.response.data;

                const formattedErrors = validationErrors.reduce((acc, error) => {
                    acc[error.field] = error.defaultMessage;
                    return acc;
                }, {});

                setErrors(formattedErrors);
                setIsSubmitting(false);
            } else {
                console.error("An unexpected error occurred:", error);
            }
        }
    }

    const handleSnackbarClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setSnackbarOpen(false);
    };

    const handleAlertClose = () => {
        setSnackbarOpen(false);
    };

    return(
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
                    maxWidth: 469,
                    width: '100%'
                }}
            >
                <Typography component="h1" variant="h5">
                    Регистрация
                </Typography>
                <Alert severity="info">
                    <Typography sx={{ fontSize: "16px", lineHeight: "19px", marginBottom: "1rem" }}>
                        {"После регистрации вам будет необходимо заключить "}
                        <Link onClick={() => navigate("/info#dogovor")}>
                            договор
                        </Link>
                        {"."}
                    </Typography>
                </Alert>
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
                        error={!!errors.login}
                        helperText={errors.login}
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
                        error={!!errors.password}
                        helperText={errors.password}
                        disabled={isSubmitting}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="Повторите пароль"
                        type="password"
                        onChange={(e) => setRepeatPassword(e.target.value)}
                        autoComplete="current-password"
                        value={repeatPassword}
                        error={!!errors.repeatPassword}
                        helperText={errors.repeatPassword}
                        disabled={isSubmitting}
                    />
                    <Box>
                        <Stack
                            spacing={{ xs: 1, sm: 2 }}
                            direction="row"
                            useFlexGap
                            sx={{ flexWrap: 'wrap' }}
                        >
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                label="ИНН"
                                onChange={(e) => setInn(e.target.value)}
                                value={inn}
                                error={!!errors.inn}
                                helperText={errors.inn}
                                disabled={isSubmitting}
                                InputProps={{
                                        inputComponent: InnMask,
                                    }}
                            />
                            <TextField
                                margin="normal"
                                sx={{ marginTop: "0px" }}
                                required
                                label="ОГРН"
                                onChange={(e) => setOgrn(e.target.value)}
                                value={ogrn}
                                error={!!errors.ogrn}
                                helperText={errors.ogrn}
                                disabled={isSubmitting}
                                InputProps={{
                                        inputComponent: OgrnMask,
                                    }}
                            />
                            <TextField
                                margin="normal"
                                sx={{ marginTop: "0px" }}
                                required
                                label="КПП"
                                onChange={(e) => setKpp(e.target.value)}
                                value={kpp}
                                error={!!errors.kpp}
                                helperText={errors.kpp}
                                disabled={isSubmitting}
                                InputProps={{
                                        inputComponent: KppMask,
                                    }}
                            />
                        </Stack>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Краткое название"
                            onChange={(e) => setName(e.target.value)}
                            value={name}
                            error={!!errors.name}
                            helperText={errors.name}
                            disabled={isSubmitting}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Полное название"
                            onChange={(e) => setNameFull(e.target.value)}
                            value={nameFull}
                            error={!!errors.nameFull}
                            helperText={errors.nameFull}
                            disabled={isSubmitting}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="ФИО контактного лица"
                            onChange={(e) => setContact(e.target.value)}
                            value={contact}
                            error={!!errors.contact}
                            helperText={errors.contact}
                            disabled={isSubmitting}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Телефон контактного лица"
                            onChange={(e) => setPhone(e.target.value)}
                            value={phone}
                            error={!!errors.phone}
                            helperText={errors.phone}
                            disabled={isSubmitting}
                            InputProps={{
                                    inputComponent: PhoneMask,
                                }}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Адрес доставки"
                            onChange={(e) => setDeliveryAddress(e.target.value)}
                            value={deliveryAddress}
                            error={!!errors.deliveryAddress}
                            helperText={errors.deliveryAddress}
                            disabled={isSubmitting}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Юридический адрес"
                            onChange={(e) => setJurAddress(e.target.value)}
                            value={jurAddress}
                            error={!!errors.jurAddress}
                            helperText={errors.jurAddress}
                            disabled={isSubmitting}
                        />
                    </Box>
                    {reCaptchaError && (
                        <ShakeText shake variant="body2" sx={{ mt: 1 }}>
                            Ошибка reCaptcha v3.
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
                        {isSubmitting ? 'Регистрация...' : 'Зарегистрироваться'}
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
                        {"Уже есть аккаунт? "}
                        <Link onClick={() => navigate("/auth/login")} variant="body2">
                            Вход в аккаунт
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
                    Регистрация успешна
                </Alert>
            </Snackbar>
        </Container>
    );
}

export default Signup;