import React from 'react';
import { AppBar, Toolbar, Typography, Button, TextField } from '@mui/material';
import { ThemeProvider } from '@mui/material/styles';
import { FaUserCircle } from 'react-icons/fa';
import { createTheme } from '@mui/material/styles';
import { IoBagOutline } from "react-icons/io5";


const Navbar = () => {

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
          <TextField
            variant="outlined"
            placeholder="Search..."
            sx={{ marginRight: 2 }}
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
