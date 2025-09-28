import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Card from './Card.jsx';
import { FaChevronLeft, FaChevronRight, FaAngleDoubleLeft, FaAngleDoubleRight } from "react-icons/fa";

const ProductFeed = () => {
    const [products, setProducts] = useState([]);
    const [size, setSize] = useState(20);
    const [totalElements, setTotalElements] = useState();
    const [totalPages, setTotalPages] = useState();
    const [number, setNumber] = useState(0);

    const fetchData = async (number, size) => {
        const response = await axios.get(`/products?page=${number}&size=${size}`);
        setProducts(response.data._embedded.products);
        setTotalElements(response.data.page.totalElements);
        setTotalPages(response.data.page.totalPages);
        setNumber(response.data.page.number);
        window.scrollTo({
            top: 0
        });
    };

    useEffect(() => {
        fetchData(number, size);
    }, []);

  const handleItemsPerPageChange = (event) => {
    const newItemsPerPage = parseInt(event.target.value, 10);
    setSize(newItemsPerPage);
    fetchData(0, newItemsPerPage);
  };

    return (
        <div>
            <div className="parent" style={{width: "75%", margin: "auto"}}>
                {products.map((item) => (
                    <Card className="child"
                        key={item.name}
                        title={item.name}
                        images="/getImage/green-grass-cute-cat-hd-de37pmurfb12yl3j.jpg"
                        price={item.priceKons}
                    />
                ))}
            </div>
            <div>
        <div>
            <div className="pagination-container">
                <div className="items-per-page">
                    <span className="items-label">Товаров на страницу:</span>
                    <select
                        className="items-select"
                        value={size}
                        onChange={handleItemsPerPageChange}
                        aria-label="Select rows per page"
                    >
                        <option value="20">20</option>
                        <option value="50">50</option>
                        <option value="100">100</option>
                        <option value="200">200</option>
                    </select>
                </div>

                <div className="pagination-controls">
                    <button
                        className="pagination-button"
                        onClick={() => fetchData(0, size)}
                        disabled={number === 0}
                        aria-label="Go to first page"
                    >
                        <FaAngleDoubleLeft />
                    </button>
                    <button
                        className="pagination-button"
                        onClick={() => fetchData(number - 1, size)}
                        disabled={number === 0}
                        aria-label="Go to previous page"
                    >
                        <FaChevronLeft />
                    </button>
                    <span className="page-info">
                        Стр. {number + 1} из {totalPages}
                    </span>
                    <button
                        className="pagination-button"
                        onClick={() => fetchData(number + 1, size)}
                        disabled={number + 1 === totalPages}
                        aria-label="Go to next page"
                    >
                        <FaChevronRight />
                    </button>
                    <button
                        className="pagination-button"
                        onClick={() => fetchData(totalPages - 1, size)}
                        disabled={number + 1 === totalPages}
                        aria-label="Go to last page"
                    >
                        <FaAngleDoubleRight />
                    </button>
                </div>
            </div>
        </div>
        </div>
        </div>
    );
};

export default ProductFeed;
