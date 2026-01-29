import './UserArtist.css';
import {useEffect, useState} from "react";
import axios from "axios";
import {useParams} from "react-router-dom";
import Spinner from "../../components/spinner/Spinner.jsx";
import defaultImage from "../../assets/art-gallery.jpg";
import profilePicture from "../../assets/user-switch.svg"
import ArtworkCard from "../../components/artworkCard/ArtworkCard.jsx";
import {useChangeProfilePicture} from "../../helpers/useChangeProfilePicture.jsx"

function UserArtist() {

    const {id} = useParams();
    const [artist, setArtist] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [artworksLoading, setArtworksLoading] = useState(true);
    const [artworks, setArtworks] = useState([]);

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

    const {
        fileInputRef,
        preview,
        file,
        loading: uploadLoading,
        openFilePicker,
        onFileChange,
        upload,
    } = useChangeProfilePicture(
        id,
        artist
            ? `http://localhost:8080/images/${artist.profilePicture}`
            : null
    );

    if (loading) {
        return (
            <Spinner size="default" text="Profiel wordt geladen"/>
        );
    }

    if (error) {
        return (
            <div className="user-container">
                <p className="error-message">{error}</p>
            </div>
        );
    }

    return (
        <div className="user-container">
            <section className="user-wrapper">
                <div className="profile-picture">
                    <img className="user-image"
                         src={preview || `http://localhost:8080/images/${artist.profilePicture}`}
                         alt={artist.username}/>
                    <div className="image-change-wrapper" onClick={openFilePicker}>
                        <img src={profilePicture} alt="change-picture" />
                    </div>
                    <input
                        type="file"
                        accept="image/*"
                        ref={fileInputRef}
                        onChange={onFileChange}
                        hidden
                    />

                    {file && (
                        <button onClick={upload} disabled={uploadLoading}>
                            {uploadLoading ? "Uploaden..." : "Opslaan"}
                        </button>
                    )}
                </div>
                <article className="user-details">
                    <h2>{artist.firstName} {artist.lastName}</h2>
                    <h3>{artist.username}</h3>
                    <p className="art-tag">{artist.typeOfArt}</p>
                    <p>{artist.city}</p>
                    <p>Hier sinds {artist.dateOfRegistration}</p>
                    <p className="user-biography">{artist.biography}</p>
                </article>
            </section>
            <section className="user-artworks-wrapper">
                <h2 className="user-artworks-header">Kunstwerken</h2>

                {artworksLoading && (
                    <Spinner size="small" text="Kunstwerken laden..."/>
                )}

                {!artworksLoading && artworks.length === 0 && (
                    <p className="artist-no-artworks">Deze kunstenaar heeft nog geen kunstwerken.</p>
                )}


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
            </section>
            <section className="user-artworks-wrapper">
                <h2 className="user-artworks-header">Favorieten</h2>

                {artworksLoading && (
                    <Spinner size="small" text="Favorieten laden..."/>
                )}

                {!artworksLoading && artworks.length === 0 && (
                    <p className="artist-no-artworks">Je hebt nog geen favorieten</p>
                )}

                {/*AANPASSEN NAAR FAVORIETEN CONTEXT*/}
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
            </section>
        </div>
    )
}

export default UserArtist;