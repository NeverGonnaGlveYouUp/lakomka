import axios from 'axios';
import React, { useState, useRef, useContext, useEffect } from 'react';
import { checkJWTExpiration } from './checkJWTExpiration.js';
import {
    Container,
    Box,
    Pagination,
    TextField,
    useMediaQuery,
    Typography,
    Grid,
    Card,
    CardContent,
    Paper
    } from "@mui/material";

const CartPageCard = ( { id } ) => {

    useEffect(() => {
    }, [id])

    return (
        <Card sx={{ display: 'flex' }}>

        </Card>
    );
}

const OrdersPage = () => {

    const [number, setNumber]               = useState(0);
    const [totalElements, setTotalElements] = useState();
    const [totalPages, setTotalPages]       = useState();
    const [orders, setOrders]               = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            checkJWTExpiration();
            const response = await axios.get('/api/orders/list?page=' + number,
                { headers: { Authorization: localStorage.getItem('jwtToken') ? 'Bearer ' + localStorage.getItem('jwtToken') : null } })
            setTotalElements(response.data.totalElements);
            setTotalPages(response.data.totalPages);
            setNumber(response.data.number);
            setOrders(response.data.content);
        }
        fetchData();
    }, [])

    return(
        <Container maxWidth="lg" sx={{ mt: 3, display: "flex", gap: "4rem", flexDirection: "row" }}>
            <Container sx={{ mt: 3, display: "flex", gap: "2rem", flexDirection: "column" }}>
                <Grid container spacing={2}>
                    {orders.map(item => (
                        <Grid item xs={12} sm={6} md={4} key={item.id}>
                            <Card onClick={() => onItemSelect(item.id)}>
                                <CardContent>
                                    <Typography variant="h6">№ {item.id} • {item.quantity} </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                        ))}
                </Grid>
                {totalElements > 6 && (
                    <Pagination
                        style={{
                            'justifyItems': 'center',
                            'margin': '20px 0 40px 0',
                        }}
                        page={number + 1}
                        siblingCount={2}
                        boundaryCount={2}
                        count={totalPages}
                        onChange={() => setNumber(value - 1)}
                        color="primary"
                        shape="rounded"/>
                )}
            </Container>
            <Container sx={{ mt: 3, display: "flex", gap: "2rem", flexDirection: "column" }}>
            </Container>
        </Container>
    );

};

const ItemDetail = ({ itemId }) => {
  const [item, setItem] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      if (itemId) {
        const result = await axios.get(`https://api.example.com/items/${itemId}`);
        setItem(result.data);
      }
    };
    fetchData();
  }, [itemId]);

  if (!item) {
    return <Typography variant="h6">Select an item to view details</Typography>;
  }

  return (
    <Paper style={{ padding: '16px' }}>
      <Typography variant="h4">{item.name}</Typography>
      <Typography variant="body1">{item.details}</Typography>
    </Paper>
  );
};



export default OrdersPage;