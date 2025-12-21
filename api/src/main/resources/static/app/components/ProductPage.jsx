import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { createTheme } from '@mui/material/styles';
import ProductCard from './Card.jsx';
import {
    Container,
    Box,
    Pagination,
    TextField,
    Button,
    Typography,
    styled,
    useMediaQuery,
    AppBar,
    Toolbar,
    Grid,
    IconButton
    } from "@mui/material";
import { checkJWTExpiration } from './checkJWTExpiration.js';
import { useAppContext } from './AppContext.js';
import { useNavigate } from 'react-router-dom';

const ProductPageImage = styled(Box)({
  width: '-webkit-fill-available',
  height: '100%',
  'objectFit': 'cover',
  'objectPosition': 'center',
  'borderRadius': '3%',
  margin: '10px',
});

const theme = createTheme({
    palette: {
      primary: {
        main: '#FFFFFF',
      },
      secondary: {
        main: '#dc004e',
      },
    },
    typography: {
      fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    },
});

const ProductPage = () => {
    const isDesktopResolution               = useMediaQuery('(min-width:992px)');
    const [showToolbar, setShowToolbar]     = useState(false);

    const { id }                            = useParams();

    const [name, setName]                   = useState('');
    const [article, setArticle]             = useState('');
    const [unit, setUnit]                   = useState('');
    const [unitVid, setUnitVid]             = useState('');
    const [packag, setPackag]               = useState(null);
    const [price, setPrice]                 = useState(0.0);
    const [weight, setWeight]               = useState(null);
    const [quantity, setQuantity]           = useState(0);
    const [cartQuantity, setCartQuantity]   = useState(0);
    const [zn, setZn]                       = useState(null);
    const [sku, setSku]                     = useState('');
    const [worker, setWorker]               = useState('');
    const [stroke, setStroke]               = useState('');
    const [country, setCountry]             = useState('');
    const [description, setDescription]     = useState('');
    const [content, setContent]             = useState('');
    const [group, setGroup]                 = useState('');

    const [otherProducts, setOtherProducts] = useState([]);

    const [oldCartQuantity, setOldCartQuantity]= useState(null);
    const { setContextCount }               = useAppContext();
    const navigate                          = useNavigate();

      useEffect(() => {
        const editCart = async () => {
          try {
            checkJWTExpiration();
            const response = await axios.put('/api/cart/add?id=' + id + '&quantity=' + (cartQuantity === '' ? 0 : cartQuantity), null,
            { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
            if (oldCartQuantity < response.data.quantity) {
              setContextCount((c) => c + (response.data.quantity - oldCartQuantity));
            } else {
              setContextCount((c) => c - (oldCartQuantity - response.data.quantity));
            }
          } catch (error) {
            console.error(error);
          }
        };
        if (oldCartQuantity != null && !isNaN(cartQuantity) && quantity != cartQuantity) {
          editCart();
        }
      }, [cartQuantity]);

    useEffect(() => {
        fetchOtherProducts();
        fetchData();
        setCartQuantity(quantity);
    }, [id]);

    const fetchOtherProducts = async () => {
        const response = await axios.get('/api/randProductsByGroup?id=' + id + '&quantity=8',
        { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
        setOtherProducts(response.data);
    };

    const fetchData = async () => {
        try {
            const response = await axios.get('/api/product?id=' + id,
            { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
            setName(response.data.name);
            setArticle(response.data.article);
            setUnit(response.data.unit);
            setUnitVid(response.data.unitVid);
            setPackag(response.data.packag);
            setPrice(response.data.price);
            setWeight(response.data.weight);
            setQuantity(response.data.quantity);
            setCartQuantity(response.data.cartQuantity);
            setZn(response.data.zn);
            setSku(response.data.sku);
            setWorker(response.data.worker);
            setStroke(response.data.stroke);
            setCountry(response.data.country);
            setDescription(response.data.description);
            setContent(response.data.content);
            setGroup(response.data.group);
        } catch (error) {
            console.error("There was an error fetching the data!", error);
        }
    };

    return (
        <div>
            <Container maxWidth="lg" sx={{ mt: 3, display: "flex", gap: "2rem", flexDirection: "column" }}>
                <Container maxWidth="lg" sx={{ display: "flex", gap: "0.5rem", flexDirection: isDesktopResolution ? "row" : "column" }}>
                    <ProductPageImage
                        sx={{ width: isDesktopResolution ? '45%' : '-webkit-fill-available' }}
                        src="/api/getImage/green-grass-cute-cat-hd-de37pmurfb12yl3j.jpg"
                        component="img"
                    />
                    <Container sx={{ display: "flex", gap: "0.5rem", flexDirection: "column", padding: "0px", marginTop: "10px" }}>
                        <Typography sx={{ fontSize: '14px', fontWeight: 400 }}>{group}</Typography>
                        <Typography sx={{ fontSize: '28px', fontWeight: 900 }}>{name}</Typography>
                        <Typography sx={{ fontSize: '14px', fontWeight: 400 }}>Артикул: {article}</Typography>
                        <div>
                            <Typography sx={{ fontSize: '25px', fontWeight: 700 }}>Цена за {unit} {price} ₽</Typography>
                            <Typography sx={{ fontSize: '14px', fontWeight: 400, marginTop: "4px" }}>При заказе в упаковках {price * packag} ₽</Typography>
                            <Typography sx={{ fontSize: '14px', fontWeight: 400 }}>Одна упаковка это {packag} {unit}</Typography>
                        </div>
                        {cartQuantity > 0 ? (
                            <Container sx={{ display: "flex", gap: "0.5rem", flexDirection: "row", paddingTop: "2rem" }}>
                                <Button
                                    color="success"
                                    fullWidth
                                    variant="contained"
                                    onClick={() => navigate("/cart")}
                                >
                                    <Container>
                                        <Typography variant="h6" >
                                            В корзине
                                        </Typography>
                                        <Typography variant="caption" style={{ marginTop: '4px' }} >
                                            Перейти
                                        </Typography>
                                    </Container>
                                </Button>
                                <IconButton onClick={() => {
                                    setOldCartQuantity(cartQuantity);
                                    setCartQuantity((c) => c - 1);
                                    }} color="primary" aria-label="decrement">
                                    -
                                </IconButton>
                                <TextField
                                    type="number"
                                    value={cartQuantity}
                                    onChange={(e) => {
                                        setOldCartQuantity(cartQuantity);
                                        setCartQuantity(e.target.value);
                                    }}
                                    onKeyPress={(event) => {
                                        if (event?.key === '-' || event?.key === ',' || event?.key === '.' || event?.key === '0') {
                                          event.preventDefault();
                                        }
                                    }}
                                    inputProps={{ min: 0, style: { textAlign: 'center' } }}
                                    label="Кол-во"
                                    variant="standard"
                                    size="small"
                                    fullWidth
                                    style={{ margin: '0 8px' }}
                                />
                                <IconButton onClick={() => {
                                    setOldCartQuantity(cartQuantity);
                                    setCartQuantity((c) => c + 1);
                                    }} color="primary" aria-label="increment">
                                    +
                                </IconButton>
                            </Container>
                            ) : (
                                <Button
                                    id="add-to-cart-button"
                                    sx={{fontWeight: 700, fontSize: '18px', marginTop: "2rem"}}
                                    fullWidth
                                    variant="contained"
                                    size="large"
                                    onClick={() => {
                                        setOldCartQuantity(0);
                                        setCartQuantity(1);
                                    }}
                                >
                                    Добавить в корзину
                                </Button>
                            )}
                    </Container>
                </Container>
                <Container maxWidth="lg" sx={{ display: "flex", gap: "1rem", flexDirection: "column" }}>
                    <Typography variant="h6" gutterBottom>
                        О товаре
                    </Typography>
                    <Container maxWidth="lg" sx={{ display: "flex", gap: "1rem", flexDirection: isDesktopResolution ? "row" : "column"}}>
                        <Grid container spacing={1.5} columns={{ xs: 4, md: 8, lg: 8 }}>
                            <Grid size={{ xs: 4, md: 4, lg: 4 }}>
                                <Typography variant="subtitle1" size={{ xs: 4, md: 4, lg: 4 }} gutterBottom>
                                    Описание
                                </Typography>
                                {description && (
                                    <Typography variant="body1" gutterBottom>
                                        {description}
                                    </Typography>
                                )}
                            </Grid>
                            <Grid size={{ xs: 4, md: 4, lg: 4 }}>
                                <Typography variant="subtitle1" gutterBottom>
                                    Состав
                                </Typography>
                                {content && (
                                    <Typography variant="body1" gutterBottom>
                                        {content}
                                    </Typography>
                                )}
                            </Grid>
                            <Grid size={{ xs: 4, md: 4, lg: 4 }}>
                                <Typography variant="subtitle1" gutterBottom>
                                    Характеристики
                                </Typography>
                                {weight && (
                                    <Typography variant="body1" gutterBottom>
                                        Масса ед. товара: {weight} грамм
                                    </Typography>
                                )}
                                {unit && unitVid && (
                                    <Typography variant="body1" gutterBottom>
                                        Единица измерения: {unit} - {unitVid}
                                    </Typography>
                                )}
                                {packag && (
                                    <Typography variant="body1" gutterBottom>
                                        Количество товара в одной упаковке: {packag} {unitVid}
                                    </Typography>
                                )}
                                {country && (
                                    <Typography variant="body1" gutterBottom>
                                        Страна изготовитель: {country}
                                    </Typography>
                                )}
                                {worker && (
                                    <Typography variant="body1" gutterBottom>
                                        Изготовитель: {worker}
                                    </Typography>
                                )}
                                {group && (
                                    <Typography variant="body1" gutterBottom>
                                        Тип: {group}
                                    </Typography>
                                )}
                            </Grid>
                        </Grid>
                    </Container>
                </Container>
            </Container>
            <Container maxWidth="lg" sx={{ mt: 3, display: "flex", gap: "1rem", flexDirection: "column" }}>
                <Typography variant="h6" gutterBottom>
                    Другие товары этого производителя
                </Typography>
                <Grid container spacing={1.5} columns={{ xs: 4, md: 8, lg: 16 }}>
                    {otherProducts.map((item, index) => (
                        <Grid key={index} size={{ xs: 4, md: 4, lg: 4 }}>
                            <ProductCard
                                id={item.id}
                                image="/api/getImage/green-grass-cute-cat-hd-de37pmurfb12yl3j.jpg"
                                name={item.name}
                                price={item.price}
                            />
                        </Grid>
                    ))}
                </Grid>
            </Container>
        </div>
    );
};

export default ProductPage;
