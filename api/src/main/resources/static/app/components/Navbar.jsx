import axios from 'axios';
import React, { useState, useRef, useContext, useEffect } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { FaRegUserCircle, FaUser, FaSignOutAlt, FaRegQuestionCircle } from 'react-icons/fa';
import { FaCartShopping } from "react-icons/fa6";
import { createTheme } from '@mui/material/styles';
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
    Tooltip,
    Box
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
    const response = await axios.get(`/api/products/search?search=` + searchTerm);
    setLoading(false);
    const updatedOptions = response.data.map((p) => {
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
                <Box sx={{ display: "flex", flexDirection: "column" }}>
                  <IconButton  color="inherit" onClick={() => navigate("/cart")}>
                    <Badge badgeContent={counter} color="secondary">
                        <FaCartShopping />
                    </Badge>
                  </IconButton >
                  <Typography sx={{ fontSize: "small" }}>Корзина</Typography>
                </Box>
                <Box sx={{ display: "flex", flexDirection: "column" }}>
                  <IconButton  color="inherit" onClick={() => navigate("/info")}>
                    <FaRegQuestionCircle />
                  </IconButton >
                  <Typography sx={{ fontSize: "small" }}>Информация</Typography>
                </Box>
              {!isLoggedIn && (
                 <Box sx={{ display: "flex", flexDirection: "column" }}>
                    <IconButton color="inherit" onClick={() => navigate("/auth/login")}>
                        <FaRegUserCircle />
                    </IconButton>
                    <Typography sx={{ fontSize: "small" }}>Войти</Typography>
                </Box>
              )}
              {isLoggedIn && (
                <Box sx={{ display: "flex", flexDirection: "column" }}>
                    <IconButton color="inherit" onClick={() => navigate("/private/profile")}>
                        <FaUser  />
                    </IconButton >
                    <Typography sx={{ fontSize: "small" }}>Профиль</Typography>
                </Box>
              )}
              {isLoggedIn && (
                <Box sx={{ display: "flex", flexDirection: "column" }}>
                    <IconButton color="inherit" onClick={handleLogout}>
                        <FaSignOutAlt />
                    </IconButton>
                    <Typography sx={{ fontSize: "small" }}>Выйти</Typography>
                </Box>
              )}
            </Stack>
          </Container>
        </Toolbar>
      </AppBar>
    </ThemeProvider>
  );
};

export default Navbar;