import './Home.css';
import ArtworkCard from "../../components/artworkCard/ArtworkCard.jsx";
import {useEffect, useState} from "react";
import Spinner from "../../components/spinner/Spinner.jsx";
import axios from "axios";
import HighlightSection from "../../components/highlightSection/HighlightSection.js";

function Home() {

    const [artworks, setArtworks] = useState([]);
    const [paintings, setPaintings] = useState([]);
    const [photos, setPhotos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        async function fetchData() {
            try {
                const response = await axios.get("/artworks");
                const data = response.data;
                setArtworks(data);
                setPaintings(data.filter(a => a.category === "paintings"));
                setPhotos(data.filter(a => a.category === "photography"));
            } catch (e) {
                setError("Kunstwerken ophalen mislukt :(")
            } finally {
                setLoading(false);
            }
        }
        fetchData();
    }, [])

    if (loading) {
        return (
            <>
                <Spinner className="spinner-default"/>
                <p>Kunstwerken worden geladen...</p>
            </>
        );
    }

    if (error) {
        return <p className="error-message">{error}</p>;
    }

    return (
        <>
            <div className="circle"></div>
            <section className="home-wrapper">
                <HighlightSection title="Uitgelichte schilderijen" items={paintings} />
                <HighlightSection title="Uitgelichte fotografie" items={photos} />
                <HighlightSection title="Uitgelicht" items={artworks} />
            </section>
        </>
    )
}

export default Home;