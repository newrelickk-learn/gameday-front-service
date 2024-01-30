import React, {useCallback, useEffect, useState} from "react";
import {GlobalHeader} from "../components/GlobalHeader";
import {ItemList} from "../components/ItemList";
import {Cart} from "../types/cart";
import {CartContext} from "../contexts/context";
import {API} from "../utils/api";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import {Checkbox, FormControlLabel, FormGroup} from "@mui/material";

export const Index = () => {
    const [cart, setCart] = useState<Cart>({id: 0, amount: 0, totalPrice: 0, items: []})
    const [tags, setTags] = useState<Array<string>>([])
    const [selectedTags, setSelectedTags] = useState<Array<string>>([])

    useEffect(() => {
        API.get(`/catalogue/tags`).then((data: {tags: string[]}) => {
            setTags(data.tags);
        })
    }, [setTags])

    const handleUpdateCart = useCallback((cart: Cart) => {
        setCart(cart)
    }, [setCart])

    const handleClickTag = useCallback((tag: string) => {
        const newSelectedTags = JSON.parse(JSON.stringify(selectedTags))
        const existedIndex = newSelectedTags.findIndex((s: string) =>s === tag)
        if (existedIndex >= 0) {
            newSelectedTags.splice(existedIndex, 1)
        } else {
            newSelectedTags.push(tag)
        }
        setSelectedTags(newSelectedTags)
    }, [selectedTags, setSelectedTags])

    return (
        <CartContext.Provider value={cart}>
            <GlobalHeader onLoadCart={(newCart) => handleUpdateCart(newCart)}/>
            <div className="main">
                <Card sx={{ maxWidth: 345, margin: '16px', flexBasis: '500px', display: 'flex', flexDirection: 'column' }}>
                    <CardContent sx={{flexGrow: 1}}>
                        <FormGroup>
                            {tags.map((tag: string) => (
                                <FormControlLabel key={`tag-${tag}`} control={<Checkbox onClick={()=> handleClickTag(tag)} checked={selectedTags.find(s=>s === tag) !== undefined} />} label={tag} />
                            ))}
                        </FormGroup>
                    </CardContent>
                </Card>
                <ItemList onAddItem={handleUpdateCart} tags={selectedTags}/>
            </div>
        </CartContext.Provider>

    )
}