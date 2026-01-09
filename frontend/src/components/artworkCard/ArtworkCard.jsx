import './ArtworkCard.css';
import FavoriteButton from "../favoriteButton/FavoriteButton.jsx";
import {useFavorites} from "../../context/FavoritesProvider.jsx";


function ArtworkCard({id, title, image, alt, price}) {

    const {favoriteIds, toggleFavorite} = useFavorites()
    const isFavorite = favoriteIds.includes(id);

    return (
        <div className="card-wrapper">
            <img src={image} alt={alt}/>
            <FavoriteButton
                isFavorite={isFavorite}
                onToggle={() => toggleFavorite(id)}
            />
            <h4>{title}</h4>
            <p>{price}</p>
        </div>
    )
}

export default ArtworkCard;