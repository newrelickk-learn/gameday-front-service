import React, {FC, useState, useEffect, useContext, Fragment, useCallback} from 'react';
import {API} from "../utils/api";
import {CartContext} from "../contexts/context";
import {Cart} from "../types/cart";
import {
    Alert,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle, Snackbar,
    Table,
    TableBody, TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@mui/material";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import {CartDetail} from "./CartDetail";
import {OrderStage} from "../types/order";
import {OrderForm} from "./OrderForm";
import NrAgent from "../o11y/newrelic";

interface GlobalHeaderProps {
    onLoadCart: (cart: any)=>void,
}
export const GlobalHeader: FC<GlobalHeaderProps> = ({ onLoadCart }) => {
    const [openCart, setOpenCart] = React.useState(false);
    const [stage, setStage] = React.useState<string>("");
    const cart = useContext<Cart>(CartContext)
    const [errorMessage, setErrorMessage] = useState("")
    const handleCloseAlert = useCallback(async () => {
        setErrorMessage("")
    }, [setErrorMessage])

    useEffect(() => {
        API.post(`/api/user`).then((data) => {
            if (data !== undefined && data.id !== undefined) {
                NrAgent.setCustomAttribute('user', `uid_${data.id}`)
                NrAgent.setUserId(`uid_${data.id}`)
            }
            console.log(data)
        }, (error) => {
            setErrorMessage("エラーが発生しました。しばらくお待ちください。 ERR-CART001")
        })
    }, [onLoadCart, setErrorMessage])

    useEffect(() => {
        API.get(`/cart`).then((data) => {
            onLoadCart(data);
        }, (error) => {
            setErrorMessage("エラーが発生しました。しばらくお待ちください。 ERR-CART001")
        })
    }, [onLoadCart, setErrorMessage])

    const handleClickOpenCart = useCallback(() => {
        API.get(`/cart`).then((data) => {
            onLoadCart(data);
            setOpenCart(true);
        }, (error) => {
            setErrorMessage("エラーが発生しました。しばらくお待ちください。ERR-CART002")
        })
    }, [setOpenCart, setErrorMessage]);

    const handleCloseCart = useCallback(() => {
        setOpenCart(false);
    }, [setOpenCart]);

    const handleNext = useCallback((close = false) => {
        if (close) {
            handleCloseCart()
            return
        }
        if (stage === "") {
            setStage(OrderStage.NEW)
        }
    }, [stage, setStage, setOpenCart]);

    return (
        <Fragment>
            <Box sx={{width: '100%', height: '64px', display: 'flex'}}>
                <Box sx={{padding: '16px', flexGrow: 1, textAlign: 'left'}}>
                    <Typography gutterBottom variant="h5" component="div">
                        靴下屋さん
                    </Typography>
                </Box>
                <Box sx={{padding: '24px'}}>
                    <Button id={"go-to-cart"} size="small" color="primary" onClick={handleClickOpenCart}>
                        カート ({cart?.amount ?? 0})
                    </Button>
                </Box>
            </Box>
            <Dialog
                fullWidth={true}
                open={openCart}
                onClose={handleCloseCart}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                {stage === ""
                    ? (<CartDetail cart={cart} onCloseCart={handleCloseCart} onNext={() => handleNext()} />)
                    : (<OrderForm cart={cart} onCloseCart={handleCloseCart} onNext={() => handleNext()} />)
                }

            </Dialog>
            <Snackbar open={errorMessage.length > 0} autoHideDuration={10000} onClose={handleCloseAlert}>
                <Alert
                    onClose={handleCloseAlert}
                    severity="error"
                    variant="filled"
                    sx={{width: '100%'}}
                >
                    {errorMessage}
                </Alert>
            </Snackbar>
        </Fragment>
    );
}
