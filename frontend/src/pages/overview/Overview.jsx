import './Overview.css';
import {useEffect, useState, useRef} from "react";
import axios from "axios";
import Spinner from "../../components/spinner/Spinner.jsx";
import ArtworkCard from "../../components/artworkCard/ArtworkCard.jsx";
import defaultImage from "../../assets/art-gallery.jpg";
import {buildFilterQuery} from "../../helpers/buildFilterQuery.js";
import InputField from "../../components/inputField/InputField.jsx";
import PriceSlider from "../../components/priceSlider/PriceSlider.jsx";
import Button from "../../components/button/Button.jsx";

function Overview() {

    const [artworks, setArtworks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showFilters, setShowFilters] = useState(false);
    const [filters, setFilters] = useState({
        title: "",
        artistFirstName: "",
        artistLastName: "",
        minPrice: "",
        maxPrice: "",
        genre: "",
        availabilities: []
    });

    const dropdownRef = useRef(null);

    useEffect(() => {
        fetchFilteredArtworks();
    }, []);

    useEffect(() => {
        const timeout = setTimeout(() => {
            fetchFilteredArtworks();
        }, 400);

        return () => clearTimeout(timeout);
    }, [filters]);

    useEffect(() => {
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowFilters(false);
            }
        }

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

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

    return (
        <div className="overview-container">
            <h2 className="overview-title">Ontdek alle kunst</h2>
            <div className="filter-dropdown-wrapper" ref={dropdownRef}>
                <div className="overview-buttons-wrapper">
                    <Button className="button-default button-tertiary"
                            type="button"
                            onClick={() => setShowFilters(prev => !prev)}
                            label={`Filters ${showFilters ? "▲" : "▼"}`}
                    />
                    <Button className="button-default button-tertiary"
                            type="button"
                            onClick={() => setShowFilters(prev => !prev)}
                            label="Sorteer"
                    />
                </div>
                {showFilters && (
                    <section className="filter-wrapper">
                        <div className="filters-left">
                            <InputField placeholder="Titel"
                                        as="input"
                                        type="text"
                                        name="title"
                                        id="title"
                                        value={filters.title}
                                        onChange={e =>
                                            setFilters({...filters, title: e.target.value})}
                            />
                            <InputField placeholder="Zoek kunstenaar op voornaam"
                                        as="input"
                                        type="text"
                                        name="artistFirstName"
                                        id="artistFirstName"
                                        value={filters.artistFirstName}
                                        onChange={e =>
                                            setFilters({...filters, artistFirstName: e.target.value})}
                            />
                            <InputField placeholder="Zoek kunstenaar op achternaam"
                                        as="input"
                                        type="text"
                                        name="artistLastName"
                                        id="artistLastName"
                                        value={filters.artistLastName}
                                        onChange={e =>
                                            setFilters({...filters, artistLastName: e.target.value})}
                            />
                            <InputField placeholder="Zoek op genre"
                                        as="input"
                                        type="text"
                                        name="genre"
                                        id="genre"
                                        value={filters.genre}
                                        onChange={e =>
                                            setFilters({...filters, genre: e.target.value})}
                            />
                        </div>
                        <div className="filters-right">
                            <PriceSlider filters={filters} setFilters={setFilters} min={0} max={2000}/>
                            <InputField
                                label="Beschikbaarheid: "
                                as="select"
                                name="availability"
                                id="availability"
                                value={filters.availabilities[0] || ""}
                                onChange={e => setFilters({...filters, availabilities: [e.target.value]})}
                                options={[
                                    {
                                        value: "",
                                        label: "Selecteer...",
                                        disabled: true,
                                    }, {
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
                            />
                            <Button className="button-default button-tertiary"
                                    type="button"
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
                                    label="Reset filters"
                            />
                        </div>
                    </section>
                )}
            </div>
            <section className="overview-wrapper">
                {artworks.length === 0 ? (
                    <div className="geen-kunstwerken-wrapper">
                        <h3 className="geen-kunstwerken-gevonden">
                            Geen kunstwerken gevonden met deze filters.
                        </h3>
                        <Button className="button-default button-tertiary"
                                type="button"
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
                                label="Reset filters"
                        />
                    </div>
                ) : (
                    artworks.map((artwork) => {
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
                    })
                )}
            </section>
        </div>
    )
}

export default Overview;