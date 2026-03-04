import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import {
    Container,
    Typography,
    List,
    ListItem,
    ListItemText,
    ListItemButton,
    ListSubheader,
    ListItemIcon,
    Divider,
    Alert,
    Link,
    Paper,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions,
    Box,
    Stack,
    TextField,
    Card,
    CardHeader,
    CardContent,
    Grid,
    Chip,
    Avatar,
    useTheme,
    useMediaQuery,
    Tooltip,
    Drawer,
    IconButton,
    Toolbar
    } from '@mui/material';
import { checkJWTExpiration } from './checkJWTExpiration.js';
import { FaSignOutAlt } from "react-icons/fa";
import { IoMenu } from "react-icons/io5";
import { useNavigate, useHistory } from 'react-router-dom';
import { PiPasswordDuotone } from "react-icons/pi";
import { keyframes } from "@emotion/react";
import InputMask from 'react-input-mask'
import PropTypes from 'prop-types';
import { SmartCaptcha } from '@yandex/smart-captcha';
import { CAPTCHA_SITEKEY } from './constants.js';
import { IMaskInput } from 'react-imask';

const drawerWidth = 280;

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

const AddBranchDialog = ({ open, onClose }) => {

    // Captcha & token
    const [captchaLoaded, setCaptchaLoaded]     = useState(false);
    const [resetCaptcha, setResetCaptcha]       = useState(0);
    const [token, setToken]                     = useState('');
    const navigate                              = useNavigate();
    const [errors, setErrors]                   = useState({});
    const [isSubmitting, setIsSubmitting]       = useState(false);

    // Form fields
    const [inn, setInn] = useState('');
    const [ogrn, setOgrn] = useState('');
    const [kpp, setKpp] = useState('');
    const [name, setName] = useState('');
    const [nameFull, setNameFull] = useState('');
    const [contact, setContact] = useState('');
    const [phone, setPhone] = useState('');
    const [deliveryAddress, setDeliveryAddress] = useState('');
    const [jurAddress, setJurAddress] = useState('');

    const handleCaptchaSuccess = (captchaToken) => {
        setToken(captchaToken);
        setCaptchaLoaded(true);
    };

    const handleResetCaptcha = () => {
      setToken('');
      setCaptchaLoaded(false);
      setResetCaptcha((prev) => prev + 1);
    };

    const handleCaptchaError = (error) => {
        console.error("Captcha error:", error);
        setCaptchaLoaded(true);
    };

    const handleCaptchaLoad = () => {
        setCaptchaLoaded(true);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!token) {
            console.error('No captcha token available');
            return;
        }

        setIsSubmitting(true);

        try {
            // Build JSON payload from component state
            const formJson = {
                inn,
                kpp,
                ogrn,
                deliveryAddress,
                jurAddress,
                name,
                nameFull,
                contact,
                phone,
                dpAgreement: true,
                token,
                expectedAction: "CREATE-JPERSON",
                siteKey: CAPTCHA_SITEKEY
            };

            const response = await axios.post('/api/jpersons/create-j-person', formJson, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null
                }});
            // success
            setIsSubmitting(false);
            onClose();
            navigate(0);
        } catch (error) {
            setIsSubmitting(false);
            if (error.response && error.response.status === 400) {
                const validationErrors = error.response.data;

                const formattedErrors = validationErrors.reduce((acc, err) => {
                    acc[err.field] = err.defaultMessage;
                    return acc;
                }, {});

                setErrors(formattedErrors);
                handleResetCaptcha();
            } else {
                console.error("An unexpected error occurred:", error);
            }
        }
    };

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Новый филиал</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    Пожалуйста, введите некоторые данные филиала и контактные данные, позже по ним с вами свяжутся для заключения договора.
                </DialogContentText>
                <form onSubmit={handleSubmit} id="branch-addition-form">
                    <Box>
                        <SmartCaptcha
                            key={resetCaptcha}
                            sitekey={CAPTCHA_SITEKEY}
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
                                name="inn"
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
                                name="ogrn"
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
                                name="kpp"
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
                            label="Краткое название Юр. Лица"
                            name="name"
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
                            label="Полное название Юр. Лица"
                            name="nameFull"
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
                            name="contact"
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
                            name="phone"
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
                            name="deliveryAddress"
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
                            name="jurAddress"
                            onChange={(e) => setJurAddress(e.target.value)}
                            value={jurAddress}
                            error={!!errors.jurAddress}
                            helperText={errors.jurAddress}
                            disabled={isSubmitting}
                        />
                    </Box>
                </form>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={isSubmitting}>Закрыть</Button>
                <Button type="submit" form="branch-addition-form" disabled={isSubmitting}>
                    Готово
                </Button>
            </DialogActions>
        </Dialog>
    )
}

