import './Artwork.css';
import axios from "axios";
import {useEffect, useState} from "react";
import {Link, useParams} from "react-router-dom";
import Spinner from "../../components/spinner/Spinner.jsx";
import Breadcrumbs from "../../components/breadCrumbs/BreadCrumbs.jsx";

function Artwork() {

    const {id} = useParams();
    const [artwork, setArtwork] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [activeImage, setActiveImage] = useState(null);

    useEffect(() => {
        async function fetchArtwork() {
            try {
                const artworkResponse = await axios.get(`http://localhost:8080/artworks/${id}`);
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
            <h2 className="artwork-details-title">{artwork.title}</h2>
            <section className="artwork-details-wrapper">
                <div className="artwork-gallery">
                    {activeImage && (
                        <div className="main-image-wrapper">
                            <img
                                src={`http://localhost:8080/images/${activeImage}`}
                                alt={artwork.title}
                                className="main-image"
                            />
                        </div>
                    )}
                    {images.length > 1 && (
                        <div className="thumbnail-wrapper">
                            {images.map((img, index) => (
                                <button
                                    key={index}
                                    className="thumbnail-button"
                                    onClick={() => setActiveImage(img)}
                                >
                                    <img
                                        src={`http://localhost:8080/images/${img}`}
                                        alt={`Afbeelding ${index + 1}`}
                                        className="thumbnail-image"
                                    />
                                </button>
                            ))}
                        </div>
                    )}
                </div>
                <div className="artwork-details">
                    <h3>{artwork.artistName}</h3>
                    <div className="price-availability-wrapper">
                        <p>Prijs: €{artwork.price}</p>
                        <p className="availability-tag">{artwork.availability}</p>
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