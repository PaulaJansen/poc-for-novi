import './Overview.css';
import {useEffect, useState} from "react";
import axios from "axios";
import Spinner from "../../components/spinner/Spinner.jsx";
import ArtworkCard from "../../components/artworkCard/ArtworkCard.jsx";
import defaultImage from "../../assets/art-gallery.jpg";
import {buildFilterQuery} from "../../helpers/buildFilterQuery.js";
import InputField from "../../components/inputField/InputField.jsx";

function Overview() {

    const [artworks, setArtworks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filters, setFilters] = useState({
        title: "",
        artistName: "",
        minPrice: "",
        maxPrice: "",
        genre: "",
        availabilities: []
    });


    // useEffect(() => {
    //     async function fetchArtworks() {
    //         try {
    //             const response = await axios.get("http://localhost:8080/artworks");
    //             const data = response.data;
    //
    //             if (Array.isArray(data)) {
    //                 setArtworks(data);
    //             } else if (Array.isArray(data.data)) {
    //                 setArtworks(data.data);
    //             } else {
    //                 setArtworks([]);
    //                 throw new Error("Onverwacht API-formaat");
    //             }
    //         } catch (e) {
    //             setError("Kunstwerken ophalen mislukt")
    //         } finally {
    //             setLoading(false);
    //         }
    //     }

    //     fetchArtworks();
    // }, []);

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
            const query = buildFilterQuery({
                title: filters.title,
                minPrice: filters.minPrice,
                maxPrice: filters.maxPrice,
                genres: filters.genre ? [filters.genre] : [],
                availabilities: filters.availabilities
            });

            const response = await axios.get(`http://localhost:8080/artworks/filter?${query}`);
            let data = response.data;

            if (filters.artistName) {
                const search = filters.artistName.toLowerCase().trim();
                data = data.filter(artwork =>
                    (artwork.artistFirstName?.toLowerCase().includes(search) ||
                        artwork.artistLastName?.toLowerCase().includes(search))
                );
            }

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

                <InputField placeholder="zoek kunstenaar"
                            as="input"
                            type="text"
                            name="artist"
                            id="artist"
                            value={filters.artistName}
                            onChange={e =>
                                setFilters({...filters, artistName: e.target.value})}
                />

                <InputField placeholder="zoek op genre"
                            as="input"
                            type="text"
                            name="genre"
                            id="genre"
                            value={filters.genre}
                            onChange={e =>
                                setFilters({...filters, genre: e.target.value.toUpperCase()})}
                />

                {/*price*/}
                <div>
                    <label>
                        Min prijs:
                        <input
                            type="number"
                            value={filters.minPrice}
                            onChange={e =>
                                setFilters({...filters, minPrice: e.target.value})
                            }
                            min={0}
                        />
                    </label>

                    <label>
                        Max prijs:
                        <input
                            type="number"
                            value={filters.maxPrice}
                            onChange={e =>
                                setFilters({...filters, maxPrice: e.target.value})
                            }
                            min={0}
                        />
                    </label>
                </div>
                {/*availabilty*/}
                <div>
                    <label>
                        <input
                            type="checkbox"
                            value="FOR_SALE"
                            checked={filters.availabilities.includes("FOR_SALE")}
                            onChange={e => {
                                const updated = e.target.checked
                                    ? [...filters.availabilities, e.target.value]
                                    : filters.availabilities.filter(a => a !== e.target.value);
                                setFilters({...filters, availabilities: updated});
                            }}
                        />
                        Te koop
                    </label>

                    <label>
                        <input
                            type="checkbox"
                            value="SOLD"
                            checked={filters.availabilities.includes("SOLD")}
                            onChange={e => {
                                const updated = e.target.checked
                                    ? [...filters.availabilities, e.target.value]
                                    : filters.availabilities.filter(a => a !== e.target.value);
                                setFilters({...filters, availabilities: updated});
                            }}
                        />
                        Verkocht
                    </label>
                </div>

                {/* Reset filters */}
                <button
                    onClick={() =>
                        setFilters({
                            title: "",
                            artistName: "",
                            minPrice: "",
                            maxPrice: "",
                            genre: [],
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