import React, { useEffect } from 'react';
import {
    Container,
    Typography,
    List,
    ListItemText,
    ListItem,
    Divider,
    Box,
} from '@mui/material';
import { useLocation } from 'react-router-dom';

const GeneralInfoPage = () => {

    const { hash } = useLocation();

    useEffect(() => {
        if (hash) {
            const element = document.getElementById(hash.replace('#', ''));
            if (element) {
                element.scrollIntoView({ behavior: 'smooth' });
            }
        }
    }, [hash]);

  return (
      <Container maxWidth="lg" sx={{ mt: 3, display: "flex", flexDirection: "column", gap: "2rem" }}>
          <Typography sx={{ margin: "10px 0 12px", lineHeight: "44px", fontSize: '44px', fontWeight: 700 }}>
              Информация
          </Typography>
          <Container id="dogovor">
                <Typography variant="h5" gutterBottom>
                  • Перед первым заказом
                </Typography>

                <Typography variant="body1" paragraph>
                  Чтобы сделать свой первый заказ необходимо зарегистрироваться и ожидать звонка из бухгалтерии для подписания договора.
                  В договоре будут указанны дни поставок, а также можно изменить данные введенные при регистрации.
                </Typography>
          </Container>
          <Container id="person_data">
                <Typography variant="h5" gutterBottom>
                  • Обработка персональных данных
                </Typography>

                <Typography variant="body1" paragraph>
                  В связи с п. 5 ч. 1 ст. 6 Закона № 152-ФЗ заключение согласия на обработку персональных данных не нужно, если данные используются в рамках договора.
                  Также не обязательно включать такое согласие в текст договора.
                </Typography>
          </Container>
          <Container id="redac_profile">
                <Typography variant="h5" gutterBottom>
                  • Инструкции по редактированию профиля
                </Typography>

                <Typography variant="body1" paragraph>
                  Чтобы редактировать свой профиль, необходимо обратиться в офис бухгалтерии.
                  Вы можете сделать это несколькими способами:
                </Typography>

                <Typography variant="h6">Контактные данные</Typography>

                <List>
                  <ListItem>
                    <ListItemText
                      primary="Электронная почта"
                      secondary={<b>Lakomka.buh@ya.ru</b>}
                    />
                  </ListItem>
                  <ListItem>
                    <ListItemText
                      primary="Телефон"
                      secondary={<b>+73822-690-004</b>}
                    />
                  </ListItem>
                </List>

                <Divider />

                <Typography variant="h6" gutterBottom>
                  Подготовка к обращению
                </Typography>

                <List>
                  <ListItem>
                    <ListItemText
                      primary="Соберите необходимые данные"
                      secondary="Подготовьте информацию, которую вы хотите изменить или дополнить."
                    />
                  </ListItem>
                  <ListItem>
                    <ListItemText
                      primary="Укажите свои данные"
                      secondary="Обязательно укажите свои ФИО и старые контактные данные."
                    />
                  </ListItem>
                </List>

                <Box mt={2}>
                  <Typography variant="body1">
                    После обращения ожидайте ответа от бухгалтерии.
                  </Typography>
                </Box>
          </Container>
      </Container>
  );
};

export default GeneralInfoPage;
