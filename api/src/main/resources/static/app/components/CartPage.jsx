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
    IconButton,
    List,
    ListItem,
    ListItemText,
    useMediaQuery,
    Button,
    Switch,
    FormControlLabel,
    Checkbox,
    Alert
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAppContext } from './AppContext.js';
import { checkJWTExpiration } from './checkJWTExpiration.js';
import useMountedRef from "./useMountedRef.jsx";
import { FaTrashAlt } from "react-icons/fa";
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';
import 'dayjs/locale/ru';
import { SmartCaptcha } from '@yandex/smart-captcha';
import { CAPTCHA_SITEKEY } from './constants.js';

const CartPageImage = styled(Box)({
  width: '88px',
  height: '117.333px',
  'objectFit': 'cover',
  'objectPsition': 'center',
  'borderRadius': '3%',
  margin: '10px',
});

const CartPageCard = ( { newData, id, image, name, price, weight, quantity, bitPackag } ) => {

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
    const [bitLocalPackag, setLocalBitPackag] = useState(false);

    useEffect(() => {
        setCount(quantity);
        setOldCount(quantity);
        setLocalWeight(weight);
        setLocalPrice(price);
        setLocalBitPackag(bitPackag);
    }, [id])

    useEffect(() => {

        // Prevent API call for invalid counts
        if (count < 0) {
            setCount(0);
            return;
        }

        if (mountedRef.current && !isNaN(count) && (count != null && oldCount != null) && count >= 0) {
            fetchData();
        }
    }, [count, bitLocalPackag]);

    const fetchData = async () => {
        try {
            checkJWTExpiration();
            setOldCount(count); // Save current count as old

            // Handle empty string or invalid values
            const quantityToSend = count === '' || isNaN(count) || count < 0 ? 0 : count;

            const response = await axios.put('/api/cart/add?id=' + id + '&bitPackag=' + bitLocalPackag + '&quantity=' + (count === '' ? 0 : count),
                { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });

            if (response.data.quantity == 0) {
                setVisible(false);
                setContextCount((c) => c - oldCount);
                // Call newData with updated values BEFORE setting local state
                newData({
                    localWeight: 0,
                    oldLocalWeight: localWeight,
                    localPrice: 0,
                    oldLocalPrice: localPrice,
                    id,
                    quantity: 0
                });
                setLocalPrice(0);
                setLocalWeight(0);
            } else {
                if (oldCount < response.data.quantity) {
                    setContextCount((c) => c + (response.data.quantity - oldCount));
                } else {
                    setContextCount((c) => c - (oldCount - response.data.quantity));
                }

                    // Call newData with updated values BEFORE setting local state
                newData({
                    localWeight: response.data.weight,
                    oldLocalWeight: localWeight,
                    localPrice: response.data.price,
                    oldLocalPrice: localPrice,
                    id,
                    quantity: response.data.quantity
                });

                setLocalPrice(response.data.price);
                setLocalWeight(response.data.weight);
            }
            setOldCount(0);
        } catch (error) {
            console.error(error);
        }
    };

    return (
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
                                {localWeight} Кг.
                            </Typography>
                        </Stack>
                        <Container sx={{ display: "flex", flexDirection: "column", gap: "0.5rem" }}>
                            <Stack direction="row" spacing={0}>
                                <IconButton
                                    onClick={() => {
                                        if (count > 0) { // Only decrement if count is greater than 0
                                            setOldCount(count);
                                            setCount((c) => c - 1);
                                        }
                                    }}
                                    color="primary"
                                    aria-label="decrement"
                                    disabled={count <= 0} // disable button when at 0
                                >
                                    -
                                </IconButton>
                                <TextField
                                    type="number"
                                    value={count ? count : ''}
                                    onChange={(e) => {
                                        const newValue = parseInt(e.target.value, 10);
                                        // Prevent negative values and NaN
                                        if (!isNaN(newValue) && newValue >= 0) {
                                            setOldCount(count);
                                            setCount(newValue);
                                        } else if (e.target.value === '') {
                                            setOldCount(count);
                                            setCount('');
                                        }
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
                            <Stack sx={{ display: "flex", flexDirection: "row" }}>
                                <IconButton sx={{width: "fit-content"}} onClick={() => {
                                        setOldCount(count);
                                        setCount(0);
                                    }}>
                                    <FaTrashAlt />
                                </IconButton>
                                <FormControlLabel
                                    label="Задать кол-во в упаковках"
                                    labelPlacement="start"
                                    control={
                                        <Checkbox
                                            checked={bitLocalPackag}
                                            onClick={() => {
                                                    setLocalBitPackag((p) => !p);
                                                }
                                            }
                                        />
                                    }
                                />
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
    const [cartSummary, setCartSummary]     = useState(null);
    const isDesktopResolution               = useMediaQuery('(min-width:992px)');
    const [isPrimVisible, setPrimVisible]   = useState(false);
    const [payVid, setPayVid]               = useState(false);
    const [prim, setPrim]                   = useState('');
    const [isSubmitting, setIsSubmitting]   = useState(false);
    const [dateDelivery, setDateDelivery]   = useState(null);
    const [errors, setErrors]               = useState({});

    const [token, setToken] = useState('');
    const [captchaLoaded, setCaptchaLoaded] = useState(false);
    const [resetCaptcha, setResetCaptcha] = useState(0);

    const handleCaptchaSuccess = (captchaToken) => {
        setToken(captchaToken);
        setCaptchaLoaded(true);
    };

    const handleResetCaptcha = () => {
      console.log('Resetting captcha: ', resetCaptcha);
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

    const sumByField = (array, field) => {
        return array.reduce((integerSum, item) => {
            const value = parseFloat(item[field]) || 0;
            return integerSum + Math.round(value * 10000);
        }, 0) / 10000;
    };

    const formatDecimal = (value) => {
        const num = parseFloat(value) || 0;
        return num.toFixed(3);
    };

    useEffect(() => {
        const fetchData = async () => {
            checkJWTExpiration();
            const response = await axios.get('/api/cart/items',
                { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });

            setProducts(response.data);

            // Calculate initial totals from the response data
            const totalItems = sumByField(response.data, 'quantity');
            const totalPrice = sumByField(response.data, 'price');
            const totalWeight = sumByField(response.data, 'weight');

            setContextCount(totalItems);
            setPrice(totalPrice);
            setWeight(totalWeight);

            // Fetch cart summary
            const summaryResponse = await axios.get('/api/cart/summary',
                { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
            setCartSummary(summaryResponse.data);
        }
        if (!mountedRef.current) {
            fetchData();
        }
    }, []);

    const removeProductById = (id) => {
      setProducts((products) => products.filter(item => item.id !== id));
    };

    // Function to update cart summary when items change
    const updateCartSummary = (updatedProducts) => {
        const totalItems = sumByField(updatedProducts, 'quantity');
        const totalPrice = sumByField(updatedProducts, 'price');
        const totalWeight = sumByField(updatedProducts, 'weight');

        setContextCount(totalItems);
        setPrice(totalPrice);
        setWeight(totalWeight);

        setCartSummary({
            totalItems,
            totalPrice,
            totalWeight
        });
    };

    const handleSubmit = async (e) => {

        if (!token) {
            console.error('No captcha token available');
            return;
        }

        checkJWTExpiration();
        setIsSubmitting(true);

        const body = {
            contact: null,
            telephone: null,
            email: null,
            addressDelivery: null,
            prim,
            dateDelivery,
            bitAccPrint: null,
            bitSertifPrint: null,
            payVid,
            token,
            "expectedAction": "CREATE_ORDER",
            "siteKey": CAPTCHA_SITEKEY,
        };

        try {
            const response = await axios.post('/api/orders/create-from-cart', body, {
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
            navigate("/private/orders");
        } catch (error) {
            if (error.response && error.response.status === 400) {
                const validationErrors = error.response.data;

                const formattedErrors = validationErrors.reduce((acc, error) => {
                    acc[error.field] = error.defaultMessage;
                    return acc;
                }, {});

                setErrors(formattedErrors);
                setIsSubmitting(false);
                handleResetCaptcha();
            } else {
                console.error("An unexpected error occurred:", error);
            }
        } finally {
            setIsSubmitting(false);
        }
    }

    const isWeekend = (date) => {
        const day = date.day();
        return day === 0 || day === 6; // Sunday = 0, Saturday = 6
    };

    return (
        <Container maxWidth="lg" sx={{ mt: 3, display: "flex", gap: "2rem", flexDirection: "column" }}>
            <Typography sx={{ margin: "10px 0 12px", lineHeight: "44px", fontSize: '44px', fontWeight: 700 }}>
                Корзина
            </Typography>
            <Box sx={{ flexGrow: 1, width: "-webkit - fill - available" }}>
                {products.length != 0 ? (
                    <Container sx={{ display: "flex", flexDirection: isDesktopResolution ? "row" : "column-reverse", gap: "2rem" }}>
                        <Grid container spacing={2} sx={{ width: "66%", flexDirection: "column" }}>
                            {products.map((item, index) => (
                                <Grid key={index}>
                                    <CartPageCard newData={(e) => {
                                        const updatedProducts = products.map(product => {
                                            if (product.productId === e.id) {
                                                return {
                                                    ...product,
                                                    quantity: e.quantity,
                                                    price: e.localPrice,    // Use the updated price from child
                                                    weight: e.localWeight   // Use the updated weight from child
                                                };
                                            }
                                            return product;
                                        });

                                        // Update the products state FIRST
                                        setProducts(updatedProducts);

                                        // Then update cart summary with the new products array
                                        updateCartSummary(updatedProducts);

                                        // Remove product if quantity is 0
                                        if (e.quantity == 0) {
                                            removeProductById(e.id);
                                        }
                                    }}
                                        id={item.productId}
                                        image="/api/getImage/green-grass-cute-cat-hd-de37pmurfb12yl3j.jpg"
                                        name={item.name}
                                        price={item.price}
                                        weight={item.weight}
                                        quantity={item.quantity}
                                        bitPackag={item.bitPackag}/>
                                </Grid>
                            ))}
                        </Grid>
                        <Paper sx={{ width: isDesktopResolution ? "34%" : "100%", height: "fit-content" }}>
                            <Box
                                component="form"
                                sx={{ p: 2, display: "flex",
                                flexDirection: isDesktopResolution ? "column" : "column-reverse" }}>
                                <List>
                                    <ListItem>
                                        <Typography variant="h6"
                                                    sx={{ mt: 2, fontSize: "20px", lineHeight: "20px", fontWeight: 700}}>
                                            Ваша корзина
                                        </Typography>
                                    </ListItem>
                                    <ListItem>
                                        <ListItemText
                                            primary="Количество товаров"
                                            secondary={cartSummary?.totalItems || contextCount}
                                        />
                                    </ListItem>
                                    <ListItem>
                                        <ListItemText
                                            primary="Общая стоимость"
                                            secondary={`${cartSummary?.totalPrice || price} ₽`}
                                        />
                                    </ListItem>
                                    <ListItem>
                                            <ListItemText
                                                primary="Общий вес"
                                                secondary={`${formatDecimal(cartSummary?.totalWeight || weight)} Кг.`}
                                            />
                                    </ListItem>
                                    <ListItem>
                                        <Typography variant="h6"
                                                sx={{ mt: 2, fontSize: "20px", lineHeight: "20px", fontWeight: 700}}>
                                            Опции заказа
                                        </Typography>
                                    </ListItem>
                                    <ListItem>
                                        <LocalizationProvider
                                            adapterLocale="ru"
                                            dateAdapter={AdapterDayjs}>
                                            <DatePicker
                                                sx={{ width: "-webkit-fill-available" }}
                                                required
                                                value={dateDelivery}
                                                onChange={setDateDelivery}
                                                minDate={dayjs().add(1, 'day')}
                                                maxDate={dayjs().add(14, 'day')}
                                                label="Дата доставки*"
                                                inputVariant="outlined"
                                                shouldDisableDate={isWeekend}
                                                views={['month', 'day']}
                                                slotProps={{
                                                    textField: {
                                                      error: !!errors.dateDelivery,
                                                      helperText: errors.dateDelivery,
                                                    },
                                                  }}/>
                                            </LocalizationProvider>
                                    </ListItem>
                                    <ListItem sx={{ flexDirection: "column", alignItems: "flex-start" }}>
                                        <FormControlLabel
                                            label="Оплатить безналом с отсрочкой"
                                            control={
                                                <Checkbox
                                                    checked={payVid}
                                                    onClick={() => setPayVid((prev) => !prev)}
                                                />
                                            }
                                        />
                                    </ListItem>
                                    <ListItem sx={{flexDirection: "column"}}>
                                        <FormControlLabel
                                            label="Хотите оставить комментарий к заказу?"
                                            control={
                                                <Checkbox
                                                checked={isPrimVisible}
                                                    onClick={() => setPrimVisible((prev) => !prev)}
                                                />
                                            }
                                        />
                                        {isPrimVisible && (
                                            <TextField
                                                label="Комментарий к заказу"
                                                multiline
                                                rows={4}
                                                variant="outlined"
                                                fullWidth
                                                margin="normal"
                                                value={prim}
                                                helperText={`${prim.length}/255 символов`}
                                                onChange={(event) => setPrim(event.target.value)}
                                                inputProps={{ maxLength: 255 }}
                                            />
                                        )}
                                    </ListItem>
                                </List>
                                {(!!localStorage.getItem('jwtToken') && !!localStorage.getItem('route') != "null") && (
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
                                        language='ru'/>
                                )}
                                {(!!localStorage.getItem('route') == "null") && (
                                    <Alert severity="warning" sx={{ mb: "1rem" }}>
                                        <Typography sx={{ fontSize: "16px", lineHeight: "19px", marginBottom: "1rem" }}>
                                            {"Чтобы начать покупки вам необходимо заключить "}
                                            <Link onClick={() => navigate("/info#dogovor")}>
                                                договор
                                            </Link>
                                            {"."}
                                        </Typography>
                                    </Alert>
                                )}
                                {(!!!localStorage.getItem('jwtToken')) && (
                                    <Alert severity="warning" sx={{ mb: "1rem" }}>
                                        <Typography sx={{ fontSize: "16px", lineHeight: "19px", marginBottom: "1rem" }}>
                                            <Link onClick={() => navigate("/auth/login")}>
                                                Авторизуйтесь
                                            </Link>
                                            {" для заказа."}
                                        </Typography>
                                    </Alert>
                                )}
                                <Button
                                    onClick={() => handleSubmit()}
                                    disabled={isSubmitting || price==0 || !!!localStorage.getItem('jwtToken') || !captchaLoaded || !token || !!!localStorage.getItem('route')}
                                    type="submit"
                                    color="success"
                                    fullWidth
                                    variant="contained">
                                    {isSubmitting ? 'Делаем заказ...' : 'Заказать'}
                                </Button>
                            </Box>
                        </Paper>
                    </Container>
                ) : (
                    <Container sx={{ justifyContent: "center", padding: "30px 0", textAlign: "center", marginBottom: "25rem" }}>
                        <Typography sx={{ fontSize: "32px", lineHeight: "32px", fontWeight: 700, marginBottom: "1rem" }}>
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