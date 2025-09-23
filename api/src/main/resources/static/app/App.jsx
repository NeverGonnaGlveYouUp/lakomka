const React = require("react");
const ReactDOM = require("react-dom/client");

import ProductFeed from './components/ProductFeed.jsx';

ReactDOM.createRoot(
    document.getElementById("app")
)
.render(
    <div>
        <h1>Paginated List</h1>
        <ProductFeed />
    </div>
);
