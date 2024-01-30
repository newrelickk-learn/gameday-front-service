import React, {FC, useState, useEffect, useContext, Fragment, useCallback} from 'react';
import {API} from "../utils/api";
import {CartContext} from "../contexts/context";
import {Cart} from "../types/cart";
import {
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Table,
    TableBody, TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@mui/material";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import {Order} from "../types/order";

interface CartDetailProps {
    cart: Cart,
    onCloseCart: () => void,
    onNext: () => void,
}
export const CartDetail: FC<CartDetailProps> = ({ cart, onCloseCart, onNext }) => {

    const handleNext = useCallback(() => {
           onNext();
    }, []);

    return (
        <Fragment>
        <DialogTitle id="alert-dialog-title">
            {"カート"}
        </DialogTitle>
    <DialogContent>

    <TableContainer>
            <Table sx={{minWidth: 450}} aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <TableCell></TableCell>
                        <TableCell>商品</TableCell>
                        <TableCell>単価</TableCell>
                        <TableCell>点数</TableCell>
                        <TableCell>金額</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {cart?.items?.map(item => (
                        <TableRow
                            key={item.product.id}
                            sx={{'&:last-child td, &:last-child th': {border: 0}}}
                        >
                            <TableCell component="th" scope="row">
                                <img height={64} width={"auto"}
                                     src={`https://demo.sockshop.nrkk.technology${item.product?.imageUrl && item.product.imageUrl.length > 0 ? item.product.imageUrl[0] : ''}`}
                                     alt={item.product.name}/>
                            </TableCell>
                            <TableCell component="th" scope="row">
                                {item.product.name}
                            </TableCell>
                            <TableCell align="center">{item.product.price}</TableCell>
                            <TableCell align="center">{item.amount}</TableCell>
                            <TableCell align="center">{item.product.price * item.amount}</TableCell>
                        </TableRow>
                    ))}
                    {cart && (
                        <TableRow
                            key={cart.id}
                            sx={{'&:last-child td, &:last-child th': {border: 0}}}
                        >
                            <TableCell colSpan={3} align="right">
                                <Typography gutterBottom variant="h5" component="div">
                                    合計
                                </Typography>
                            </TableCell>
                            <TableCell align="center">
                                <Typography gutterBottom variant="h5" component="div">
                                    {cart.amount}
                                </Typography>
                            </TableCell>
                            <TableCell align="center">
                                <Typography gutterBottom variant="h5" component="div">
                                    {cart.totalPrice}
                                </Typography>
                            </TableCell>
                        </TableRow>
                    )}
                </TableBody>
            </Table>
        </TableContainer>
    </DialogContent>
            <DialogActions>
                <Button onClick={onCloseCart}>戻る</Button>
                <Button onClick={handleNext} autoFocus>
                    注文に進む
                </Button>
            </DialogActions>
        </Fragment>
    );
}
