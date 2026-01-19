import './Overview.css';
import {useEffect, useState} from "react";
import axios from "axios";
import Spinner from "../../components/spinner/Spinner.jsx";
import ArtworkCard from "../../components/artworkCard/ArtworkCard.jsx";
import defaultImage from "../../assets/art-gallery.jpg";
import {buildFilterQuery} from "../../helpers/buildFilterQuery.js";
import InputField from "../../components/inputField/InputField.jsx";
import PriceSlider from "../../components/priceSlider/PriceSlider.jsx";

function Overview() {

    const [artworks, setArtworks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filters, setFilters] = useState({
        title: "",
        artistFirstName: "",
        artistLastName: "",
        minPrice: "",
        maxPrice: "",
        genre: "",
        availabilities: []
    });

    useEffect(() => {
        fetchFilteredArtworks();
    }, []);

    useEffect(() => {
        const timeout = setTimeout(() => {
            fetchFilteredArtworks();
        }, 400);

        return () => clearTimeout(timeout);
    }, [filters]);

    async function fetchFilteredArtworks() {
        try {
            const query = buildFilterQuery(filters);
            const response = await axios.get(`http://localhost:8080/artworks/filter?${query}`);
            console.log(query)

            let data = response.data;
            setArtworks(Array.isArray(data) ? data : []);
        } catch (e) {
            setError("Kunstwerken filteren mislukt")
            setArtworks([]);
        } finally {
            setLoading(false);
        }
    }

    if (loading) {
        return (
            <Spinner size="default" text="Kunst wordt geladen"/>
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


            <section className="filter-wrapper">

                <InputField placeholder="titel"
                            as="input"
                            type="text"
                            name="title"
                            id="title"
                            value={filters.title}
                            onChange={e =>
                                setFilters({...filters, title: e.target.value})}
                />
                <InputField placeholder="zoek kunstenaar op voornaam"
                            as="input"
                            type="text"
                            name="artistFirstName"
                            id="artistFirstName"
                            value={filters.artistFirstName}
                            onChange={e =>
                                setFilters({...filters, artistFirstName: e.target.value})}
                />
                <InputField placeholder="zoek kunstenaar op achternaam"
                            as="input"
                            type="text"
                            name="artistLastName"
                            id="artistLastName"
                            value={filters.artistLastName}
                            onChange={e =>
                                setFilters({...filters, artistLastName: e.target.value})}
                />
                <InputField placeholder="zoek op genre"
                            as="input"
                            type="text"
                            name="genre"
                            id="genre"
                            value={filters.genre}
                            onChange={e =>
                                setFilters({...filters, genre: e.target.value})}
                />
                <PriceSlider filters={filters} setFilters={setFilters} min={0} max={2000}/>
                <InputField
                    label="Beschikbaarheid: "
                    as="select"
                    name="availability"
                    id="availability"
                    options={[
                        {
                            value: "AVAILABLE",
                            label: "Beschikbaar",
                        }, {
                            value: "SOLD",
                            label: "Verkocht",
                        }, {
                            value: "ONLOAN",
                            label: "Uitgehuurd",
                        }
                    ]}
                    onChange={e => setFilters({...filters, availabilities: [e.target.value]})}
                />

                {/* Reset filters */}
                <button
                    onClick={() =>
                        setFilters({
                            title: "",
                            artistFirstName: "",
                            artistLastName: "",
                            minPrice: "",
                            maxPrice: "",
                            genre: "",
                            availabilities: []
                        })
                    }
                >
                    Reset filters
                </button>

            </section>


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