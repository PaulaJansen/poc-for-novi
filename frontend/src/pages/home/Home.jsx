import "./Home.css";
import {useEffect, useState} from "react";
import axios from "axios";
import HighlightSection from "../../components/highlightSection/HighlightSection.jsx";

function Home() {

    const [artworks, setArtworks] = useState([]);
    const [paintings, setPaintings] = useState([]);
    const [photos, setPhotos] = useState([]);
    const [loadingArtworks, setLoadingArtworks] = useState(true);
    const [loadingPaintings, setLoadingPaintings] = useState(true);
    const [loadingPhotos, setLoadingPhotos] = useState(true);
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
                setError("Kunstwerken ophalen mislukt")
                setArtworks([]);
                setPaintings([]);
                setPhotos([]);
            } finally {
                setLoadingArtworks(false);
                setLoadingPaintings(false);
                setLoadingPhotos(false);
            }
        }

        fetchData();
    }, [])

    return (

        <div className="home-container">
            <div className="circle-large"></div>
            <section className="home-wrapper">
                {error && <p className="error-message">{error}</p>}
                <HighlightSection title="Uitgelichte schilderijen" items={paintings} loading={loadingPaintings}/>
                <HighlightSection title="Uitgelichte fotografie" items={photos} loading={loadingPhotos}/>
                <HighlightSection title="Uitgelicht" items={artworks} loading={loadingArtworks}/>
            </section>
        </div>
    )
}

export default Home;