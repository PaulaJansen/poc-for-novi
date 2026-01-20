import "./HighlightSection.css";
import ArtworkCard from "../artworkCard/ArtworkCard.jsx";
import defaultImage from "../../assets/art-gallery.jpg";
import Spinner from "../spinner/Spinner.jsx";
import arrow from "../../assets/arrow-circle.svg";
import {Link} from "react-router-dom";

function HighlightSection({title, items, loading}) {

    const displayItems =
        items && items.length > 0
            ? items.slice(0, 5).map(item => ({
                ...item,
                key: `real-${item.id}`,
                isPlaceholder: false
            }))
            : Array.from({length: 5}).map((_, idx) => ({
                key: `placeholder-${idx}`,
                id: `placeholder-${idx}`,
                image: defaultImage,
                alt: "Placeholder artwork",
                title: "---",
                price: "",
                isPlaceholder: true
            }));

    return (
        <div className="highlight-section-wrapper">
            <h2>{title}</h2>
            {loading && (
                <div className="spinner-overlay">
                    <Spinner size="default" text="Laden..."/>
                </div>
            )}

            <article className="highlight-wrapper">
                {displayItems.map((item) => {
                    const imageUrl = item.isPlaceholder
                        ? defaultImage
                        : item.images?.[0]
                            ? `http://localhost:8080/images/${item.images[0]}`
                            : defaultImage;

                    return (
                    <ArtworkCard
                        key={item.key ?? item.id}
                        id={item.id}
                        image={imageUrl}
                        alt={item.title}
                        title={item.title}
                        price={item.price}
                        isPlaceholder={item.isPlaceholder}
                    />
                );
            })}
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