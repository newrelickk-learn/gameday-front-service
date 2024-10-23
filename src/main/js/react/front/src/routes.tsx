import { BrowserRouter, Route, Routes } from "react-router-dom";
import {Index} from "./pages";
import {AdminPage} from "./pages/admin";
import {ProductPage} from "./pages/product";

export const RouterConfig = () => {
    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Index />} />
                    <Route path="/product/:id" element={<ProductPage />} />
                    <Route path="/admin" element={<AdminPage />} />
                </Routes>
            </BrowserRouter>
        </>
    );
};