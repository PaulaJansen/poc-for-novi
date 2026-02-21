import './Artwork.css';
import {useContext, useEffect, useRef, useState} from "react";
import {Link, useLocation, useNavigate, useParams} from "react-router-dom";
import Spinner from "../../components/spinner/Spinner.jsx";
import Breadcrumbs from "../../components/breadCrumbs/BreadCrumbs.jsx";
import {toast} from "react-toastify";
import {FavoritesContext} from "../../context/FavoritesContext.js";
import {AuthContext} from "../../context/AuthContext.js";
import FavoriteButton from "../../components/favoriteButton/FavoriteButton.jsx";
import API from "../../helpers/api.js";

function Artwork() {

    const {id} = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const editedToastShown = useRef(false);

    const [artwork, setArtwork] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [activeImage, setActiveImage] = useState(null);

    const {favoriteIds, toggleFavorite} = useContext(FavoritesContext);
    const {auth} = useContext(AuthContext);

    const isVisitor = auth?.user?.roleNames?.includes("VISITOR");
    const artworkId = Number(id);
    const isFavorite = favoriteIds.includes(artworkId);

    useEffect(() => {
        if (!editedToastShown.current) {
            if (location.state?.created) {
                toast.success("Kunstwerk succesvol toegevoegd!",
                    {
                        duration: 3000,
                        position: "top-center",
                    });
            }

            if (location.state?.edited) {
                toast.success("Kunstwerk succesvol aangepast!",
                    {
                        duration: 3000,
                        position: "top-center",
                    });
                navigate(location.pathname, {replace: true});
            }

            if (location.state?.created || location.state?.edited) {
                editedToastShown.current = true;
                navigate(location.pathname, {replace: true});
            }
        }
    }, [location.state, location.pathname, navigate]);

    useEffect(() => {
        async function fetchArtwork() {
            try {
                const artworkResponse = await API.get(`/artworks/${id}`);
                const artworkData = artworkResponse.data;
                console.log(artworkData);
                setArtwork(artworkData);

                if (artworkResponse.data.images?.length) {
                    setActiveImage(artworkResponse.data.images[0]);
                }
            } catch (e) {
                console.error(e);
                setError("Kunstwerk ophalen mislukt")
            } finally {
                setLoading(false);
            }
        }

        fetchArtwork();
    }, [id]);

    useEffect(() => {
        if (artwork?.images?.length) {
            setActiveImage(artwork.images[0]);
        } else {
            setActiveImage(null);
        }
    }, [artwork]);

    if (loading) {
        return (
            <Spinner size="default" text="Kunstwerk wordt geladen"/>
        );
    }

    if (error) {
        return (
            <div className="artwork-container">
                <p className="error-message">{error}</p>
            </div>
        );
    }

    const images = artwork.images || [];

    return (
        <div className="artwork-container">
            <Breadcrumbs lastLabel={artwork.title}/>
            <section className="artwork-details-wrapper">
                <div className="artwork-gallery">
                    <h2 className="artwork-details-title">{artwork.title}</h2>
                    {activeImage && (
                        <div className="main-image-wrapper">
                            <img
                                src={`http://localhost:8080/uploads/${activeImage}`}
                                alt={artwork.title}
                                className="main-image"
                            />
                            {isVisitor && (
                                <FavoriteButton
                                    isFavorite={isFavorite}
                                    onToggle={() => toggleFavorite(artworkId)}
                                    favoriteClassName="favorite-artwork-page"
                                />
                            )}
                        </div>
                    )}
                    {images.length > 1 && (
                        <div className="thumbnail-wrapper">
                            {images.map((img, index) => (
                                <button
                                    key={img}
                                    className="thumbnail-button"
                                    onClick={() => setActiveImage(img)}
                                >
                                    <img
                                        src={`http://localhost:8080/uploads/${img}`}
                                        alt={`Afbeelding ${index + 1}`}
                                        className="thumbnail-image"
                                    />
                                </button>
                            ))}
                        </div>
                    )}
                </div>
                <div className="artwork-details">
                    <Link to={`/artist/${artwork.artistId}`} className="link">
                        <h3>{artwork.artistName}</h3>
                    </Link>
                    <div className="price-availability-wrapper">
                        <p>Prijs: €{artwork.price}</p>
                        <p className="availability-tag">{artwork.availabilityLabel}</p>
                    </div>
                    <div>
                        <div className="sizes-wrapper">
                            <p className="size-descriptor">Lengte: </p>
                            <p>{artwork.lengthInCm} cm</p>
                        </div>
                        <div className="sizes-wrapper">
                            <p className="size-descriptor">Breedte: </p>
                            <p>{artwork.widthInCm} cm</p>
                        </div>
                        <div className="sizes-wrapper">
                            <p className="size-descriptor">Hoogte: </p>
                            <p>{artwork.heightInCm} cm</p>
                        </div>
                    </div>
                    <div className="genre-list">
                        {artwork.genreNames?.map((genreName) => (
                            <span key={genreName} className="genre-tag">{genreName}</span>
                        ))}
                    </div>
                </div>
            </section>
            <Link to="/overview" className="artwork-detail-button">
                ← Terug naar overzicht
            </Link>
        </div>
    )
}

export default Artwork;