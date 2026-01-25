import './Artist.css';
import {useEffect, useState} from "react";
import axios from "axios";
import {useParams} from "react-router-dom";
import Spinner from "../../components/spinner/Spinner.jsx";
import Breadcrumbs from "../../components/breadCrumbs/BreadCrumbs.jsx";

function Artist() {

    const {id} = useParams();
    const [artist, setArtist] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function fetchArtist() {
            try {
                const artistResponse = await axios.get(`http://localhost:8080/artists/${id}`);
                const artistData = artistResponse.data;
                console.log(artistData);
                setArtist(artistData);
            } catch (e) {
                console.error(e);
                setError("Kunstenaar ophalen mislukt")
            } finally {
                setLoading(false);
            }
        }

        fetchArtist();
    }, [id]);

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
        </artist-container>
    )
}

export default Artist;