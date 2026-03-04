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
                    <ProductPageImage sx={{
                        width: isDesktopResolution ? '45%' : '-webkit-fill-available',
                        marginBlockEnd: 'auto',
                        marginTop: '12px'}}
                        src="/api/getImage/green-grass-cute-cat-hd-de37pmurfb12yl3j.jpg"
                        component="img"
                    />
                    <Container sx={{ display: "flex", gap: "0.5rem", flexDirection: "column", padding: "0px", marginTop: "10px" }}>
                        <Typography sx={{ fontSize: { lg: '18px', md: '22px' }, fontWeight: 400 }}>Артикул: {article}</Typography>
                        <Typography sx={{ fontSize: { lg: '18px', md: '22px' }, fontWeight: 400 }}>{group}</Typography>
                        <Typography sx={{ fontSize: { lg: '28px', md: '32px' }, fontWeight: 900, textTransform: "capitalize" }}>{name}</Typography>
                        <div>
                            <Typography sx={{ fontSize: { lg: '24px', md: '28px' }, fontWeight: 700 }}>Цена за {unit} {price} ₽</Typography>
                            <Typography sx={{ fontSize: { lg: '18px', md: '22px' }, fontWeight: 400, marginTop: "4px" }}>При заказе в упаковках {price * packag} ₽ за шт.</Typography>
                            <Typography sx={{ fontSize: { lg: '18px', md: '22px' }, fontWeight: 400 }}>Масса в упаковках {packag} {unit} за шт.</Typography>
                        </div>
                        {cartQuantity > 0 ? (
                            <Container sx={{ display: "flex", gap: "0.5rem", flexDirection: "row", pt: "2rem" }}>
                                <Button
                                    color="success"
                                    fullWidth
                                    variant="contained"
                                    onClick={() => navigate("/cart")}>
                                    <Typography variant="h6" sx={{ fontSize: { lg: '18px', md: '22px' } }}>
                                        К корзине
                                    </Typography>
                                </Button>
                                <IconButton onClick={() => {
                                    setOldCartQuantity(cartQuantity);
                                    setCartQuantity((c) => c - 1);
                                    }} color="primary" aria-label="decrement"
                                    sx={{fontWeight: 700, fontSize: "-webkit-xxx-large" }}>
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
                                        if (event?.key === '-' || event?.key === ',' || event?.key === '.') {
                                          event.preventDefault();
                                        }
                                    }}
                                    inputProps={{ min: 0, style: { textAlign: 'center' } }}
                                    label="Кол-во"
                                    variant="standard"
                                    size="small"
                                    sx={{fontWeight: 700, fontSize: "-webkit-xxx-large", margin: '0 8px' }}
                                    fullWidth/>
                                <IconButton onClick={() => {
                                    setOldCartQuantity(cartQuantity);
                                    setCartQuantity((c) => c + 1);
                                    }} color="primary" aria-label="increment"
                                    sx={{fontWeight: 700, fontSize: "-webkit-xxx-large" }}>
                                    +
                                </IconButton>
                            </Container>
                            ) : (
                                <Button
                                    id="add-to-cart-button"
                                    sx={{fontWeight: 700, fontSize: { lg: '18px', md: '22px' }, marginTop: "2rem"}}
                                    fullWidth
                                    variant="contained"
                                    size="large"
                                    onClick={() => {
                                        setOldCartQuantity(0);
                                        setCartQuantity(1);
                                    }}>
                                    Добавить в корзину
                                </Button>
                            )}
                    </Container>
                </Container>
                <Container maxWidth="lg" sx={{ display: "flex", gap: "1rem", flexDirection: "column" }}>
                    <Typography sx={{ fontSize: { lg: '24px', md: '28px' }, fontWeight: 700 }} gutterBottom>
                        О товаре
                    </Typography>
                    <Container maxWidth="lg" sx={{ display: "flex", gap: "1rem", flexDirection: isDesktopResolution ? "row" : "column"}}>
                        <Grid container spacing={1.5} columns={{ xs: 4, md: 8, lg: 8 }}>
                            <Grid size={{ xs: 4, md: 4, lg: 4 }} sx={{ minWidth: "-webkit-fill-available" }}>
                                {description && (
                                    <div>
                                        <Typography sx={{ fontSize: { lg: '20px', md: '28px' }, fontWeight: 700 }} gutterBottom>
                                            Описание
                                        </Typography>
                                        <Typography variant="body1" sx={{ fontSize: { lg: '16px', md: '24px' } }} gutterBottom>
                                            {description}
                                        </Typography>
                                    </div>
                                )}
                            </Grid>
                            <Grid size={{ xs: 4, md: 4, lg: 4 }} sx={{ minWidth: "-webkit-fill-available" }}>
                                {content && (
                                    <div>
                                        <Typography sx={{ fontSize: { lg: '20px', md: '28px' }, fontWeight: 700 }} gutterBottom>
                                            Состав
                                        </Typography>
                                        <Typography variant="body1" sx={{ fontSize: { lg: '16px', md: '24px' } }} gutterBottom>
                                            {content}
                                        </Typography>
                                    </div>
                                )}
                            </Grid>
                            <Grid size={{ xs: 4, md: 4, lg: 4 }} sx={{ minWidth: "-webkit-fill-available" }}>
                                {(weight || unit || unitVid || packag || country || worker || group) && (
                                    <Typography sx={{ fontSize: { lg: '20px', md: '28px' }, fontWeight: 700 }} gutterBottom>
                                        Характеристики
                                    </Typography>
                                )}
                                {weight && (
                                    <Typography variant="body1" sx={{ fontSize: { lg: '16px', md: '24px' } }} gutterBottom>
                                        Масса ед. товара: {weight} грамм
                                    </Typography>
                                )}
                                {unit && unitVid && (
                                    <Typography variant="body1" sx={{ fontSize: { lg: '16px', md: '24px' } }} gutterBottom>
                                        Единица измерения: {unit} - {unitVid}
                                    </Typography>
                                )}
                                {packag && (
                                    <Typography variant="body1" sx={{ fontSize: { lg: '16px', md: '24px' } }} gutterBottom>
                                        Количество товара в одной упаковке: {packag} {unitVid}.
                                    </Typography>
                                )}
                                {country && (
                                    <Typography variant="body1" sx={{ fontSize: { lg: '16px', md: '24px' } }} gutterBottom>
                                        Страна изготовитель: {country}
                                    </Typography>
                                )}
                                {worker && (
                                    <Typography variant="body1" sx={{ fontSize: { lg: '16px', md: '24px' } }} gutterBottom>
                                        Изготовитель: {worker}
                                    </Typography>
                                )}
                                {group && (
                                    <Typography variant="body1" sx={{ fontSize: { lg: '16px', md: '24px' } }} gutterBottom>
                                        Категория: {group}
                                    </Typography>
                                )}
                            </Grid>
                        </Grid>
                    </Container>
                </Container>
            </Container>
            {otherProducts.length !== 0 && (
                <Container maxWidth="lg" sx={{ mt: 3, display: "flex", gap: "1rem", flexDirection: "column" }}>
                    <Typography sx={{ fontSize: { lg: '24px', md: '28px' }, fontWeight: 700 }} gutterBottom>
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
                                    quantity={item.cartQuantity}
                                    zn={item.zn}
                                />
                            </Grid>
                        ))}
                    </Grid>
                </Container>
            )}
        </div>
    );
};

export default ProductPage;
