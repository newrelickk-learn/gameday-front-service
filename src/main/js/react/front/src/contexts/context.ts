import {Context, createContext} from 'react';
import {Cart} from "../types/cart";

interface CartContextType {
    cart: Cart;
    updateCart: (cart: Cart) => void;
}

export const CartContext: Context<CartContextType> = createContext<CartContextType>({
    cart: {id: 0, amount: 0, totalPrice: 0, items: []},
    updateCart: () => {}
})