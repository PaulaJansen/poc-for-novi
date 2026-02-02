import {FavoritesContext} from "./FavoritesContext.js";
import {useContext, useState} from "react";

export default function FavoritesContextProvider({children}) {

    const [favoriteIds, setFavoriteIds] = useState([]);

    function toggleFavorite(id) {
        setFavoriteIds(prev =>
            prev.includes(id)
                ? prev.filter(favoriteId => favoriteId !== id)
                : [...prev, id]
        );
    }

    return (
        <FavoritesContext.Provider value={{ favoriteIds, toggleFavorite }}>
            {children}
        </FavoritesContext.Provider>
    );
}

export function useFavorites() {
    return useContext(FavoritesContext);
}