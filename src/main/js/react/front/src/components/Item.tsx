import React, {FC, useState, useEffect, useCallback} from 'react';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import {API} from "../utils/api";
import {Alert, Snackbar} from "@mui/material";
import { useNavigate } from "react-router-dom"

interface ItemProps {
    item: any,
    onAddItem: (cart: any)=>void,
}
export const Item: FC<ItemProps> = ({ item, onAddItem }) => {

    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState("")
    const handleCloseAlert = useCallback(async () => {
        setErrorMessage("")
    }, [setErrorMessage])

    const handleAddItem = useCallback(async (productId: string) => {
        const cart = await API.post('/cart/add', {productId, amount: 1})
        if (cart === undefined) {
            setErrorMessage("エラーが発生しました。管理者にお問い合わせください。")
        } else {
            onAddItem(cart)
        }
    }, [setErrorMessage])

    return (
        <>
            <Card sx={{maxWidth: 345, margin: '16px', flexBasis: '500px', display: 'flex', flexDirection: 'column'}}>
                <CardActionArea sx={{flexGrow: 1, display: 'flex', flexDirection: 'column'}}>
                    <CardMedia
                        component="img"
                        height="280"
                        image={`https://demo.sockshop.nrkk.technology${item.imageUrl && item.imageUrl.length > 0 ? item.imageUrl[0] : ''}`}
                        alt="green iguana"
                    />
                    <CardContent sx={{flexGrow: 1}} onClick={()=>navigate(`/product/${item.id}`)}>

                        <Typography gutterBottom variant="h5" component="div">
                            {item.name}
                        </Typography>
                        <Typography gutterBottom variant="body1" component="div">
                            {item.price}円(在庫数 {item.count})
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            {item.description}
                        </Typography>
                    </CardContent>
                </CardActionArea>
                <CardActions>
                    <Button id={`item-${item.id}`} size="small" color="primary" onClick={() => handleAddItem(item.id)}>
                        カートに追加する
                    </Button>
                </CardActions>
            </Card>
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
        </>
    );
}
