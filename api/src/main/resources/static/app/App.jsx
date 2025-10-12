const React = require("react");
const ReactDOM = require("react-dom/client");

import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { GoogleReCaptchaProvider } from "react-google-recaptcha-v3";
import { createTheme, ThemeProvider } from '@mui/material/styles';
import ProductFeed from './components/ProductFeed.jsx';
import Login from './components/Login.jsx';

const SITE_KEY = '6Lf3LuYrAAAAAJqGCS8WfdcmtAl-RsYvSvHEXW94';

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
    <GoogleReCaptchaProvider reCaptchaKey={SITE_KEY}>
        <ThemeProvider theme={theme}>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    {/* <Route path="/signup" element={<SignUp />} /> */}
                    <Route path="/home" element={<ProductFeed />} />
                </Routes>
            </BrowserRouter>
        </ThemeProvider>
    </GoogleReCaptchaProvider>
);
