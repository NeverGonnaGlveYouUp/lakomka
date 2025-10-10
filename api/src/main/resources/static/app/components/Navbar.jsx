import axios from 'axios';
import React, { useState, useRef } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { FaUserCircle } from 'react-icons/fa';
import { createTheme } from '@mui/material/styles';
import { IoBagOutline } from "react-icons/io5";
import {
    AppBar,
    Toolbar,
    Typography,
    Button,
    Autocomplete,
    TextField
     } from '@mui/material';


const Navbar = () => {
  const [options, setOptions] = useState([]);
  const previousController = useRef();

  const fetchData = async (searchTerm) => {
    if (previousController.current) {
      previousController.current.abort();
    }
    const controller = new AbortController();
    const signal = controller.signal;
    previousController.current = controller;
    const response = await axios.get(`/products/getByFilter?search=name%3D%3D%22%2A${capitalizeFirstLetter(searchTerm)}%2A%22%2Cname%3D%3D%22%2A${searchTerm}%2A%22&page=0&size=10&sort=name%3B%20asc`);
    const updatedOptions = response.data.content.map((p) => {
        return { title: p.name };
    });
    setOptions(updatedOptions);
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
        main: '#dc004e',
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
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            My Logo
          </Typography>
          <Autocomplete
            variant="outlined"
            label="Что-то ищите? "
            options={options}
            onInputChange={onInputChange}
            getOptionLabel={(option) => option.title}
            style={{ width: 300 }}
            renderInput={(params) => (
              <TextField {...params} variant="outlined" />
            )}
          />
          <Button color="inherit" startIcon={<FaUserCircle />}>
          </Button>
          <Button color="inherit" startIcon={<IoBagOutline />}>
          </Button>
        </Toolbar>
      </AppBar>
    </ThemeProvider>
  );
};

export default Navbar;
