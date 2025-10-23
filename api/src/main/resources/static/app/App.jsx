const React = require("react");
const ReactDOM = require("react-dom/client");
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import ProductFeed from './components/ProductFeed.jsx';
import Login from './components/Login.jsx';
import ProductPage from './components/ProductPage.jsx';
import ContactForm from './components/ContactForm.jsx'

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
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/" element={<ProductFeed />} />
                    <Route path="/cf" element={<ContactForm />} />
                    <Route path="/product/:id" element={<ProductPage />} />
                </Routes>
            </BrowserRouter>
        </ThemeProvider>
);
