import axios from 'axios';
import React, { useState, useRef, useContext, useEffect } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { FaRegUserCircle, FaUser, FaSignOutAlt, FaShoppingBag } from 'react-icons/fa';
import { createTheme } from '@mui/material/styles';
import { IoBagOutline } from "react-icons/io5";
import {
    AppBar,
    Toolbar,
    Typography,
    IconButton,
    Autocomplete,
    TextField,
    Stack,
    Container,
    CircularProgress,
    Badge,
    Tooltip
} from '@mui/material';
import { useAppContext } from './AppContext.js';
import { useNavigate, useLocation } from "react-router-dom";
import { checkJWTExpiration } from './checkJWTExpiration.js';

const Navbar = () => {
  const [options, setOptions]               = useState([]);
  const [loading, setLoading]               = useState(false);
  const previousController                  = useRef();
  const { counter, setContextCount }        = useAppContext();
  const navigate                            = useNavigate();
  const location                            = useLocation();
  const [loggedUsername, setLoggedUsername] = useState('');
  const [isLoggedIn, setIsLoggedIn]         = useState(false);

    // Fetch username when component mounts
    useEffect(() => {
        const fetchUsername = async () => {
            try {
                const token = localStorage.getItem('jwtToken');
                if (!!token){
                    setIsLoggedIn(true);
                    const response = await axios.get('/api/current-user', {
                        headers: {
                            'Authorization': 'Bearer ' + token
                        }
                    });
                    if (response.data && response.data.userName) {
                        setLoggedUsername(response.data.userName);
                    }
                } else {
                    setIsLoggedIn(false);
                }
            } catch (error) {
                console.error('Error fetching username:', error);
                setIsLoggedIn(false);
            }
        };
        checkJWTExpiration();
        fetchUsername();
    }, []);

  const fetchData = async (searchTerm) => {
    if (previousController.current) {
      previousController.current.abort();
    }
    const controller = new AbortController();
    const signal = controller.signal;
    previousController.current = controller;
    setLoading(true);
    const response = await axios.get(`/api/products/getByFilter?search=name%3D%3D%22%2A${capitalizeFirstLetter(searchTerm)}%2A%22%2Cname%3D%3D%22%2A${searchTerm}%2A%22&page=0&size=10&sort=name%2Casc`,
    { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
    setLoading(false);
    const updatedOptions = response.data.content.map((p) => {
        return {
            id: p.id,
            title: p.name,
        };
    });
    setOptions(updatedOptions);
  }

  function findIdByTitle(title) {
      const item = options.find(option => option.title === title);
      return item ? item.id : null;
  }

  const onInputChange = (event, value, reason) => {
    if (value) {
      fetchData(value);
    } else {
      fetchData([]);
    }
  };


  function capitalizeFirstLetter(val) {
      return String(val).charAt(0).toUpperCase() + String(val).slice(1);
  }

  const handleLogout = async () => {
    try {
      const token = localStorage.getItem('jwtToken');
      if (token) {
        // Call backend logout endpoint
        await axios.post('/api/logout', {}, {
          headers: {
            'Authorization': 'Bearer ' + token
          }
        });
      }
    } catch (error) {
      console.error('Error during logout:', error);
    } finally {

      // Remove JWT token from localStorage
      localStorage.removeItem('jwtToken');

      // Clear the shopping cart counter
      setContextCount(0);

      // Redirect to login page or home
      navigate("/");

      // Reset state
      setIsLoggedIn(false);
      setLoggedUsername('');

    }
  };

  const theme = createTheme({
    palette: {
      primary: {
        main: '#FFFFFF',
      },
      secondary: {
        main: '#1976d2',
      },
    },
    typography: {
      fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    },
  });

  // Check if we're in the cart route
  const isCartRoute = location.pathname === '/cart';

  return (
    <ThemeProvider theme={theme}>
      <AppBar position="sticky">
        <Toolbar>
          <Container maxWidth="lg" sx={{ mt: 3,  display: "flex", justifyContent: "space-between", marginTop: "0px"}}>
              <Typography
                variant="h6"
                component="div"
                sx={{ alignSelf: "center" }}
                onClick={ () => navigate("/") }>
                My Logo
              </Typography>
              <Autocomplete
                options={options}
                onInputChange={onInputChange}
                onChange={(option) => findIdByTitle(option.target.textContent) != null ? navigate("/product/" + findIdByTitle(option.target.textContent)) : option.preventDefault()}
                getOptionLabel={(option) => option.title}
                style={{ width: 400 }}
                noOptionsText="Введите название"
                renderInput={(params) => (
                  <TextField
                  {...params}
                  label="Поиск"
                  slotProps={{input: {
                    ...params.InputProps,
                    endAdornment: (
                        <React.Fragment>
                            {loading ? <CircularProgress color="inherit" size={20} /> : null}
                            {params.InputProps.endAdornment}
                        </React.Fragment>
                        )
                      }}}/>
              )}
            />
            <Stack direction="row" spacing={2}>
              {isCartRoute && (
                <Tooltip title="Каталог товаров">
                    <IconButton color="inherit" onClick={() => navigate("/")}>
                        <FaShoppingBag />
                    </IconButton>
                </Tooltip>
              )}
              <IconButton  color="inherit" onClick={() => navigate("/cart")}>
                <Tooltip title="Корзина">
                    <Badge badgeContent={counter} color="secondary">
                        <IoBagOutline />
                    </Badge>
                </Tooltip>
              </IconButton >
              {!isLoggedIn && (
                <Tooltip title="Войти">
                    <IconButton color="inherit" onClick={() => navigate("/auth/login")}>
                        <FaRegUserCircle />
                    </IconButton>
                </Tooltip>
              )}
              <Typography variant="h6" component="div" sx={{ alignSelf: "center" }}>
                  {loggedUsername}
              </Typography>
              {isLoggedIn && (
                <Tooltip title="Профиль">
                    <IconButton color="inherit" onClick={() => navigate("/private/profile")}>
                        <FaUser  />
                    </IconButton >
                </Tooltip>
              )}
              {isLoggedIn && (
                <Tooltip title="Выйти">
                    <IconButton color="inherit" onClick={handleLogout}>
                        <FaSignOutAlt />
                    </IconButton>
                </Tooltip>
              )}
            </Stack>
          </Container>
        </Toolbar>
      </AppBar>
    </ThemeProvider>
  );
};

export default Navbar;