const JPersonItem = (props) => {
    const data = props.data || props;

    const theme = useTheme();
    const isSmUp = useMediaQuery(theme.breakpoints.up('sm'));

    const title = data.addressDelivery || `Филиал ${data.kpp || ''}`;
    const sub = !!data.kpp && `КПП: ${data.kpp}`;

    const fields = [
        { key: 'rest', label: 'Долг, ₽', value: data.rest },
        { key: 'restTime', label: 'Старый долг, ₽', value: data.restTime },
        { key: 'nameFull', label: 'Полное наименование', value: data.nameFull },
        { key: 'name', label: 'Краткое наименование', value: data.name },
        { key: 'address', label: 'Юр. адрес', value: data.address },
        { key: 'addressDelivery', label: 'Адрес доставки', value: data.addressDelivery },
        { key: 'mapDelivery', label: 'Описание места доставки', value: data.mapDelivery },
        { key: 'route', label: 'Дни поставок', value: data.route },
        { key: 'contact', label: 'Контакт', value: data.contact },
        { key: 'post', label: 'Должность', value: data.post },
        { key: 'phone', label: 'Телефон', value: data.phone },
        { key: 'email', label: 'Email', value: data.email },
        { key: 'INN', label: 'ИНН', value: data.INN },
        { key: 'OGRN', label: 'ОГРН', value: data.OGRN },
        { key: 'KPP', label: 'КПП', value: data.KPP },
    ].filter(f => typeof f.value !== 'undefined' && f.value !== null && String(f.value).trim() !== '');

    const columns = isSmUp ? 2 : 1;
    const itemsPerColumn = Math.ceil(fields.length / columns);

    return (
        <Card elevation={1} sx={{ mb: 2, borderRadius: 2, padding: "1rem" }}>
            <CardHeader
                avatar={
                    <Avatar sx={{ bgcolor: 'primary.main', color: 'white' }}>
                        {data.name ? data.name.charAt(0).toUpperCase() : (data.nameFull ? data.nameFull.charAt(0).toUpperCase() : 'Ф')}
                    </Avatar>
                }
                title={
                    <Typography sx={{ fontWeight: 700, fontSize: 16 }}>
                        {title}
                    </Typography>
                }
                subheader={
                    <Stack direction="row" spacing={1} alignItems="center" sx={{ mt: 0.5 }}>
                        {sub && <Chip label={sub} size="small" color="secondary" />}
                        {data.email && <Chip label="Email" size="small" variant="outlined" />}
                        {data.phone && <Chip label="Телефон" size="small" variant="outlined" />}
                    </Stack>
                }
                sx={{ pb: 0 }}
            />
            <CardContent sx={{ pt: 1 }}>
                <Grid container spacing={1}>
                    {fields.map((f, idx) => (
                        <Grid item xs={12} sm={6} key={f.key + '-' + idx}>
                            <Box sx={{
                                px: 1,
                                py: 0.5,
                                borderRadius: 1,
                                backgroundColor: 'transparent',
                                display: 'flex',
                                flexDirection: 'column'
                            }}>
                                <Typography variant="caption" color="text.secondary" sx={{ fontSize: 12 }}>
                                    {f.label}
                                </Typography>
                                <Typography variant="body2" sx={{ fontWeight: 500, wordBreak: 'break-word' }}>
                                    {f.value}
                                </Typography>
                            </Box>
                        </Grid>
                    ))}
                </Grid>
            </CardContent>
        </Card>
    )
}

