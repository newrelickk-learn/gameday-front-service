import React, {FC, Fragment, useCallback, useEffect, useState} from 'react';
import {API} from "../utils/api";
import {Cart} from "../types/cart";
import {DialogActions, DialogContent, DialogTitle, MenuItem, TextField} from "@mui/material";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import {Order, OrderStage} from "../types/order";

interface OrderFormProps {
    cart: Cart,
    onCloseCart: () => void,
    onNext: () => void,
}
export const OrderForm: FC<OrderFormProps> = ({ cart, onCloseCart, onNext }) => {

    const [order, setOrder] = useState<Order>();

    useEffect(() => {
        API.get(`/order`).then((data) => {
            setOrder(data);
        })
    }, [setOrder])

    const handleChangeCouponCode = useCallback((code: string) => {
        const newOrder = JSON.parse(JSON.stringify(order)) as Order
        newOrder.couponCode = code
        setOrder(newOrder)
    }, [order, setOrder]);

    const handleChangePaymentType = useCallback((type: string) => {
        const newOrder = JSON.parse(JSON.stringify(order)) as Order
        newOrder.paymentType = type
        setOrder(newOrder)
    }, [order, setOrder]);

    const handleNext = useCallback(() => {
        const newOrder = JSON.parse(JSON.stringify(order)) as Order
        if (order?.orderStage === OrderStage.NEW) {
            if (!order.paymentType) {
                order.paymentType = 'cache'
            }
            API.post(`/order/confirm`, order).then((data) => {
                setOrder(data);
            })
        } else if (order?.orderStage === OrderStage.CONFIRM) {
            API.post(`/order/purchase`).then((data) => {
                setOrder(data);
            })
        } else if (order?.orderStage === OrderStage.PURCHASED) {
            onNext()
        }
    }, [order, setOrder, onNext]);
    return (
        <Fragment>
            <DialogTitle id="alert-dialog-title">
                {order?.orderStage === OrderStage.NEW && "注文"}
                {order?.orderStage === OrderStage.CONFIRM && "注文 - 確認"}
                {order?.orderStage === OrderStage.PURCHASED && "注文 - 完了"}
            </DialogTitle>
            <DialogContent>

            <Box sx={{width: '100%', height: '120px', display: 'flex'}}>
                {order?.orderStage !== OrderStage.PURCHASED && (<>
                <TextField helperText="クーポンコード" disabled={order?.orderStage !== OrderStage.NEW} defaultValue={order?.couponCode ?? ''} onChange={(e) => handleChangeCouponCode(e.target.value)}/>
                <TextField
                    select
                    label="お支払い方法"
                    defaultValue={order?.paymentType ?? "cache"}
                    disabled={order?.orderStage !== OrderStage.NEW}
                    onChange={(e) => handleChangePaymentType(e.target.value)}
                    helperText="お支払い方法を選択してください"
                >
                    <MenuItem value={"cache"}>
                        代引き
                    </MenuItem>
                    <MenuItem value={"card"}>
                        クレジットカード
                    </MenuItem>
                    <MenuItem value={"point"}>
                        ポイント
                    </MenuItem>
                </TextField>
                </>)}
            </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onCloseCart}>閉じる</Button>
                {order?.orderStage !== OrderStage.PURCHASED && (
                <Button onClick={handleNext} autoFocus>
                    {order?.orderStage === OrderStage.NEW && "確認"}
                    {order?.orderStage === OrderStage.CONFIRM && "注文"}
                </Button>
                    )}
            </DialogActions>
        </Fragment>
    );
}
