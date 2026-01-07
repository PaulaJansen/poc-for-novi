import './Home.css';
import ArtworkCard from "../../components/artworkCard/ArtworkCard.jsx";

function Home() {
    return (
        <>
            <div className="circle"></div>
            <section className="home-wrapper">
                <h3>Uitgelichte schilderijen</h3>
                <article className="highlight-wrapper">
                    <ArtworkCard/>
                </article>
                <h3>Uitgelichte fotografie</h3>
                <article className="highlight-wrapper">
                    <ArtworkCard/>
                </article>
                <h3>Uitgelicht</h3>
                <article className="highlight-wrapper">
                    <ArtworkCard/>
                </article>
            </section>
        </>
    )
}

export default Home;