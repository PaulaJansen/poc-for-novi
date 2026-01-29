import './UserVisitor.css';
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import Spinner from "../../components/spinner/Spinner.jsx";
import {useChangeProfilePicture} from "../../helpers/useChangeProfilePicture.jsx";
import profilePicture from "../../assets/user-switch.svg"

function UserVisitor() {

    const {id} = useParams();
    const [visitor, setVisitor] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        async function fetchVisitor() {
            try {
                const response = await axios.get(`http://localhost:8080/visitors/${id}`);
                console.log(response.data);
                setVisitor(response.data);
            } catch (e) {
                console.error(e);
                setError("Gegevens ophalen mislukt");
            } finally {
                setLoading(false);
            }
        }

        fetchVisitor();
    }, [id])

    const {
        fileInputRef,
        preview,
        file,
        loading: uploadLoading,
        openFilePicker,
        onFileChange,
        upload,
    } = useChangeProfilePicture(
        id,
        visitor
            ? `http://localhost:8080/images/${visitor.profilePicture}`
            : null
    );

    if (loading) {
        return (
            <Spinner size="default" text="Profiel wordt geladen"/>
        );
    }

    if (error) {
        return (
            <div className="user-container">
                <p className="error-message">{error}</p>
            </div>
        );
    }

    return (
        <div className="user-container">
            <section className="user-wrapper">
                <div className="profile-picture">
                    <img className="user-image"
                         src={preview || `http://localhost:8080/images/${visitor.profilePicture}`}
                         alt={visitor.username}/>
                    <div className="image-change-wrapper" onClick={openFilePicker}>
                        <img src={profilePicture} alt="change-picture" />
                    </div>
                    <input
                        type="file"
                        accept="image/*"
                        ref={fileInputRef}
                        onChange={onFileChange}
                        hidden
                    />

                    {file && (
                        <button onClick={upload} disabled={uploadLoading}>
                            {uploadLoading ? "Uploaden..." : "Opslaan"}
                        </button>
                    )}
                </div>
                <article className="user-details">
                    <h2>{visitor.name}</h2>
                    <h3>{visitor.username}</h3>
                    <p>Hier sinds {visitor.dateOfRegistration}</p>
                </article>
            </section>
            {/*<section className="user-artworks-wrapper">*/}
            {/*    <h2 className="user-artworks-header">Favorieten</h2>*/}

            {/*AANPASSEN NAAR FAVORIETEN CONTEXT*/}
            {/*    {artworks.map(artwork => {*/}
            {/*        const imageUrl = artwork.images?.[0]*/}
            {/*            ? `http://localhost:8080/images/${artwork.images[0]}`*/}
            {/*            : defaultImage;*/}

            {/*        return (*/}
            {/*            <ArtworkCard*/}
            {/*                key={artwork.id}*/}
            {/*                id={artwork.id}*/}
            {/*                image={imageUrl}*/}
            {/*                alt={artwork.title}*/}
            {/*                title={artwork.title}*/}
            {/*                price={artwork.price}*/}
            {/*            />*/}
            {/*        );*/}
            {/*    })}*/}
            {/*</section>*/}
        </div>
    )
}

export default UserVisitor;