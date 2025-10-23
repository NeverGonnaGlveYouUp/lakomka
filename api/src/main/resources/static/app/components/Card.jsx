import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import {
  Card,
  CardContent,
  CardMedia,
  Typography,
  IconButton,
  styled,
  Box,
  CardActions,
  CardActionArea,
  TextField,
  Stack
} from '@mui/material';
import { IoBagAddOutline } from "react-icons/io5";
import useMountedRef from "./useMountedRef.jsx";
import { checkJWTExpiration } from './checkJWTExpiration.js';
import { useAppContext } from './AppContext.js';
import { useNavigate } from "react-router-dom";

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

const ProductCard = ({ id, image, name, price, quantity }) => {

  const mountedRef                    = useMountedRef();
  const [count, setCount]             = useState(0);
  const [oldCount, setOldCount]       = useState(0);
  const { setContextCount }           = useAppContext();
  const navigate                      = useNavigate();

  useEffect(() => {
    setCount(quantity);
  }, [id])

  useEffect(() => {
    const fetchData = async () => {
      try {
        checkJWTExpiration();
        const response = await axios.put('/api/cart/add?id=' + id + '&quantity=' + (count === '' ? 0 : count), null,
        { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } });
        if (oldCount < response.data.quantity) {
          setContextCount((c) => c + (response.data.quantity - oldCount));
        } else {
          setContextCount((c) => c - (oldCount - response.data.quantity));
        }
      } catch (error) {
        console.error(error);
      }
    };
    if (mountedRef.current && !isNaN(count) && quantity != count) {
      fetchData();
    }
  }, [count]);

  return (
    <StyledCard>
      <CardActionArea onClick={() => navigate("/main/product/" + id)}>
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
            <Stack direction="row" spacing={0}>
              <IconButton onClick={() => {
                  setOldCount(count);
                  setCount((c) => c - 1);
                  }} color="primary" aria-label="decrement">
                -
              </IconButton>
              <TextField
                type="number"
                value={count}
                onChange={(e) => {
                    setOldCount(count);
                    setCount(e.target.value);
                }}
                onKeyPress={(event) => {
                    if (event?.key === '-' || event?.key === ',' || event?.key === '.' || event?.key === '0') {
                      event.preventDefault();
                    }
                }}
                inputProps={{ min: 0, style: { textAlign: 'center' } }}
                label="Кол-во"
                variant="standard"
                size="small"
                fullWidth
                style={{ margin: '0 8px' }}
              />
              <IconButton onClick={() => {
                  setOldCount(count);
                  setCount((c) => c + 1);
                  }} color="primary" aria-label="increment">
                +
              </IconButton>
            </Stack>
          ) : (
            <IconButton onClick={() => {
                setOldCount(0);
                setCount(1);
            }} aria-label="to-cart" color="primary">
              <IoBagAddOutline />
            </IconButton>
          )}
      </CardActions>
    </StyledCard>
  );
};

export default ProductCard;
