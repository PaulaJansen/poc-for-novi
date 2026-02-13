import "./ArtworkCard.css";
import FavoriteButton from "../favoriteButton/FavoriteButton.jsx";
import {Link} from "react-router-dom";
import editBrush from "../../assets/paint-brush-broad.svg";
import {useContext} from "react";
import {AuthContext} from "../../context/AuthContext.js";
import {FavoritesContext} from "../../context/FavoritesContext.js";

function ArtworkCard({id, title, image, alt, price, isPlaceholder, onEdit, onToggleFavorite, isFavoriteProp}) {

    const {favoriteIds, toggleFavorite} = useContext(FavoritesContext);
    const {auth} = useContext(AuthContext);

    const isFavorite = typeof isFavoriteProp === "boolean"
        ? isFavoriteProp
        : favoriteIds.includes(Number(id));

    const isVisitor = auth?.user?.roleNames?.includes("VISITOR");

    return (
        <Link to={`/artwork/${id}`} className="card-link">
            <div className="card-wrapper">
                <div className="card-image-wrapper">
                    <img className="card-image" src={image} alt={alt || "Geen afbeelding beschikbaar"}/>
                </div>
                {!isPlaceholder && !onEdit && isVisitor && (
                    <FavoriteButton
                        isFavorite={isFavorite}
                        onToggle={onToggleFavorite ? onToggleFavorite : () => toggleFavorite(id)}
                        favoriteClassName="favorite-artwork-card"
                    />
                )}
                <div className="card-text-wrapper">
                    <h3>{title || "Titel onbekend"}</h3>
                    <p>{price ? `€${price}` : "Prijs onbekend"}</p>
                </div>

                {onEdit && (
                    <div className="edit-artwork-button"
                         onClick={(e) => {
                             e.preventDefault();
                             onEdit(id);
                         }}
                    >
                        <img src={editBrush} alt="edit"/>
                    </div>
                )}
            </div>
        </Link>
    )
}

export default ArtworkCard;