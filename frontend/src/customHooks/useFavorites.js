import {useContext} from "react";
import {FavoritesContext} from "../context/FavoritesContext.js";

export function useFavorites() {
    const context = useContext(FavoritesContext);

    if (!context) {
        throw new Error("useFavorites must be used within FavoritesContextProvider");
    }

    return context;
}