import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
    Container,
    Stack,
    Grid,
    Box,
    Typography,
    Link,
    Paper,
    styled,
    Card,
    CardContent,
    CardMedia,
    CardActionArea,
    TextField,
    IconButton
    } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAppContext } from './AppContext.js';
import { checkJWTExpiration } from './checkJWTExpiration.js';
import useMountedRef from "./useMountedRef.jsx";
import { FaTrashAlt } from "react-icons/fa";

const CartPageImage = styled(Box)({
  width: '88px',
  height: '117.333px',
  'object-fit': 'cover',
  'object-position': 'center',
  'border-radius': '3%',
  margin: '10px',
});

const CartPageCard = ( { newData, id, image, name, price, weight, quantity } ) => {

    const [count, setCount]             = useState(null);
    const [oldCount, setOldCount]       = useState(null);
    const { setContextCount }           = useAppContext();
    const mountedRef                    = useMountedRef();
    const [localWeight, setLocalWeight] = useState(null);
    const [oldLocalWeight, setOldLocalWeight] = useState(null);
    const [localPrice, setLocalPrice]   = useState(null);
    const [oldLocalPrice, setOldLocalPrice]   = useState(null);
    const navigate                      = useNavigate();
    const [visible, setVisible]         = useState(true);

    useEffect(() => {
      setCount(quantity);
      setLocalWeight(weight);
      setLocalPrice(price);
    }, [id])

    useEffect(() => {
      const fetchData = async () => {
        try {
          checkJWTExpiration();
          setOldCount(localWeight);
          setOldLocalPrice(localPrice);
          const response = await axios.put('/api/cart/add?id=' + id + '&quantity=' + (count === '' ? 0 : count), null,
          { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
          if (response.data.quantity == 0) {
              setVisible(false);
              setContextCount((c) => c - oldCount);
              setLocalPrice(0);
              setLocalWeight(0);
          } else {
              if (oldCount < response.data.quantity) {
                setContextCount((c) => c + (response.data.quantity - oldCount));
              } else {
                setContextCount((c) => c - (oldCount - response.data.quantity));
              }
              setLocalPrice(response.data.price);
              setLocalWeight(response.data.weight);
          }
          newData({
              localWeight, oldLocalWeight,
              localPrice, oldLocalPrice,
              id, quantity});
          setOldCount(0);
        } catch (error) {
          console.error(error);
        }
      };
      if (mountedRef.current && !isNaN(count) && (count != null && oldCount != null)) {
        fetchData();
      }
    }, [count]);

    return(
        <div>
            {visible && (
                <Card sx={{ display: 'flex' }}>
                    <CardActionArea
                        sx={{ width: "auto" }}
                        onClick={() => {
                              navigate("/product/" + id);
                              window.scrollTo({ top: 0, behavior: "smooth" });
                        }}>
                    <CardMedia
                        sx={{ padding: "1rem", width: 151, objectFit: "cover" }}
                        src="/api/getImage/green-grass-cute-cat-hd-de37pmurfb12yl3j.jpg"
                        component="img"/>
                    </CardActionArea>
                    <CardContent sx={{ display: 'flex',
                                    flexDirection: "row",
                                    width: "-webkit-fill-available",
                                    justifyContent: "space-between" }}>
                        <Stack spacing={0}
                            sx={{ flexDirection: "column",
                                width: "-webkit-fill-available" }}>
                            <Typography component="div" variant="h5">
                                {name}
                            </Typography>
                            <Typography component="div" variant="h6">
                                {localPrice} ₽
                            </Typography>
                            <Typography component="div" sx={{ fontSize: "14px", color: "rgba(22, 22, 21, .4)"}}>
                                {localWeight} грамм
                            </Typography>
                        </Stack>
                        <Container sx={{ display: "flex", flexDirection: "column", gap: "0.5rem" }}>
                            <Stack direction="row" spacing={0}>
                                <IconButton onClick={() => {
                                    setOldCount(count);
                                    setCount((c) => c - 1);
                                    }} color="primary" aria-label="decrement">
                                        -
                                </IconButton>
                                <TextField
                                    type="number"
                                    value={count ? count : ''}
                                    onChange={(e) => {
                                        setOldCount(count);
                                        setCount(parseInt(e.target.value, 10));
                                    }}
                                    onKeyPress={(event) => {
                                        if (event?.key === '-' || event?.key === ',' || event?.key === '.') {
                                            event.preventDefault();
                                        }}}
                                    inputProps={{ min: 0, style: { textAlign: 'center' } }}
                                    label="Кол-во"
                                    variant="standard"
                                    size="small"
                                    style={{ margin: '0 8px' }}
                                />
                                <IconButton onClick={() => {
                                    setOldCount(count);
                                    setCount((c) => c + 1);
                                    }} color="primary" aria-label="increment">
                                        +
                                </IconButton>
                            </Stack>
                            <Stack>
                                <IconButton sx={{width: "fit-content"}} onClick={() => {
                                        setOldCount(count);
                                        setCount(0);
                                    }}>
                                    <FaTrashAlt />
                                </IconButton>
                            </Stack>
                        </Container>
                    </CardContent>
                </Card>
            )}
         </div>
    );
}

const CartPage = () => {

    const [products, setProducts]           = useState([]);
    const [weight, setWeight]               = useState(0);
    const [price, setPrice]                 = useState(0);
    const { contextCount, setContextCount } = useAppContext();
    const mountedRef                        = useMountedRef();
    const navigate                          = useNavigate();

    const sumByField = (array, field) => {
        return array.reduce((accumulator, current) => {
            return accumulator + current[field];
        }, 0);
    };

    useEffect(() => {
        const fetchData = async () => {
            checkJWTExpiration();
            const response = await axios.get('/api/cart/items', null,
            { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
            setContextCount(sumByField(response.data, 'quantity'));
            setPrice(sumByField(response.data, 'price'));
            setWeight(sumByField(response.data, 'weight'));
            setProducts(response.data);
        }
        if (!mountedRef.current) {
            fetchData();
        }
    }, []);

    const removeProductById = (id) => {
      setProducts((products) => products.filter(item => item.id !== id));
    };

    return(
        <Container maxWidth="lg" sx={{ mt: 3,  display: "flex", gap: "2rem", flexDirection: "column" }}>
            <Typography sx={{ margin: "10px 0 12px", lineHeight: "44px", fontSize: '44px', fontWeight: 700 }}>
                Корзина
            </Typography>
            <Box sx={{ flexGrow: 1, width: "-webkit - fill - available"}}>
            { products.length != 0 ? (
                <Container sx={{ display: "flex", flexDirection: "row", gap: "2rem"}}>
                    <Grid container spacing={2} sx={{ flexDirection: "column" }}>
                        {products.map((item, index) => (
                            <Grid key={index}>
                                <CartPageCard newData={(e) => {
                                        setPrice((p) => p - e.oldLocalPrice);
                                        setPrice((p) => p + e.localWeight);
                                        setWeight((w) => w - e.oldLocalWeight);
                                        setWeight((w) => w + e.localWeight);
                                        if (e.quantity == 0) {
                                            removeProductById(e.id);
                                        }
                                    }}
                                    id={item.productId}
                                    image="/api/getImage/green-grass-cute-cat-hd-de37pmurfb12yl3j.jpg"
                                    name={item.name}
                                    price={item.price}
                                    weight={item.weight}
                                    quantity={item.quantity}/>
                            </Grid>
                        ))}
                    </Grid>
                    <Paper sx={{ width: "34%%" }}>

                    </Paper>
                </Container>
            ) : (
                <Container sx={{ justifyContent: "center", padding: "30px 0", textAlign: "center", marginBottom: "25rem" }}>
                    <Typography sx={{ fontSize: "32px", lineHeight: "32px", fontWeight: 700, marginBottom: "1rem"}}>
                        {"В Вашей корзине пока нет товаров"}
                    </Typography>
                    <Typography sx={{ fontSize: "16px", lineHeight: "19px", marginBottom: "1rem" }}>
                        {"Вы можете их выбрать в "}
                        <Link onClick={() => navigate("/")}>
                             каталоге
                        </Link>
                        {"."}
                    </Typography>
                    {!!!localStorage.getItem('jwtToken') ?
                        (<div>
                            <Typography sx={{ fontSize: "16px", lineHeight: "19px", marginBottom: "0.5rem" }}>
                                {"Возможно, у вас остались товары в корзине, если Вы уже зарегистрированы на сайте."}
                            </Typography>
                            <Typography sx={{ fontSize: "16px", lineHeight: "19px", marginBottom: "1rem" }}>
                                {"Чтобы их увидеть, необходимо "}
                                <Link onClick={() => navigate("/auth/login")}>
                                    авторизоваться
                                </Link>
                                {"."}
                            </Typography>
                        </div>) : (<div></div>)}
                </Container>
            )}
            </Box>
        </Container>
    );
}

export default CartPage;