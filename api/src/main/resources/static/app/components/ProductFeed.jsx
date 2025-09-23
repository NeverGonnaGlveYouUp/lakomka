import React, { useEffect, useState } from 'react';
import axios from 'axios';

const ProductFeed = () => {
    const [data, setData] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const fetchData = async (pageNumber) => {
        const response = await axios.get(`/products?page=${pageNumber}&size=20`);
        setData(response.data._embedded.products);
        setTotalPages(response.data.page.totalPages);
    };

    useEffect(() => {
        fetchData(page);
    }, [page]);

    return (
        <div>
            <ul>
                {data.map(item => (
                    <li key={item.LP005}>{item.name}</li>
                ))}
            </ul>
            <div>
                <button onClick={() => setPage(page - 1)} disabled={page === 0}>
                    Previous
                </button>
                <span> Page {page + 1} of {totalPages} </span>
                <button onClick={() => setPage(page + 1)} disabled={page + 1 === totalPages}>
                    Next
                </button>
            </div>
        </div>
    );
};

export default ProductFeed;
