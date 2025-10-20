import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  Card,
  CardContent,
  CardMedia,
  Typography,
  IconButton,
  styled,
  Box,
  Link,
  CardActions,
  CardActionArea,
  TextField
} from '@mui/material';
import { IoBagAddOutline } from "react-icons/io5";
import useMountedRef from "./useMountedRef.jsx";

const StyledCard = styled(Card)({
  maxWidth: '100%',
  transition: '0.3s',
  '&:hover': {
    boxShadow: '0px 4px 20px rgba(0, 0, 0, 0.2)',
  },
});

const StyledCardMedia = styled(CardMedia)({
  width: '90.5%',
  height: '100%',
  'object-fit': 'cover',
  'object-position': 'center',
  'border-radius': '3%',
  margin: '10px',
  '&:hover': {
       top: '-10px',
  },
});

const ProductCard = ({ id, image, name, price, quantity = 0 }) => {

  const mountedRef = useMountedRef();
  const [count, setCount] = useState(quantity);

  useEffect(() => {
    const fetchData = async () => {
      const response = await axios.put('/api/cart/add?id=' + id + '&quantity=' + count, null, { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
      setCount(response.data.quantity);
    };
    if (mountedRef.current) {
      fetchData();
    }
  }, [count]);

  return (
    <StyledCard>
      <CardActionArea onClick={() => window.location.href = "/product/" + id}>
        <StyledCardMedia
          component="img"
          image={image}
          alt={name}
        />
        <CardContent>
          <Box display="flex"
               flexDirection="column"
               alignItems="flex-start">
            <Box display="flex" alignItems="center">
              <Typography
                variant="body1"
                style={{
                  fontWeight: 400,
                  color: 'rgba(0, 0, 0, 0.6)',
                  marginRight: '8px',
                }}>
                  Цена:
              </Typography>
              <Typography
                  variant="body1"
                  style={{
                      fontWeight: 700,
                  }}>
                  {price} ₽
              </Typography>
            </Box>
            <Box display="flex"
                 flexDirection="row"
                 alignItems="flex-start"
                 justifyContent="space-between"
                 width="-webkit-fill-available">
              <Typography
                  variant="body1"
                  style={{
                      fontWeight: 500,
                  }}>
                  {name}
              </Typography>
              </Box>
          </Box>
        </CardContent>
      </CardActionArea>
        <CardActions sx={{ justifyContent: "center" }}>
          {count > 0 ? (
            <>
              <IconButton onClick={() => {
                  setCount((c) => c - 1);
                  }} color="primary" aria-label="decrement">
                -
              </IconButton>
              <TextField
                type="number"
                value={count}
                inputProps={{ min: 0 }}
                variant="outlined"
                size="small"
                style={{ width: '50px', margin: '0 8px' }}
              />
              <IconButton onClick={() => {
                  setCount((c) => c + 1);
                  }} color="primary" aria-label="increment">
                +
              </IconButton>
            </>
          ) : (
            <IconButton onClick={() => {
                setCount((c) => c + 1);
            }} aria-label="to-cart" color="primary">
              <IoBagAddOutline />
            </IconButton>
          )}
      </CardActions>
    </StyledCard>
  );
};

export default ProductCard;
