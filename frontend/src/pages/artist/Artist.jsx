import './Artist.css';
import {useEffect, useRef, useState} from "react";
import axios from "axios";
import {useParams} from "react-router-dom";
import Spinner from "../../components/spinner/Spinner.jsx";
import Breadcrumbs from "../../components/breadCrumbs/BreadCrumbs.jsx";
import ArtworkCard from "../../components/artworkCard/ArtworkCard.jsx";
import defaultImage from "../../assets/art-gallery.jpg";
import arrow from "../../assets/arrow-circle.svg";

function Artist() {

    const {id} = useParams();
    const [artist, setArtist] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [artworksLoading, setArtworksLoading] = useState(true);
    const [artworks, setArtworks] = useState([]);
    const [showScrollArrow, setShowScrollArrow] = useState(false);
    const scrollRef = useRef(null);

    useEffect(() => {
        async function fetchArtistAndArtworks() {
            try {
                const [artistResponse, artworkResponse] = await Promise.all([
                    axios.get(`http://localhost:8080/artists/${id}`),
                    axios.get(`http://localhost:8080/artists/${id}/artworks`),
                ]);
                const artistData = artistResponse.data;
                const artworkData = artworkResponse.data;
                console.log(artistData);
                console.log(artworkData);
                setArtist(artistData);
                setArtworks(artworkData);
            } catch (e) {
                console.error(e);
                setError("Gegevens ophalen mislukt")
            } finally {
                setLoading(false);
                setArtworksLoading(false);
            }
        }

        fetchArtistAndArtworks();
    }, [id]);

    useEffect(() => {
        const wrapper = scrollRef.current;
        if (wrapper && wrapper.scrollWidth > wrapper.clientWidth) {
            setShowScrollArrow(true);
        }
    }, [artworks]);

    const handleScroll = () => {
        const wrapper = scrollRef.current;
        if (!wrapper) return;
        const isAtEnd = wrapper.scrollLeft + wrapper.clientWidth >= wrapper.scrollWidth - 1;
        setShowScrollArrow(!isAtEnd);
    };

    if (loading) {
        return (
            <Spinner size="default" text="Kunstenaar wordt geladen"/>
        );
    }

    if (error) {
        return (
            <div className="artist-container">
                <p className="error-message">{error}</p>
            </div>
        );
    }

    return (
        <artist-container>
            <Breadcrumbs lastLabel={`${artist.firstName} ${artist.lastName}`}/>
            <section className="artist-wrapper">
                <img className="artist-image" src={`http://localhost:8080/images/${artist.profilePicture}`}
                     alt={artist.username}/>
                <article className="artist-details">
                    <div className="artist-header">
                        <h2>{artist.firstName} {artist.lastName}</h2>
                        <h3>({artist.username})</h3>
                    </div>
                    <p className="art-tag">{artist.typeOfArt}</p>
                    <p>{artist.city}</p>
                    <p>Hier sinds {artist.dateOfRegistration}</p>
                </article>
            </section>
            <p className="artist-biography">{artist.biography}</p>
            <section className="artist-artworks-wrapper">
                <h2 className="artist-artworks-header">Werken van deze kunstenaar</h2>

                {artworksLoading && (
                    <Spinner size="small" text="Kunstwerken laden..."/>
                )}

                {!artworksLoading && artworks.length === 0 && (
                    <p className="artist-no-artworks">Deze kunstenaar heeft nog geen kunstwerken.</p>
                )}

                <div className="artist-scrollbar-wrapper">
                    <div className="artist-artworks" ref={scrollRef} onScroll={handleScroll}>
                        {artworks.map(artwork => {
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
                    </div>
                    <div className={`scroll-arrow ${showScrollArrow ? "visible" : ""}`}
                         onClick={() => scroll(200)}
                    >
                        <img
                            src={arrow}
                            alt="See more"
                            className="artist-arrow-icon"
                        />
                    </div>
                </div>
            </section>
        </artist-container>
    )
}

export default Artist;