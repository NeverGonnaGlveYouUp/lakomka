import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Card from './Card.jsx';
import { FaChevronLeft, FaChevronRight, FaAngleDoubleLeft, FaAngleDoubleRight } from "react-icons/fa";
import { CiGrid41, CiViewTable, CiFilter } from "react-icons/ci";
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';


const ProductFeed = () => {
    const [products, setProducts] = useState([]);
    const [size, setSize] = useState(20);
    const [totalElements, setTotalElements] = useState();
    const [totalPages, setTotalPages] = useState();
    const [number, setNumber] = useState(0);
    const [outputLayout, setOutputLayout] = useState('grid');
    const [sortProduct, setSortProduct] = useState('new-products');
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleSliderChange = (value) => {
        setRange(value);
    };

    const fetchData = async (number, size, jump = true) => {
        const response = await axios.get(`/products?page=${number}&size=${size}`);
        setTotalElements(response.data.page.totalElements);
        setTotalPages(response.data.page.totalPages);
        setNumber(response.data.page.number);
        if(jump === true){
            setProducts(response.data._embedded.products);
            window.scrollTo({
                top: 0
            });
        } else {
            setProducts(products.concat(response.data._embedded.products));
        }
    };

    useEffect(() => {
        fetchData(number, size);
    }, []);

    const handleItemsPerPageChange = (event) => {
        const newItemsPerPage = parseInt(event.target.value, 10);
        setSize(newItemsPerPage);
        fetchData(0, newItemsPerPage);
    };

    const handleSortChange = (event) => {
        setSortProduct(event.target.value);
    };

    function handleOutputChange(event){
        setOutputLayout(event.target.value);
    };

    return (
        <div className="containerNew">
            <div className="rowNew">
                <div className="colNew colNew-md-3">
                    <Stack spacing={2} direction="row">
                          <Button variant="text">Text</Button>
                          <Button variant="contained">Contained</Button>
                          <Button variant="outlined">Outlined</Button>
                        </Stack>
                </div>
                <div className="colNew colNew-md-9">
                    <div className="catalog-hat-style">
                        <div className="catalog-hat-style-controls">
                            <div className="sort-select">
                                <select
                                    className="items-select"
                                    value={sortProduct}
                                    onChange={handleSortChange}
                                >
                                    <option value="new-products">Новинки</option>
                                    <option value="cheaper">Дешевле</option>
                                    <option value="more-expensive">Дороже</option>
                                </select>
                            </div>
                            <button onClick={() => setIsModalOpen(true)}>
                                Фильтр
                            </button>
                            <div className="radio-button-group">
                                <div className="radio-button-group icons-container"
                                     onChange={handleOutputChange}>
                                    <input
                                        id="grid"
                                        name="output"
                                        type="radio"
                                        value="grid"
                                        checked={outputLayout === 'grid'}
                                        style={{ display: 'none' }}
                                    />
                                    <label for="grid">
                                        <CiGrid41 className="icon" color={outputLayout === 'grid' ? '#007bff' : '#000'}/>
                                    </label>
                                    <input
                                        id="table"
                                        name="output"
                                        type="radio"
                                        value="table"
                                        checked={outputLayout === 'table'}
                                        style={{ display: 'none' }}
                                    />
                                    <label for="table">
                                        <CiViewTable className="icon" color={outputLayout === 'table' ? '#007bff' : '#000'}/>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div className="catalog-hat-style-active-filters">
                        </div>
                    </div>
                    <div className="parent">
                        {products.map((item) => (
                            <Card className="child"
                                key={item.name}
                                title={item.name}
                                images="/getImage/green-grass-cute-cat-hd-de37pmurfb12yl3j.jpg"
                                price={item.priceKons}
                            />
                        ))}
                    </div>
                    <div className="pagination-container">
                        <button className="show-more-products-button"
                                onClick={() => fetchData(number + 1, size, false)}
                                disabled={number + 1 === totalPages}>
                            <span>Показать ещё {size}</span>
                        </button>
                        <div className="pagination-row">
                            <div className="items-per-page">
                                <span className="items-label">Товаров на страницу:</span>
                                <select
                                    className="items-select"
                                    value={size}
                                    onChange={handleItemsPerPageChange}
                                >
                                    <option value="20">20</option>
                                    <option value="50">50</option>
                                    <option value="100">100</option>
                                    <option value="200">200</option>
                                </select>
                            </div>
                            <div>
                                <button
                                    className="pagination-button"
                                    onClick={() => fetchData(0, size, true)}
                                    disabled={number === 0}
                                    aria-label="Go to first page"
                                >
                                    <FaAngleDoubleLeft />
                                </button>
                                <button
                                    className="pagination-button"
                                    onClick={() => fetchData(number - 1, size, true)}
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
                                    onClick={() => fetchData(number + 1, size, true)}
                                    disabled={number + 1 === totalPages}
                                    aria-label="Go to next page"
                                >
                                    <FaChevronRight />
                                </button>
                                <button
                                    className="pagination-button"
                                    onClick={() => fetchData(totalPages - 1, size, true)}
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
        </div>
    );
};

export default ProductFeed;
