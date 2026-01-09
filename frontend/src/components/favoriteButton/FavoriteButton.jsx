import "./FavoriteButton.css";
import favorite from "../../assets/heart-fill.svg";
import noFavorite from "../../assets/heart-thin.svg";

function FavoriteButton({isFavorite, onToggle}) {
    return (
        <button onClick={onToggle}
                aria-label={
                    isFavorite
                        ? "Verwijderen uit favorieten"
                        : "Toevoegen aan favorieten"
                }
                aria-pressed={isFavorite}
        >
            {isFavorite ? favorite : noFavorite}
        </button>
    );
}

export default FavoriteButton;