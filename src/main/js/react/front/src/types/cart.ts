export type Cart = {
    id: number
    amount: number
    totalPrice: number
    items: CartItem[]
}

export type CartItem = {
    id: number
    amount: number
    product: Product
}

export type Product = {
    id: string
    name: string
    description: string
    imageUrl: string[]
    price: number
    amount: number
}
