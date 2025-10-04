import React from 'react';
import { BottomNavigation, Toolbar, Typography, Grid, Link, Box } from '@mui/material';
import { ThemeProvider } from '@mui/material/styles';
import { createTheme } from '@mui/material/styles';

const Footer = () => {
  return (
    <BottomNavigation
        height="100%"
        marginTop="48px">
      <Toolbar>
        <Grid container spacing={3} justifyContent="space-between">
          <Grid>
            <Typography variant="h6">Contact Us</Typography>
            <Typography variant="body1">Email: contact@example.com</Typography>
            <Typography variant="body1">Phone: +123 456 7890</Typography>
            <Typography variant="body1">Address: 123 Main St, City, Country</Typography>
          </Grid>
          <Grid>
            <Typography variant="h6">Useful Links</Typography>
            <Box>
              <Link href="#" color="inherit" underline="hover" style={{ display: 'block' }}>
                Home
              </Link>
              <Link href="#" color="inherit" underline="hover" style={{ display: 'block' }}>
                About Us
              </Link>
              <Link href="#" color="inherit" underline="hover" style={{ display: 'block' }}>
                Services
              </Link>
              <Link href="#" color="inherit" underline="hover" style={{ display: 'block' }}>
                Contact
              </Link>
            </Box>
          </Grid>
        </Grid>
      </Toolbar>
    </BottomNavigation>
  );
};

export default Footer;
