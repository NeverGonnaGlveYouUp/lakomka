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
  CardActions,
  CardActionArea,
  TextField,
  Stack
} from '@mui/material';
import { IoBagAddOutline, IoStar } from "react-icons/io5";
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
  width: 'calc(100% - 20px)',
  aspectRatio: '1 / 1',
  objectFit: 'cover',
  objectPosition: 'center',
  borderRadius: '12px',
  margin: '10px',
  backgroundColor: '#f0f0f0',
  transition: 'transform 0.3s ease-in-out, opacity 0.5s ease-in',
  position: 'sticky',
  top: '1px',

  opacity: 0,
  '&[src]': {
    opacity: 1,
  },

  '&:hover': {
    transform: 'translateY(-5px) scale(1.02)',
    boxShadow: '0 10px 20px rgba(0,0,0,0.1)',
  },
});

const ProductCard = ({ id, image, name, price, quantity, zn }) => {

  const mountedRef                    = useMountedRef();
  const [count, setCount]             = useState(null);
  const [oldCount, setOldCount]       = useState(null);
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
        setOldCount(0);
      } catch (error) {
        console.error(error);
      }
    };
    if (mountedRef.current && !isNaN(count) && ((count != quantity && oldCount != null) || (count == null && oldCount == null))) {
      fetchData();
    }
  }, [count]);

  return (
    <StyledCard sx={{ height: "-webkit-fill-available" }}>
      <CardActionArea onClick={() => {
              navigate("/product/" + id);
              window.scrollTo({ top: 0, behavior: "smooth" });
          }}>
        <StyledCardMedia
          component="img"
          image={image}
          alt={name}
          loading="lazy"
          onLoad={(e) => e.currentTarget.style.opacity = '1'}
        />
        {(zn == 2) && (
          <CardContent
            sx={{
              position: 'absolute',
              display: "inline-flex",
              bottom: "94px",
              width: "fit-content",
              alignItems: "center",
              left: "16px",
              padding: 0,
              backgroundColor: "#f1117eff",
              color: "#ffffffff",
              borderRadius: "8px"}}>
            <IoStar sx={{ padding: "4px" }}/>
            <Typography sx={{ fontWeight: 150, mr: "8px" }} component="div">
              Новинка
            </Typography>
          </CardContent>
        )}
        <CardContent sx={{ padding: "0px 16px 4px"}}>
          <Box display="flex"
               flexDirection="column"
               alignItems="flex-start">
            <Box display="flex" alignItems="center">
              <Typography
                  variant="body1"
                  sx={{
                      fontWeight: 700,
                      color: "rgba(16, 196, 76, 1)"
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
                  sx={{
                      fontWeight: 500,
                      fontSize: { lg: '18px', md: '22px' },
                      display: "-webkit-box",
                      textTransform: "capitalize",
                      "-webkit-box-orient": "vertical",
                      "-webkit-line-clamp": "2",
                      textOverflow: "ellipsis",
                      overflow: "hidden"
                  }}>
                  {name + '\n\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0'}
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
                    setCount(parseInt(e.target.value, 10));
                }}
                onKeyPress={(event) => {
                    if (event?.key === '-' || event?.key === ',' || event?.key === '.') {
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
