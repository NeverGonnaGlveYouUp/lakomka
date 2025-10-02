const React = require("react");
const ReactDOM = require("react-dom/client");

import ProductFeed from './components/ProductFeed.jsx';
import Footer from './components/Footer.jsx';
import Header from './components/Header.jsx';

ReactDOM.createRoot(
    document.getElementById("app")
)
.render(
    <div>
        <Header />
        <ProductFeed />
        <Footer />
    </div>
);
