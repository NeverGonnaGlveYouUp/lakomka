import React from 'react';
import {
  Card,
  CardContent,
  CardMedia,
  Typography,
  IconButton,
  styled,
  Box
} from '@mui/material';
import { IoBagAddOutline } from "react-icons/io5";

const StyledCard = styled(Card)({
  maxWidth: '100%',
  transition: '0.3s',
  '&:hover': {
    boxShadow: '0px 4px 20px rgba(0, 0, 0, 0.2)',
  },
});

const StyledCardMedia = styled(CardMedia)({
  width: '93%',
  height: '100%',
  'object-fit': 'cover',
  'object-position': 'center',
  'border-radius': '3%',
  margin: '10px',
  '&:hover': {
       top: '-10px',
  },
});

const ProductCard = ({ image, name, price }) => {
  return (
    <StyledCard>
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
            <IconButton  aria-label="to-cart" color="primary">
                <IoBagAddOutline />
            </IconButton >
          </Box>
        </Box>
      </CardContent>
    </StyledCard>
  );
};

export default ProductCard;
