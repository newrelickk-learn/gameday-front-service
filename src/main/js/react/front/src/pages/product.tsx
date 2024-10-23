import React, {useCallback, useEffect, useState} from "react";
import {GlobalHeader} from "../components/GlobalHeader";
import {ItemList} from "../components/ItemList";
import {Cart, Product} from "../types/cart";
import {CartContext} from "../contexts/context";
import {API} from "../utils/api";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import {Alert, Checkbox, FormControlLabel, FormGroup, Snackbar} from "@mui/material";
import {useParams} from "react-router-dom";
import {Item} from "../components/Item";


export const ProductPage = () => {
    const urlParams = useParams<{ id: string }>()
    const [cart, setCart] = useState<Cart>({id: 0, amount: 0, totalPrice: 0, items: []})
    const [item, setItem] = useState<Product>()
    const [selectedTags, setSelectedTags] = useState<Array<string>>([])
    const [errorMessage, setErrorMessage] = useState("")
    const handleCloseAlert = useCallback(async () => {
        setErrorMessage("")
    }, [setErrorMessage])

    useEffect(() => {
        API.get(`/catalogue/item/${urlParams.id}`).then((data: Product) => {
            setItem(data);
        }, (error) => {
            setErrorMessage("エラーが発生しました。しばらくお待ちください。ERR-COMMON-TAG001")
        })
    }, [setItem, setErrorMessage])

    const handleUpdateCart = useCallback((cart: Cart) => {
        setCart(cart)
    }, [setCart])

    return (
        <CartContext.Provider value={cart}>
            <GlobalHeader onLoadCart={handleUpdateCart}/>
            <div className="main">
                {item !== undefined && <Item item={item} onAddItem={handleUpdateCart}/>}
            </div>
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
        </CartContext.Provider>
    )
}