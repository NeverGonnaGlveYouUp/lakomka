const React = require("react");
const ReactDOM = require("react-dom/client");

import { createTheme, ThemeProvider } from '@mui/material/styles';

import ProductFeed from './components/ProductFeed.jsx';
import Navbar from './components/Navbar.jsx';
import Footer from './components/Footer.jsx';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
  typography: {
    fontFamily: 'Arial, sans-serif',
    h1: {
      fontSize: '2rem',
      fontWeight: 700,
    },
    body1: {
      fontSize: '1rem',
    },
  },
});

ReactDOM.createRoot(
    document.getElementById("app")
)
.render(
    <ThemeProvider theme={theme}>
        <Navbar />
        <ProductFeed />
        <Footer />
    </ThemeProvider>
);
