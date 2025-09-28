const React = require("react");
const ReactDOM = require("react-dom/client");

import ProductFeed from './components/ProductFeed.jsx';
import Footer from './components/Footer/Footer.jsx';

ReactDOM.createRoot(
    document.getElementById("app")
)
.render(
    <div>
        <ProductFeed />
    </div>
);
