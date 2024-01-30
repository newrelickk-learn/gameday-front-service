import { BrowserRouter, Route, Routes } from "react-router-dom";
import {Index} from "./pages";
import {AdminPage} from "./pages/admin";

export const RouterConfig = () => {
    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Index />} />
                    <Route path="/admin" element={<AdminPage />} />
                </Routes>
            </BrowserRouter>
        </>
    );
};