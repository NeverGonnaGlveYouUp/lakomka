import React, { useEffect, useState } from 'react';
import axios from 'axios';
import ProductCard from './Card.jsx';
import Filter from './Filter.jsx';
import {
    Container,
    Box,
    Grid,
    Stack,
    Pagination,
    TextField,
    } from "@mui/material";
import useMatchMedia from './hooks/useMatchMedia.jsx'


const ProductFeed = () => {
    const [products, setProducts]           = useState([]);
    const [size, setSize]                   = useState(36);
    const [totalElements, setTotalElements] = useState();
    const [totalPages, setTotalPages]       = useState();
    const [number, setNumber]               = useState(0);
    const [priceRange, setPriceRange]       = useState([]);
    const [massRange, setMassRange]         = useState([]);
    const isDesktopResolution               = useMatchMedia('(min-width:992px)', true);

    useEffect(() => {
        fetchData(number, size);
    }, []);

    const handlePageChange = (event, value) => {
        window.scrollTo({ top: 0, behavior: "smooth" })
        fetchData(value - 1, size);
    };

    const fetchData = async (number, size) => {
        const response = await axios.get(`/products?page=${number}&size=${size}`);
        setTotalElements(response.data.page.totalElements);
        setTotalPages(response.data.page.totalPages);
        setNumber(response.data.page.number);
        setProducts(response.data._embedded.products);
    };

    const sortOptions = [
      {
        value: 'new',
        label: 'Новинки',
      },
      {
        value: 'cost-less',
        label: 'Дешевле',
      },
      {
        value: 'cost-more',
        label: 'Дороже',
      },
      {
        value: 'name-asc',
        label: 'А → Я',
      },
      {
        value: 'name-desc',
        label: 'Я → А',
      },
    ];


    return (
        <Container maxWidth="lg" sx={{ mt: 3,  display: "flex", gap: "2rem" }}>
                {isDesktopResolution && ( <Filter /> )}
            <div>
                <Box component="section" sx={{ p: 2 }}>
                    <TextField
                      id="outlined-select-sort-native"
                      select
                      label="Сортировка"
                      defaultValue="new"
                      slotProps={{
                        select: {
                          native: true,
                        },
                      }}>
                      {sortOptions.map((option) => (
                        <option key={option.value} value={option.value}>
                          {option.label}
                        </option>
                      ))}
                    </TextField>
                    {!isDesktopResolution && ( <Filter /> )}
                </Box>
                <Grid container spacing={1.5} columns={{ xs: 4, md: 8, lg: 16 }}>
                    {products.map((item, index) => (
                      <Grid key={index} size={{ xs: 4, md: 4, lg: 4 }}>
                        <ProductCard
                              image="/getImage/green-grass-cute-cat-hd-de37pmurfb12yl3j.jpg"
                              name={item.name}
                              price={item.priceKons}
                            />
                      </Grid>
                    ))}
                </Grid>
                <Pagination
                    style={{
                        'justifyItems': 'center',
                        'margin': '10px 0 20px 0',
                        }}
                    count={totalPages}
                    onChange={handlePageChange}
                    color="primary"
                    shape="rounded"/>
            </div>
        </Container>
    );
};

export default ProductFeed;
