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
    Button
    } from "@mui/material";
import useMatchMedia from './hooks/useMatchMedia.jsx'
import { FaFilter } from "react-icons/fa";


const ProductFeed = () => {
    const sortOptions = [
      {
        value: '&sort=priceKons%2Casc',
        label: 'Дешевле',
      },
      {
        value: '&sort=priceKons%2Cdesc',
        label: 'Дороже',
      },
      {
        value: '&sort=name%2Casc',
        label: 'А → Я',
      },
      {
        value: '&sort=name%2Cdesc',
        label: 'Я → А',
      },
      {
        value: '&sort=zn==1',
        label: 'Новинки',
      },
    ];
    const [products, setProducts]           = useState([]);
    const [size, setSize]                   = useState(36);
    const [totalElements, setTotalElements] = useState();
    const [totalPages, setTotalPages]       = useState();
    const isDesktopResolution               = useMatchMedia('(min-width:992px)', true);
    const [searchParamsGlobal, setSearchParamsGlobal] = useState([]);
    const [number, setNumber]               = useState(0);
    const [sort, setSort]                   = useState(sortOptions[0].value);

    useEffect(() => {
        fetchData();
    }, []);

    useEffect(() => {
        fetchData();
    }, [sort]);

    useEffect(() => {
        window.scrollTo({ top: 0, behavior: "smooth" })
        fetchData();
    }, [number]);

    const handlePageChange = (event, value) => {
        setNumber(value - 1);
    };

    const handleSortChange = (event) => {
        setSort(event.target.value);
    };

    const fetchData = async (filterData) => {
        let url = `/products/getByFilter?page=${number}&size=${size}`;
        if(filterData){
            setNumber(0);
            url = `/products/getByFilter?page=0&size=${size}`;
            let searchParams = [];
            let priceRange   = filterData.priceRange;
            let massRange    = filterData.massRange;
            let worker       = filterData.worker;
            let productGroup = filterData.productGroup;
            let country      = filterData.country;

            if ((priceRange[0] || priceRange[0] <= 0) && priceRange[1]) {
                searchParams.push(`priceKons%3E%3D${priceRange[0]}`);
                searchParams.push(`priceKons%3C%3D${priceRange[1]}`);
            }
            if ((massRange[0] || massRange[0] <= 0) && massRange[1]) {
                searchParams.push(`weight%3E%3D${massRange[0]}`);
                searchParams.push(`weight%3C%3D${massRange[1]}`);
            }
            if (worker) {
                searchParams.push(`worker%3D%3D%22${worker}%22`);
            }
            if (country) {
                searchParams.push(`country%3D%3D%22${country}%22`);
            }
            if (productGroup) {
                searchParams.push(`productGroup%3D%3D%22${productGroup}%22`);
            }
            if (searchParams.length > 0) {
                url += `&search=${searchParams.join('%3B')}`;
            }
            setSearchParamsGlobal(searchParams);
        } else if (searchParamsGlobal[0]) {
            url += `&search=${searchParamsGlobal.join('%3B')}`;
        }
        url += sort;
        const response = await axios.get(url);
        setTotalElements(response.data.totalElements);
        setTotalPages(response.data.totalPages);
        setNumber(response.data.number);
        setProducts(response.data.content);
    };

    return (
        <Container maxWidth="lg" sx={{ mt: 3,  display: "flex", gap: "2rem" }}>
            <Box sx={{ display: "flex", flexDirection: "column" }}>
                {isDesktopResolution && (<Filter onFilterApply={(e) =>{
                        isDesktopResolution && setSearchParamsGlobal([]);
                        isDesktopResolution && fetchData(e);
                    }}/>)}
            </Box>
            <div>
                <Box component="section" sx={{ p: 2 }}>
                    {!isDesktopResolution && ( <Filter onFilterApply={(e) =>{
                        !isDesktopResolution && setSearchParamsGlobal([]);
                        !isDesktopResolution && fetchData(e);
                        }}/> )}
                    <Box sx={{ display: 'flex', flexDirection: 'row', gap: '1rem' }}>
                        <TextField
                          id="outlined-select-sort-native"
                          select
                          label="Сортировка"
                          defaultValue="new"
                          onChange={handleSortChange}
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
                    </Box>
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
                    page={number + 1}
                    count={totalPages}
                    onChange={handlePageChange}
                    color="primary"
                    shape="rounded"/>
            </div>
        </Container>
    );
};

export default ProductFeed;
