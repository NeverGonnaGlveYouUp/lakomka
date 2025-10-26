import axios from 'axios';
import React, { useState, useRef, useContext, useEffect } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { FaRegUserCircle, FaKey } from 'react-icons/fa';
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
    Badge
} from '@mui/material';
import { useAppContext } from './AppContext.js';
import { useNavigate } from "react-router-dom";

const Navbar = () => {
  const [options, setOptions]       = useState([]);
  const [loading, setLoading]       = useState(false);
  const previousController          = useRef();
  const { counter }                 = useAppContext();
  const navigate                    = useNavigate();
  const [loggedUsername, setLoggedUsername] = useState('');

    // Fetch username when component mounts
    useEffect(() => {
        const fetchUsername = async () => {
            try {
                const response = await axios.get('/api/current-user', {
                    headers: {
                        'Authorization': localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null
                    }
                });
                if (response.data && response.data.userName) {
                    setLoggedUsername(response.data.userName);
                }
            } catch (error) {
                console.error('Error fetching username:', error);
            }
        };

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
    const response = await axios.get(`/api/products/getByFilter?search=name%3D%3D%22%2A${capitalizeFirstLetter(searchTerm)}%2A%22%2Cname%3D%3D%22%2A${searchTerm}%2A%22&page=0&size=10&sort=name%2Casc`);
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
              <Typography variant="h6" component="div" sx={{ alignSelf: "center" }}>
                My Logo
              </Typography>
              <Autocomplete
                options={options}
                onInputChange={onInputChange}
                onChange={(option) => findIdByTitle(option.target.textContent) != null ? navigate("/main/product/" + findIdByTitle(option.target.textContent)) : option.preventDefault()}
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
              <IconButton color="inherit" onClick={() => navigate("/auth/login")}>
                  <FaRegUserCircle  />
              </IconButton >
              <IconButton color="inherit" onClick={() => navigate("/auth/change-password")}>
                  <FaKey  />
              </IconButton >
              <IconButton  color="inherit">
                <Badge badgeContent={counter} color="secondary">
                  <IoBagOutline />
                </Badge>
              </IconButton >
              <Typography variant="h6" component="div" sx={{ alignSelf: "center" }}>
                  {loggedUsername}
              </Typography>
            </Stack>
          </Container>
        </Toolbar>
      </AppBar>
    </ThemeProvider>
  );
};

export default Navbar;
