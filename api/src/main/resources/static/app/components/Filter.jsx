import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Box,
         Slider,
         Typography,
         Autocomplete,
         TextField,
         Stack,
         InputAdornment
    } from '@mui/material';
import { NumericFormat } from 'react-number-format';

const Filter = ({ onFilterChange }) => {
    const [priceRange, setPriceRange]       = useState([]);
    const [minPrice, setMinPrice]           = useState();
    const [maxPrice, setMaxPrice]           = useState();
    const [massRange, setMassRange]         = useState([]);
    const [maxMass, setMaxMass]             = useState();
    const [minMass, setMinMass]             = useState();
    const [workers, setWorkers]             = useState([]);
    const [countries, setCountries]         = useState([]);
    const [productGroups, setProductGroups] = useState([]);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        const response = await axios.get(`/products/search/getFilterBoundaries`);
        setPriceRange([response.data.minPrice, response.data.maxPrice]);
        setMinPrice(response.data.minPrice);
        setMaxPrice(response.data.maxPrice);
        setMassRange([response.data.minMass, response.data.maxMass]);
        setMinMass(response.data.minMass);
        setMaxMass(response.data.maxMass);
        setWorkers(response.data.workers.split(', '));
        setCountries(response.data.countries.split(', '));
        setProductGroups(response.data.productGroups.split(', '));
    };

    const handlePriceRangeChange = (event, newValue) => {
      setPriceRange(newValue);
    };

    const handleMassRangeChange = (event, newValue) => {
      setMassRange(newValue);
    };

    return (
        <Box sx={{ width: "100%", paddingTop: "1rem" }}>
            <Autocomplete
                  disablePortal
                  options={productGroups}
                  sx={{ width: "100%", paddingBottom: "1rem" }}
                  renderInput={(params) => <TextField {...params} label="Категория" />}
                />
            <Stack direction="row" justifyContent="space-between" alignItems="center">
                <TextField
                      label="Мин. Цена"
                      type="number"
                      variant="filled"
                      InputLabelProps={{ shrink: true }}
                      sx={{ width: "120px" }}
                      value={priceRange[0]}
                      slotProps={{
                            input: {
                                startAdornment: <InputAdornment position="start">₽</InputAdornment>,
                            },
                      }}
                      onChange={(e) => {
                        setPriceRange([Number(e.target.value), priceRange[1]]);
                      }}
                    />
                    <Typography sx={{ padding: "1rem" }}>-</Typography>
                    <TextField
                      label="Макс. Цена"
                      type="number"
                      variant="filled"
                      InputLabelProps={{ shrink: true }}
                      sx={{ width: "120px" }}
                      value={priceRange[1]}
                      slotProps={{
                            input: {
                                startAdornment: <InputAdornment position="start">₽</InputAdornment>,
                            },
                      }}
                      onChange={(e) => {
                        setPriceRange([priceRange[0], Number(e.target.value)]);
                      }}
                    />
            </Stack>
            <Slider
                getAriaLabel={() => "Price range"}
                value={priceRange}
                onChange={handlePriceRangeChange}
                valueLabelDisplay="auto"
                min={minPrice}
                max={maxPrice}
            />
            <Stack direction="row" justifyContent="space-between" alignItems="center">
                <TextField
                      label="Мин. Масса"
                      type="number"
                      variant="filled"
                      InputLabelProps={{ shrink: true }}
                      sx={{ width: "120px" }}
                      value={massRange[0]}
                      slotProps={{
                            input: {
                                startAdornment: <InputAdornment position="start">гр.</InputAdornment>,
                            },
                      }}
                      onChange={(e) => {
                        setMassRange([Number(e.target.value), massRange[1]]);
                      }}
                    />
                    <Typography sx={{ padding: "1rem" }}>-</Typography>
                    <TextField
                      label="Макс. Масса"
                      type="number"
                      variant="filled"
                      InputLabelProps={{ shrink: true }}
                      sx={{ width: "120px" }}
                      value={massRange[1]}
                      slotProps={{
                            input: {
                                startAdornment: <InputAdornment position="start">гр.</InputAdornment>,
                            },
                      }}
                      onChange={(e) => {
                        setMassRange([massRange[0], Number(e.target.value)]);
                      }}
                    />
            </Stack>
            <Slider
                getAriaLabel={() => "Mass range"}
                value={massRange}
                onChange={handleMassRangeChange}
                valueLabelDisplay="auto"
                min={minMass}
                max={maxMass}
            />
            <Autocomplete
                  disablePortal
                  options={workers}
                  sx={{ width: "100%", paddingBottom: "1rem" }}
                  renderInput={(params) => <TextField {...params} label="Производитель" />}
                />
            <Autocomplete
                  disablePortal
                  options={countries}
                  sx={{ width: "100%", paddingBottom: "1rem" }}
                  renderInput={(params) => <TextField {...params} label="Страна производитель" />}
                />
        </Box>
    );
};

export default Filter;