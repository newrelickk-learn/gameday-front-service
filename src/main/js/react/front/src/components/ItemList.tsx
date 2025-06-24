import React, {FC, useState, useEffect, useCallback} from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import {API} from "../utils/api";
import {Item} from "./Item";
import {Alert, Snackbar} from "@mui/material";

interface ItemListProps {
    onAddItem : (cart: any)=>void,
    tags: Array<string>,
    searchQuery: string,
}
export const ItemList: FC<ItemListProps> = ({ onAddItem, tags, searchQuery }) => {

    const [items, setItems] = useState([])
    const [description, setDescription] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const handleCloseAlert = useCallback(async () => {
        setErrorMessage("")
        setDescription("")
    }, [setErrorMessage])

    useEffect(() => {

        if (searchQuery.length > 0) {
            API.post(`/catalogue/search`, {query: searchQuery}).then((data) => {
                setItems(data.products);
                setDescription(data.description);
            }, (error) => {
                setErrorMessage("エラーが発生しました。しばらくお待ちください。ERR-TAG001")
                throw error
            })
        } else {
            API.get(`/catalogue/items?tags=${tags.join(',')}`).then((data) => {
                setItems(data);
            }, (error) => {
                setErrorMessage("エラーが発生しました。しばらくお待ちください。ERR-TAG001")
                throw error
            })
        }
    }, [tags, searchQuery, setItems, setErrorMessage, setDescription])

    return (
        <>
            <Box sx={{
                position: 'relative',
                display: 'flex',
                flexDirection: 'row',
                flexWrap: 'wrap',
                width: '1500px',
                marginLeft: 'auto',
                marginRight: 'auto'
            }}>
                {items.map((item: any, idx: number) => (
                    <Item key={`item-${idx}`} item={item} onAddItem={(cart) => onAddItem(cart)}/>))}
            </Box>
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
            <Snackbar open={description.length > 0} autoHideDuration={120000} onClose={handleCloseAlert}>
                <Alert
                    onClose={handleCloseAlert}
                    severity="success"
                    variant="filled"
                    sx={{width: '100%'}}
                >
                    {description}
                </Alert>
            </Snackbar>
        </>
    );
}
