import React, { useEffect, useState } from 'react';
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
    Link
    } from '@mui/material';
import { checkJWTExpiration } from './checkJWTExpiration.js';
import { FaSignOutAlt } from "react-icons/fa";
import { useNavigate } from 'react-router-dom';
import { PiPasswordDuotone } from "react-icons/pi";

const ProfilePage = () => {

    const [data, setData]               = useState({});
    const navigate                      = useNavigate();

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

    return(
        <Container maxWidth="lg" sx={{ mt: 3, display: "flex", flexDirection: "row", gap: "2rem" }}>
            <List
                sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }}
                component="nav">
                <ListSubheader sx={{ fontWeight: "900" }}>Заказы</ListSubheader>
                <ListItemButton>
                    <ListItemText primary="Моя корзина"
                        onClick={ () => navigate("/cart") }/>
                </ListItemButton>
                <ListItemButton>
                    <ListItemText primary="Мои заказы"
                        onClick={ () => navigate("/private/orders") }/>
                </ListItemButton>
                <ListSubheader sx={{ fontWeight: "900" }}>Управление профилем</ListSubheader>
                <ListItemButton>
                    <ListItemText primary="Сменить пароль"
                        onClick={ () => navigate("/private/change-password") }/>
                </ListItemButton>
                <ListItemButton>
                    <ListItemText primary="Выйти"
                        onClick={ () => {
                            localStorage.removeItem('jwtToken');
                            navigate("/");
                            }}/>
                </ListItemButton>
                <ListSubheader sx={{ fontWeight: "900" }}>Информация</ListSubheader>
                <ListItemButton>
                    <ListItemText primary="Как редактировать профиль?"
                        onClick={ () => {
                            navigate("/info#redac_profile");
                            }}/>
                </ListItemButton>
            </List>
            <Container sx={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
                <Typography variant="h6" sx={{ textAlign: "end", fontSize: "20px", lineHeight: "20px", fontWeight: 400}}>
                    Логин: {data.userName}
                </Typography>
                <Typography variant="h6" sx={{fontSize: "20px", lineHeight: "20px", fontWeight: 400}}>
                    Информация о профиле
                </Typography>
                <List>
                    {data.route === null && (
                        <ListItem>
                            <Alert severity="warning">
                                <Typography sx={{ fontSize: "16px", lineHeight: "19px", marginBottom: "1rem" }}>
                                    {"Чтобы начать покупки вам необходимо заключить "}
                                    <Link onClick={() => navigate("/info#dogovor")}>
                                        договор
                                    </Link>
                                    {"."}
                                </Typography>
                            </Alert>
                        </ListItem>
                    )}
                    {typeof data.rest !== 'undefined' && (
                        <ListItem>
                            <ListItemText primary="Долг в рублях" secondary={data.rest} />
                        </ListItem>
                    )}
                    {typeof data.restTime !== 'undefined' && (
                        <ListItem>
                            <ListItemText primary="Старый долг в рублях" secondary={data.restTime} />
                        </ListItem>
                    )}
                    {data.nameFull && (
                        <ListItem>
                            <ListItemText primary="Полное наименование" secondary={data.nameFull} />
                        </ListItem>
                    )}
                    {data.name && (
                        <ListItem>
                            <ListItemText primary="Краткое наименование" secondary={data.name} />
                        </ListItem>
                    )}
                    {data.address && (
                        <ListItem>
                            <ListItemText primary="Юр. Адрес" secondary={data.address} />
                        </ListItem>
                    )}
                    {data.addressDelivery && (
                        <ListItem>
                            <ListItemText primary="Адрес доставки" secondary={data.addressDelivery} />
                        </ListItem>
                    )}
                    {data.mapDelivery && (
                        <ListItem>
                            <ListItemText primary="Описание места доставки" secondary={data.mapDelivery} />
                        </ListItem>
                    )}
                    {data.route && (
                        <ListItem>
                            <ListItemText primary="Дни поставок" secondary={data.route} />
                        </ListItem>
                    )}
                    {data.contact && (
                        <ListItem>
                            <ListItemText primary="ФИО контактного лица" secondary={data.contact} />
                        </ListItem>
                    )}
                    {data.post && (
                        <ListItem>
                            <ListItemText primary="Должность контактного лица" secondary={data.post} />
                        </ListItem>
                    )}
                    {data.phone && (
                        <ListItem>
                            <ListItemText primary="Телефон контактного лица" secondary={data.phone} />
                        </ListItem>
                    )}
                    {data.email && (
                        <ListItem>
                            <ListItemText primary="Email контактного лица" secondary={data.email} />
                        </ListItem>
                    )}
                    {data.INN && (
                        <ListItem>
                            <ListItemText primary="ИНН" secondary={data.INN} />
                        </ListItem>
                    )}
                    {data.OGRN && (
                        <ListItem>
                            <ListItemText primary="ОГРН" secondary={data.OGRN} />
                        </ListItem>
                    )}
                    {data.KPP && (
                        <ListItem>
                            <ListItemText primary="КПП" secondary={data.KPP} />
                        </ListItem>
                    )}
                </List>
            </Container>
        </Container>
    );
}

export default ProfilePage;