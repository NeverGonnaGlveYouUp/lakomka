import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Box,
         Slider,
         Typography,
         Autocomplete,
         TextField,
         Stack,
         InputAdornment,
         Button
    } from '@mui/material';
import { NumericFormat } from 'react-number-format';
import { FaFilter } from "react-icons/fa";
import { CiFilter } from "react-icons/ci";

const Filter = ({ onFilterApply }) => {
    const [priceRange, setPriceRange]       = useState([]);
    const [minPrice, setMinPrice]           = useState();
    const [maxPrice, setMaxPrice]           = useState();
    const [massRange, setMassRange]         = useState([]);
    const [maxMass, setMaxMass]             = useState();
    const [minMass, setMinMass]             = useState();
    const [workers, setWorkers]             = useState([]);
    const [worker, setWorker]               = useState();
    const [countries, setCountries]         = useState([]);
    const [country, setCountry]             = useState();
    const [productGroups, setProductGroups] = useState([]);
    const [productGroup, setProductGroup]   = useState();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        const response = await axios.get(`/api/products/search/getFilterBoundaries`,
        { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
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

    const submitFilter = () => {
        onFilterApply({ priceRange, massRange, worker, country, productGroup });
    };

    return (
        <Box sx={{ width: "100%", paddingTop: "1rem" }}>
            <Typography sx={{ margin: "10px 0 12px", lineHeight: "22px", fontSize: '22px', fontWeight: 400 }}>
                Фильтр
            </Typography>
            <Autocomplete
                  disablePortal
                  options={productGroups}
                  sx={{ width: "100%", paddingBottom: "1rem" }}
                  onChange={(e) => {
                      setProductGroup(e.target.textContent);
                      onFilterApply({ priceRange, massRange, worker, country, productGroup: e.target.textContent });
                  }}
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
                        onFilterApply({ priceRange, massRange, worker, country, productGroup });
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
                        onFilterApply({ priceRange, massRange, worker, country, productGroup });
                      }}
                    />
            </Stack>
            <Slider
                getAriaLabel={() => "Price range"}
                value={priceRange}
                onChangeCommitted={submitFilter}
                onChange={(e) => {
                    setPriceRange(e.target.value);
                }}
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
                        onFilterApply({ priceRange, massRange, worker, country, productGroup });
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
                        onFilterApply({ priceRange, massRange, worker, country, productGroup });
                      }}
                    />
            </Stack>
            <Slider
                getAriaLabel={() => "Mass range"}
                value={massRange}
                onChangeCommitted={submitFilter}
                onChange={(e) => {
                    setMassRange(e.target.value);
                }}
                valueLabelDisplay="auto"
                min={minMass}
                max={maxMass}
            />
            <Autocomplete
                  disablePortal
                  options={workers}
                  sx={{ width: "100%", paddingBottom: "1rem" }}
                  onChange={(e) => {
                      setWorker(e.target.textContent);
                      onFilterApply({ priceRange, massRange, worker: e.target.textContent, country, productGroup });
                  }}
                  renderInput={(params) => <TextField {...params} label="Производитель" />}
                />
            <Autocomplete
                  disablePortal
                  options={countries}
                  sx={{ width: "100%", paddingBottom: "1rem" }}
                  onChange={(e) => {
                      setCountry(e.target.textContent);
                      onFilterApply({ priceRange, massRange, worker, country: e.target.textContent, productGroup });
                  }}
                  renderInput={(params) => <TextField {...params} label="Страна производитель" />}
                />
        </Box>
    );
};

export default Filter;