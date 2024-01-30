import React, {useCallback, useEffect, useState} from "react";
import {GlobalHeader} from "../components/GlobalHeader";
import {ItemList} from "../components/ItemList";
import {Cart} from "../types/cart";
import {CartContext} from "../contexts/context";
import {API} from "../utils/api";
import {Order, OrderStage} from "../types/order";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";

export const AdminPage = () => {

    const [orders, setOrders] = useState<Order[]>([]);


    useEffect(() => {

        API.get(`/admin/order/list`).then((data) => {
            setOrders(data);
        })
    }, [setOrders])

    const handleShip = useCallback(async (id: number)=> {
        const order = await API.post(`/order/${id}/ship`);
        const newOrders: Order[] = JSON.parse(JSON.stringify(orders));
        const orderIndex = newOrders.findIndex(o => o.id === id)
        newOrders[orderIndex] = order
        setOrders(newOrders)
    }, [orders, setOrders])

    return (
        <Box  sx={{ position: 'relative', display: 'flex', flexDirection: 'row', flexWrap: 'wrap', width: '1500px', marginLeft: 'auto', marginRight: 'auto' }}>
        <TableContainer component={Paper} sx={{ margin: "16px" }}>
            <Table sx={{ minWidth: 650 }} aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <TableCell>ID</TableCell>
                        <TableCell align="center">状態</TableCell>
                        <TableCell align="center">支払い方法</TableCell>
                        <TableCell align="center">クーポン</TableCell>
                        <TableCell align="center">操作</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {orders?.map((order) => (
                        <TableRow
                            key={order.id}
                            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                        >
                            <TableCell component="th" scope="row">
                                {order.id}
                            </TableCell>
                            <TableCell align="center">{order.orderStage}</TableCell>
                            <TableCell align="center">{order.paymentType}</TableCell>
                            <TableCell align="center">{order.couponCode}</TableCell>
                            <TableCell align="center">
                                {order.orderStage === OrderStage.PURCHASED && (<Button variant="contained" onClick={() => handleShip(order.id)}>出荷</Button>)}
                                {order.orderStage === OrderStage.SHIPPED && (<Box>配達中</Box>)}
                                {order.orderStage === OrderStage.DELIVERED && (<Box>配達完了</Box>)}
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
        </Box>
    )
}