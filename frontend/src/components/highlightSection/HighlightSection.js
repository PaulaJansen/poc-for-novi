import './HighlightSection.css';
import ArtworkCard from "../artworkCard/ArtworkCard.jsx";

function HighlightSection({title, items}) {
    return (
        <>
            <h3>{title}</h3>
            <article className="highlight-wrapper">
                {items.slice(0,5).map(item => (
                    <ArtworkCard
                        key={item.id}
                        image={item.image}
                        alt={item.title}
                        title={item.title}
                        price={item.price}
                    />
                ))}
            </article>
        </>
    );
}

export default HighlightSection;