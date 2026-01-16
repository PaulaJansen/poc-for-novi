import './Overview.css';
import {useEffect, useState} from "react";
import axios from "axios";
import Spinner from "../../components/spinner/Spinner.jsx";
import ArtworkCard from "../../components/artworkCard/ArtworkCard.jsx";
import defaultImage from "../../assets/art-gallery.jpg";

function Overview() {

    const [artworks, setArtworks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        async function fetchArtworks() {
            try {
                const response = await axios.get("http://localhost:8080/artworks");
                const data = response.data;

                if (Array.isArray(data)) {
                    setArtworks(data);
                } else if (Array.isArray(data.data)) {
                    setArtworks(data.data);
                } else {
                    setArtworks([]);
                    throw new Error("Onverwacht API-formaat");
                }
            } catch (e) {
                setError("Kunstwerken ophalen mislukt")
            } finally {
                setLoading(false);
            }
        }

        fetchArtworks();
    }, []);

    if (loading) {
        return (
            <>
                <Spinner className="spinner-default"/>
                <p>Kunst wordt geladen...</p>
            </>
        );
    }

    if (error) {
        return (
            <div className="overview-container">
                <p className="error-message">{error}</p>
            </div>
        );
    }

    if (artworks.length === 0) {
        return <p>Er zijn nog geen kunstwerken beschikbaar!</p>
    }

    return (
        <div className="overview-container">
            <h3 className="overview-title">Ontdek alle kunst</h3>
            <section className="overview-wrapper">
                {artworks.map((artwork) => {
                    const imageUrl = artwork.images?.[0]
                            ? `http://localhost:8080/images/${artwork.images[0]}`
                            : defaultImage;

                    return (
                        <ArtworkCard
                            key={artwork.id}
                            id={artwork.id}
                            image={imageUrl}
                            alt={artwork.title}
                            title={artwork.title}
                            price={artwork.price}
                        />
                    );
                })}
            </section>
        </div>
    )
}

export default Overview;