import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Box,
         Slider,
         Typography,
         Backdrop,
         CircularProgress,
         TextField,
         Stack
    } from '@mui/material';
import { NumericFormat } from 'react-number-format';

const Filter = ({ onFilterChange }) => {
    const [priceRange, setPriceRange]       = useState([]);
    const [minPrice, setMinPrice]           = useState(priceRange[0]);
    const [maxPrice, setMaxPrice]           = useState(priceRange[1]);
    const [massRange, setMassRange]         = useState([]);
    const [workers, setWorkers]             = useState([]);
    const [countries, setCountries]         = useState([]);
    const [productGroups, setProductGroups] = useState([]);
    const [priceMarks, setPriceMarks]       = useState([]);
    const [massMarks, setMassMarks]         = useState([]);
    const [backdrop, setBackdrop]           = useState(false);
    const minDistance = 10;


    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        const response = await axios.get(`/products/search/getFilterBoundaries`);
        setPriceRange([response.data.minPrice, response.data.maxPrice]);
        setMinPrice(response.data.minPrice);
        setMaxPrice(response.data.maxPrice);
        setMassRange([response.data.minMass, response.data.maxMass]);
        setPriceMarks(
              [{
                value: response.data.minPrice,
                label: response.data.minPrice,
              },
              {
                value: response.data.maxPrice,
                label: response.data.maxPrice,
              }]
          );
        setMassMarks(
              [{
                value: response.data.minMass,
                label: response.data.minMass,
              },
              {
                value: response.data.maxMass,
                label: response.data.maxMass,
              }]
          );
        setWorkers(response.data.workers.split(', '));
        setCountries(response.data.countries.split(', '));
        setProductGroups(response.data.productGroups.split(', '));
    };

    const handleMaxChange = (event) => {
      if (event.target.value >= priceRange[0] $$ event.target.value <= priceRange[1]){
          setMaxPrice(event.target.value);
      } else {
          if(event.target.value >= priceRange[0]){
            setMaxPrice(priceRange[0]);

          }
      }
    };

    const handleMinChange = (event) => {
      if (event.target.value >= priceRange[0] && ){
          setMinPrice(event.target.value);
      } else {
          setMinPrice(priceRange[1]);
      }
    };

    return (
        <Box sx={{ width: 300 }}>
            <Stack direction="row" spacing={2}>
              <NumericFormat
                value={minPrice}
                onChange={handleMinChange}
                customInput={TextField}
                thousandSeparator
                valueIsNumericString
                prefix="$"
                variant="standard"
                label="Мин. Цена"
                allowNegative={false}
                decimalScale={0}
                defaultValue={priceRange[0]}
              />
              <NumericFormat
                value={maxPrice}
                onChange={handleMaxChange}
                customInput={TextField}
                thousandSeparator
                valueIsNumericString
                prefix="$"
                variant="standard"
                label="Макс. Цена"
                allowNegative={false}
                decimalScale={0}
                defaultValue={priceRange[1]}
              />
            </Stack>
        </Box>
    );
};

export default Filter;