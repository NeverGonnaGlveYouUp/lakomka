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

const Signup = () => {
    const navigate                              = useNavigate();
    const [login, setLogin]                     = useState('');
    const [password, setPassword]               = useState('');
    const [repeatPassword, setRepeatPassword]   = useState('');

//     в дто
    const [inn, setInn]                         = useState('');
    const [kpp, setKpp]                         = useState('');
    const [ogrn, setOgrn]                       = useState('');
    const [deliveryAddress, setDeliveryAddress] = useState('');
    const [jurAddress, setJurAddress]           = useState('');
    const [name, setName]                       = useState('');
    const [nameFull, setNameFull]               = useState('');
    const [contact, setContact]                 = useState('');
    const [phone, setPhone]                     = useState('');

    const [error, setError]                     = useState(false);
    const [snackbarOpen, setSnackbarOpen]       = useState(false);
    const [isSubmitting, setIsSubmitting]       = useState(false);



    const handleBackClick = () => {
        navigate('/');
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
                <Box
                    component="form"
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
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="Повторите пароль"
                        type="password"
                        onChange={(e) => setRepeatPassword(e.target.value)}
                        autoComplete="current-password"
                        value={repeatPassword}
                        error={error}
                        disabled={isSubmitting}
                    />
                    {error && (
                        <ShakeText shake variant="body2" sx={{ mt: 1 }}>
                            Неправильный логин или пароль.
                        </ShakeText>
                    )}
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
                                error={error}
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
                                error={error}
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
                                error={error}
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
                            label="ФИО контактного лица"
                            onChange={(e) => setContact(e.target.value)}
                            value={contact}
                            error={error}
                            disabled={isSubmitting}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Телефон контактного лица"
                            onChange={(e) => setPhone(e.target.value)}
                            value={phone}
                            error={error}
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
                            error={error}
                            disabled={isSubmitting}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Юридический адрес"
                            onChange={(e) => setJurAddress(e.target.value)}
                            value={jurAddress}
                            error={error}
                            disabled={isSubmitting}
                        />
                    </Box>
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
                        onClick={handleBackClick}
                        disabled={isSubmitting}
                    >
                        Назад
                    </Button>
                    <Typography variant="body2" align="center">
                        {"Уже есть аккаунт? "}
                        <Link href="/login" variant="body2">
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