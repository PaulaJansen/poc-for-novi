import "./ArtworkCard.css";
import FavoriteButton from "../favoriteButton/FavoriteButton.jsx";
import {useFavorites} from "../../context/FavoritesProvider.jsx";
import {Link} from "react-router-dom";

function ArtworkCard({id, title, image, alt, price, isPlaceholder}) {

    const {favoriteIds, toggleFavorite} = useFavorites()
    const isFavorite = favoriteIds.includes(id);

    return (
        <Link to={`/artwork/${id}`} className="card-link">
            <div className="card-wrapper">
                <div className="card-image-wrapper">
                    <img className="card-image" src={image} alt={alt || "Geen afbeelding beschikbaar"}/>
                </div>
                {!isPlaceholder && (
                    <FavoriteButton
                        isFavorite={isFavorite}
                        onToggle={() => toggleFavorite(id)}
                    />
                )}
                <div className="card-text-wrapper">
                    <h3>{title || "Titel onbekend"}</h3>
                    <p>{price ? `€${price}` : "Prijs onbekend"}</p>
                </div>
            </div>
        </Link>
    )
}

export default ArtworkCard;