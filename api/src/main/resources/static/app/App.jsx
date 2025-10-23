const React = require("react");
const ReactDOM = require("react-dom/client");
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { GoogleReCaptchaProvider } from "react-google-recaptcha-v3";
import { createTheme, ThemeProvider } from '@mui/material/styles';
import ProductFeed from './components/ProductFeed.jsx';
import Login from './components/Login.jsx';
import Signup from './components/Signup.jsx';
import ProductPage from './components/ProductPage.jsx';
import Navbar from './components/Navbar.jsx';
import Footer from './components/Footer.jsx';
import { AppProvider } from './components/AppContext.js';
import { Outlet } from 'react-router-dom';

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


const MainLayout = () => {
  return (
    <div>
      <Navbar />
      <main>
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

const AuthLayout = () => {
  return (
    <div>
      <main>
        <Outlet />
      </main>
    </div>
  );
};

function App() {
    const SITE_KEY = '6Lf3LuYrAAAAAJqGCS8WfdcmtAl-RsYvSvHEXW94';
    return (
        <GoogleReCaptchaProvider
            reCaptchaKey={SITE_KEY}>
            <AppProvider>
                <ThemeProvider theme={theme}>
                    <BrowserRouter>
                        <Routes>
                            <Route path="/auth" element={<AuthLayout />}>
                                <Route path="/auth/login" element={<Login />} />
                                <Route path="/auth/signup" element={<Signup />} />
                            </Route>
                            <Route path="/main" element={<MainLayout />}>
                                <Route index element={<ProductFeed />} />
                                <Route path="/main/product/:id" element={<ProductPage />} />
                            </Route>
                        </Routes>
                    </BrowserRouter>
                </ThemeProvider>
            </AppProvider>
        </GoogleReCaptchaProvider>
    );
}
ReactDOM.createRoot(document.getElementById("app")).render(<App />);
