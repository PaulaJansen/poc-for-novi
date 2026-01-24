import "./FavoriteButton.css";
import favorite from "../../assets/heart-fill.svg";
import noFavorite from "../../assets/heart-duotone.svg";

function FavoriteButton({isFavorite, onToggle}) {
    return (
        <button className="favorite-button"
                onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    onToggle();
                }}
                aria-label={
                    isFavorite
                        ? "Verwijderen uit favorieten"
                        : "Toevoegen aan favorieten"
                }
                aria-pressed={isFavorite}
        >
            <img
                src={isFavorite ? favorite : noFavorite}
                alt={isFavorite ? "Favoriet" : "Niet favoriet"}
                className="favorite-icon"
            />
        </button>
    );
}

export default FavoriteButton;