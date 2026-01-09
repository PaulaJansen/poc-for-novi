import "./ArtworkCard.css";
import FavoriteButton from "../favoriteButton/FavoriteButton.jsx";
import {useFavorites} from "../../context/FavoritesProvider.jsx";


function ArtworkCard({id, title, image, alt, price}) {

    const {favoriteIds, toggleFavorite} = useFavorites()
    const isFavorite = favoriteIds.includes(id);

    return (
        <div className="card-wrapper">
            <img src={image} alt={alt || "Geen afbeelding beschikbaar"}/>
            <FavoriteButton
                isFavorite={isFavorite}
                onToggle={() => toggleFavorite(id)}
            />
            <h4>{title || "Titel onbekend"}</h4>
            <p>{price ? `€{price}` : "Prijs onbekend"}</p>
        </div>
    )
}

export default ArtworkCard;