const ProfilePage = () => {

    const [data, setData]               = useState({});
    const navigate                      = useNavigate();

    const [dialogOpen, setDialogOpen] = useState(false);

    const theme = useTheme();
    const isMdUp = useMediaQuery(theme.breakpoints.up('mg'));
    const [mobileOpen, setMobileOpen] = useState(false);

    const toggleDrawer = (open) => (event) => {
        if (event && event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
            return;
        }
        setMobileOpen(open);
    };

    useEffect(() => {
        checkJWTExpiration();
        const fetchUsername = async () => {
            try {
                const response = await axios.get('/api/current-user/model', {
                    headers: {
                        'Authorization': localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null
                    }
                });
                if (response.data) {
                    setData(response.data);
                }
            } catch (error) {
                navigate("/error");
            }
        };
        fetchUsername();
    }, []);

    const sideMenu = (
        <Box sx={{ width: { xs: drawerWidth }, p: 1 }}>
            <List
                sx={{ width: '100%', bgcolor: 'background.paper' }}
                component="nav">
                <ListSubheader sx={{ fontWeight: "900" }}>Заказы</ListSubheader>
                <ListItemButton onClick={() => { if (!isMdUp) setMobileOpen(false); navigate("/cart"); }}>
                    <ListItemText primary="Моя корзина" />
                </ListItemButton>
                <ListItemButton onClick={() => { if (!isMdUp) setMobileOpen(false); navigate("/private/orders"); }}>
                    <ListItemText primary="Мои заказы" />
                </ListItemButton>
                <Divider sx={{ my: 1 }} />
                <ListSubheader sx={{ fontWeight: "900" }}>Управление профилем</ListSubheader>
                <ListItemButton onClick={() => { if (!isMdUp) setMobileOpen(false); navigate("/private/change-password"); }}>
                    <ListItemText primary="Сменить пароль" />
                </ListItemButton>
                <ListItemButton onClick={() => {
                        localStorage.removeItem('jwtToken');
                        if (!isMdUp) setMobileOpen(false);
                        navigate("/");
                    }}>
                    <ListItemText primary="Выйти" />
                    <ListItemIcon>
                        <FaSignOutAlt />
                    </ListItemIcon>
                </ListItemButton>
                <Divider sx={{ my: 1 }} />
                <ListSubheader sx={{ fontWeight: "900" }}>Информация</ListSubheader>
                <ListItemButton onClick={() => { if (!isMdUp) setMobileOpen(false); navigate("/info#redac_profile"); }}>
                    <ListItemText primary="Как редактировать профиль?" />
                </ListItemButton>
            </List>
        </Box>
    );

    return(
        <Box sx={{ display: 'flex' }}>
            <Drawer
                variant={isMdUp ? "permanent" : "temporary"}
                open={isMdUp ? true : mobileOpen}
                onClose={toggleDrawer(false)}
                ModalProps={{
                    keepMounted: true, // Better open performance on mobile.
                }}
                sx={{
                    '& .MuiDrawer-paper': {
                        width: drawerWidth,
                        boxSizing: 'border-box',
                    },
                }}
            >
                <Toolbar />
                {sideMenu}
            </Drawer>

            <Box component="main" sx={{
                flexGrow: 1,
                width: { md: `calc(100% - ${drawerWidth}px)` },
                ml: { md: `${drawerWidth}px` },
                p: 2
            }}>
                <Container maxWidth="lg" sx={{ mt: 1, display: "flex", flexDirection: "column", gap: "1rem" }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            {!isMdUp && (
                                <IconButton
                                    color="inherit"
                                    aria-label="open drawer"
                                    edge="start"
                                    onClick={toggleDrawer(true)}
                                    sx={{ mr: 1 }}
                                >
                                    <IoMenu />
                                </IconButton>
                            )}
                            <Typography variant="h6" sx={{ fontWeight: 700 }} >
                                Профиль
                            </Typography>
                        </Box>
                        <Typography sx={{ textAlign: "end", fontSize: "14px", fontWeight: 400}}>
                            Логин: {data.userName}
                        </Typography>
                    </Box>

                    <Paper sx={{ p: 2 }}>
                        <Typography
                            sx={{
                                fontSize: { lg: '20px', md: '28px' },
                                fontWeight: 700,
                                mt: "1rem",
                                ml: "1rem"}}>
                            Ваши филиалы
                        </Typography>
                        <Typography
                            sx={{
                                padding: "1rem",
                                margin: "1rem",
                                fontSize: { lg: '14px', md: '18px' },
                                borderRadius: "16px",
                                fontWeight: 400,
                                backgroundColor: "#f1f4f9"
                                }}>
                            {"Вы можете добавить новый филиал для поставок, оставив заявку на заключение "}
                            <Link onClick={() => navigate("/info#dogovor")}>
                                договора
                            </Link>
                            {"."}
                        </Typography>
                        <Button
                            fullWidth
                            variant="contained"
                            color="primary"
                            sx={{ maxWidth: "max-content", mb: "1rem", ml: "1rem" }}
                            onClick={() => setDialogOpen(true)}>
                            Добавить филиал
                        </Button>
                        <Box sx={{ mt: 1 }}>
                            <Grid container>
                                {(data.jPersons || []).length === 0 && (
                                    <Grid item xs={12}>
                                        <Typography color="text.secondary" sx={{ px: 1 }}>
                                            У вас пока нет добавленных филиалов.
                                        </Typography>
                                    </Grid>
                                )}
                                {(data.jPersons || []).map((person, index) => (
                                    <Grid item key={index} xs={12} sm={6} md={4}>
                                        <JPersonItem data={person} />
                                    </Grid>
                                ))}
                            </Grid>
                        </Box>
                    </Paper>
                </Container>
            </Box>

            <AddBranchDialog open={dialogOpen} onClose={() => setDialogOpen(false)} />
        </Box>
    );
}

export default ProfilePage;