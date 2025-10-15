import axios from 'axios';
import React, { useState, useRef } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { FaRegUserCircle } from 'react-icons/fa';
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
    CircularProgress
     } from '@mui/material';


const Navbar = () => {
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = React.useState(false);
  const previousController = useRef();

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
          <Container maxWidth="lg" sx={{ mt: 3,  display: "flex", justifyContent: "space-between", marginTop: "0px"}}>
              <Typography variant="h6" component="div" sx={{ alignSelf: "center" }}>
                My Logo
              </Typography>
              <Autocomplete
                options={options}
                onInputChange={onInputChange}
                onChange={(option) => window.location.href = "/product/" + findIdByTitle(option.target.textContent)}
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
              <IconButton  color="inherit" href="/login">
                  <FaRegUserCircle  />
              </IconButton >
              <IconButton  color="inherit">
                  <IoBagOutline />
              </IconButton >
            </Stack>
          </Container>
        </Toolbar>
      </AppBar>
    </ThemeProvider>
  );
};

export default Navbar;
