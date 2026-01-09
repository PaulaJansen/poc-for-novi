import "./HighlightSection.css";
import ArtworkCard from "../artworkCard/ArtworkCard.jsx";
import defaultImage from "../../assets/art-gallery.jpg";
import Spinner from "../spinner/Spinner.jsx";
import arrow from "../../assets/arrow-circle.svg";
import {Link} from "react-router-dom";

function HighlightSection({title, items, loading}) {

    const displayItems =
        items && items.length > 0
            ? items.slice(0, 5)
            : Array.from({length: 5}).map((_, idx) => ({
                id: `placeholder-${idx}`,
                image: defaultImage,
                alt: "Placeholder artwork",
                title: "---",
                price: "€-,-",
            }));

    return (
        <div className="highlight-section-wrapper">
            <h3>{title}</h3>
            {loading && (
                <div className="spinner-overlay">
                    <Spinner className="spinner-default"/>
                    <p>Laden...</p>
                </div>
            )}

            <article className="highlight-wrapper">
                {displayItems.map((item) => (
                    <ArtworkCard
                        key={item.id}
                        id={item.id}
                        image={item.image}
                        alt={item.title || item.title}
                        title={item.title}
                        price={item.price}
                    />
                ))}
                <Link to="/overview">
                    <img
                        src={arrow}
                        alt="See more"
                        className="arrow-icon"
                    />
                </Link>
            </article>
        </div>
    );
}

export default HighlightSection;