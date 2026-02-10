import {FavoritesContext} from "./FavoritesContext.js";
import {useContext, useEffect, useState} from "react";
import {AuthContext} from "./AuthContext.js";
import {toast} from "react-toastify";
import axios from "axios";

export default function FavoritesContextProvider({children}) {

    const [favoriteIds, setFavoriteIds] = useState([]);
    const {auth} = useContext(AuthContext);

    useEffect(() => {
        if (!auth?.user) {
            setFavoriteIds([]);
            return;
        }

        setFavoriteIds(auth.user.favoriteArtworkIds || []);
    }, [auth]);

    async function toggleFavorite(artworkId) {
        if (!auth || !auth.user) {
            toast.info("Log eerst in om kunst als favoriet op te slaan!", {
                position: "top-center",
                autoClose: 3000,
            });
            return;
        }

        const userId = auth.user.id;
        const currentFavorites = favoriteIds || [];
        const isFavorite = currentFavorites.includes(artworkId);

        try {
            const url = isFavorite
                ? `http://localhost:8080/visitors/${userId}/favorites/${artworkId}/remove`
                : `http://localhost:8080/visitors/${userId}/favorites/${artworkId}/add`;

            const response = await axios.patch(url, null, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
            });

            setFavoriteIds((response.data.favoritesIds || []).map(id => Number(id)));
        } catch (e) {
            console.error(e);
            toast.error("Favoriet aanpassen mislukt");
        }
    }

    return (
        <FavoritesContext.Provider value={{favoriteIds, toggleFavorite, setFavoriteIds}}>
            {children}
        </FavoritesContext.Provider>
    );
}