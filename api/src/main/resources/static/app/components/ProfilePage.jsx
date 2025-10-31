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
    ListItemIcon
    } from '@mui/material';
import { checkJWTExpiration } from './checkJWTExpiration.js';
import { CiLogout } from "react-icons/ci";
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
                component="nav"
                aria-labelledby="nested-list-subheader"
                subheader={
                  <ListSubheader component="div" id="nested-list-subheader">
                    Управление профилем
                  </ListSubheader>
                }>
                <ListItemButton>
                    <ListItemIcon>
                        <PiPasswordDuotone />
                    </ListItemIcon>
                    <ListItemText primary="Сменить пароль"
                        onClick={ () => navigate("/private/change-password") }/>
                </ListItemButton>
                <ListItemButton>
                    <ListItemIcon>
                        <CiLogout />
                    </ListItemIcon>
                    <ListItemText primary="Выйти"
                        onClick={ () => {
                            localStorage.removeItem('jwtToken');
                            navigate("/");
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