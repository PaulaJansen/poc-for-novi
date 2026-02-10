import "./ArtworksSection.css";
import defaultImage from "../../assets/art-gallery.jpg";
import ArtworkCard from "../artworkCard/ArtworkCard.jsx";
import arrow from "../../assets/arrow-circle.svg";
import {useEffect, useRef, useState} from "react";

function ArtworksSection({artworks}) {

    const [showScrollArrow, setShowScrollArrow] = useState(false);
    const scrollRef = useRef(null);

    useEffect(() => {
        const wrapper = scrollRef.current;
        if (wrapper && wrapper.scrollWidth > wrapper.clientWidth) {
            setShowScrollArrow(true);
        }
    }, [artworks]);

    const handleScroll = () => {
        const wrapper = scrollRef.current;
        if (!wrapper) return;
        const isAtEnd = wrapper.scrollLeft + wrapper.clientWidth >= wrapper.scrollWidth - 1;
        setShowScrollArrow(!isAtEnd);
    };

    const scroll = (amount) => {
        const wrapper = scrollRef.current;
        if (wrapper) {
            wrapper.scrollBy({ left: amount, behavior: "smooth" });
        }
    };

    return (
        <section className="scrollbar-wrapper">
            <div className="artist-artworks" ref={scrollRef} onScroll={handleScroll}>
                {artworks.map(artwork => {
                    const imageUrl = artwork.images?.[0]
                        ? `http://localhost:8080/uploads/${artwork.images[0]}`
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
            </div>
            <div className={`scroll-arrow ${showScrollArrow ? "visible" : ""}`}
                 onClick={() => scroll(200)}
            >
                <img
                    src={arrow}
                    alt="See more"
                    className="section-arrow-icon"
                />
            </div>
        </section>
    )
}

export default ArtworksSection;