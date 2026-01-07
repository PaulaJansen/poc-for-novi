import './ArtworkCard.css';

function ArtworkCard({title, image, alt, price}) {
    return (
        <div className="card-wrapper">
            <img src={image} alt={alt} />
            <h4>{title}</h4>
            <p>{price}</p>
        </div>
    )
}

export default ArtworkCard;