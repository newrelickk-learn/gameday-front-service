import {Context, createContext} from 'react';
import {Cart} from "../types/cart";

export const CartContext: Context<Cart|any> = createContext({})