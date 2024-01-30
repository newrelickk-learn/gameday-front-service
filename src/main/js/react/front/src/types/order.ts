export enum OrderStage {
    NEW = "NEW",
    CONFIRM = "CONFIRM",
    PURCHASED = "PURCHASED",
    SHIPPED = "SHIPPED",
    DELIVERED = "DELIVERED",
}
export type Order = {
    id: number
    couponCode?: string
    paymentType?: string
    orderStage: OrderStage
}
