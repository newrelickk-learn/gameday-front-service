import {FC, useState, useEffect, useCallback} from 'react';
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

interface ItemListProps {
    onAddItem : (cart: any)=>void,
    tags: Array<string>,
}
export const ItemList: FC<ItemListProps> = ({ onAddItem, tags }) => {

    const [items, setItems] = useState([])

    useEffect(() => {

        API.get(`/catalogue/items?tags=${tags.join(',')}`).then((data) => {
            setItems(data);
        })
    }, [tags, setItems])

    return (
        <Box  sx={{ position: 'relative', display: 'flex', flexDirection: 'row', flexWrap: 'wrap', width: '1500px', marginLeft: 'auto', marginRight: 'auto' }}>
            {items.map((item: any, idx: number)=> (<Item key={`item-${idx}`} item={item} onAddItem={(cart) => onAddItem(cart)}/>))}
        </Box>
    );
}
