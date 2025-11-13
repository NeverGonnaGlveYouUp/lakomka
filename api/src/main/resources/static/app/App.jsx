const React = require("react");
const ReactDOM = require("react-dom/client");
import { BrowserRouter, Routes, Route, Link, Navigate, Outlet } from 'react-router-dom';
import { GoogleReCaptchaProvider } from "react-google-recaptcha-v3";
import { createTheme, ThemeProvider } from '@mui/material/styles';
import ProductFeed from './components/ProductFeed.jsx';
import Login from './components/Login.jsx';
import Signup from './components/Signup.jsx';
import ChangePassword from './components/ChangePassword.jsx';
import ProductPage from './components/ProductPage.jsx';
import Navbar from './components/Navbar.jsx';
import CartPage from './components/CartPage.jsx';
import ProfilePage from './components/ProfilePage.jsx';
import ErrorPage from './components/ErrorPage.jsx';
import GeneralInfoPage from './components/GeneralInfoPage.jsx';
import { checkJWTExpiration } from './components/checkJWTExpiration.js';
import { AppProvider } from './components/AppContext.js';

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

const isAuthenticated = () => !!localStorage.getItem('jwtToken');

const AuthLayout = () => {
  if (isAuthenticated()) {
    return <Navigate to="/private/profile" replace />;
  }

  return (
    <div>
        <main>
            <Outlet />
        </main>
    </div>
  );
};

const LayoutWithNavbar = () => {
  return (
      <div>
          <Navbar />
          <main>
              <Outlet />
          </main>
      </div>
  );
};

const ProtectedRoute = () => {
  if (!isAuthenticated()) {
    return <Navigate to="/auth/login" replace />;
  }

  return (LayoutWithNavbar());
};

function App() {
    const SITE_KEY = '6Lf1gPQrAAAAAG_tjJ1Jy4QuHJjKy5uBEZZc0z3y';
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
                            <Route path="/" element={<LayoutWithNavbar />}>
                                <Route index element={<ProductFeed />} />
                                <Route path="/product/:id" element={<ProductPage />} />
                                <Route path="/cart" element={<CartPage />} />
                                <Route path="/info" element={<GeneralInfoPage />} />
                            </Route>
                            <Route path="/private" element={<ProtectedRoute />}>
                                <Route path="/private/change-password" element={<ChangePassword />} />
                                <Route path="/private/profile" element={<ProfilePage />} />
                            </Route>
                            <Route path="/error" element={<ErrorPage />} />
                        </Routes>
                    </BrowserRouter>
                </ThemeProvider>
            </AppProvider>
        </GoogleReCaptchaProvider>
    );
}
ReactDOM.createRoot(document.getElementById("app")).render(<App />);
