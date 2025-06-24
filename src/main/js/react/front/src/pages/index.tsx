import React, {useCallback, useEffect, useState} from "react";
import {GlobalHeader} from "../components/GlobalHeader";
import {ItemList} from "../components/ItemList";
import {Cart} from "../types/cart";
import {CartContext} from "../contexts/context";
import {API} from "../utils/api";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import {Alert, Checkbox, FormControlLabel, FormGroup, InputAdornment, Snackbar, TextField} from "@mui/material";
import CardMedia from "@mui/material/CardMedia";
import SearchIcon from '@mui/icons-material/Search';
import debounce from 'lodash.debounce';

export const Index = () => {
    const [cart, setCart] = useState<Cart>({id: 0, amount: 0, totalPrice: 0, items: []})
    const [tags, setTags] = useState<Array<string>>([])
    const [selectedTags, setSelectedTags] = useState<Array<string>>([])
    const [errorMessage, setErrorMessage] = useState("")
    const [searchQuery, setSearchQuery] = useState("")
    const handleCloseAlert = useCallback(async () => {
        setErrorMessage("")
    }, [setErrorMessage])

    useEffect(() => {
        API.get(`/catalogue/tags`).then((data: {tags: string[]}) => {
            setTags(data.tags);
        }, (error) => {
            setErrorMessage("エラーが発生しました。しばらくお待ちください。ERR-COMMON-TAG001")
        })
    }, [setTags, setErrorMessage])

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

    const handleChangeSearch = debounce(useCallback((searchQuery: string) => {
        setSearchQuery(searchQuery)
    }, [setSearchQuery]), 500)

    return (
        <CartContext.Provider value={cart}>
            <GlobalHeader onLoadCart={handleUpdateCart}/>
            <CardMedia
                component="img"
                sx={{ width: '100%' }}
                image={`https://demo.sockshop.nrkk.technology/img/top_banner.png`}
                alt="green iguana"
            />
            <TextField
                slotProps={{
                    input: {
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon />
                            </InputAdornment>
                        ),
                    },
                }}
                onChange={(e) => handleChangeSearch(e.target.value)} />
            <div className="main">
                <Card sx={{ maxWidth: 345, margin: '16px', flexBasis: '500px', display: 'flex', flexDirection: 'column' }}>
                    <CardContent sx={{flexGrow: 1}}>
                        <FormGroup>
                            {tags.map((tag: string) => (
                                <FormControlLabel key={`tag-${tag}`} control={<Checkbox id={`tag-${tag}`} onClick={()=> handleClickTag(tag)} checked={selectedTags.find(s=>s === tag) !== undefined} />} label={tag} />
                            ))}
                        </FormGroup>
                    </CardContent>
                </Card>
                <ItemList onAddItem={handleUpdateCart} tags={selectedTags} searchQuery={searchQuery}/>
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