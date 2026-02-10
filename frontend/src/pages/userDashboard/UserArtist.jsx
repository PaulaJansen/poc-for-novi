import './UserDashboard.css';
import {useContext, useEffect, useState} from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import Spinner from "../../components/spinner/Spinner.jsx";
import defaultImage from "../../assets/art-gallery.jpg";
import profilePicture from "../../assets/user-switch.svg";
import closeSquare from "../../assets/x-square.svg";
import ArtworkCard from "../../components/artworkCard/ArtworkCard.jsx";
import {useChangeProfilePicture} from "../../customHooks/useChangeProfilePicture.jsx";
import Button from "../../components/button/Button.jsx";
import InputField from "../../components/inputField/InputField.jsx";
import {useForm} from "react-hook-form";

function UserArtist({id}) {

    const {register, handleSubmit, reset} = useForm({
        shouldUnregister: false,
    });

    const navigate = useNavigate();

    const [artist, setArtist] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [artworksLoading, setArtworksLoading] = useState(true);
    const [artworks, setArtworks] = useState([]);
    const [changeProfile, setChangeProfile] = useState(false);

    useEffect(() => {
        async function fetchArtistAndArtworks() {
            try {
                const [artistResponse, artworkResponse] = await Promise.all([
                    axios.get(`http://localhost:8080/artists/${id}`),
                    axios.get(`http://localhost:8080/artists/${id}/artworks`),
                ]);
                const artistData = artistResponse.data;
                const artworkData = artworkResponse.data;
                console.log(artistData);
                console.log(artworkData);
                setArtist(artistData);
                setArtworks(artworkData);
            } catch (e) {
                console.error(e);
                setError("Gegevens ophalen mislukt")
            } finally {
                setLoading(false);
                setArtworksLoading(false);
            }
        }

        fetchArtistAndArtworks();
    }, [id]);

    useEffect(() => {
        if (artist) {
            reset({
                firstName: artist.firstName,
                lastName: artist.lastName,
                city: artist.city,
                biography: artist.biography,
                email: artist.email,
            });
        }
    }, [artist, reset]);

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
        artist
            ? `http://localhost:8080/uploads/${artist.profilePicture}`
            : null
    );

    async function handleFormSubmit(data) {
        try {
            await axios.patch(`http://localhost:8080/artists/${id}`, data,
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`
                    }
                }
            );
            console.log("Gegevens zijn opgeslagen!");
            setChangeProfile(false);

            const artistResponse = await axios.get(
                `http://localhost:8080/artists/${id}`,
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`
                    }
                }
            );

            setArtist(artistResponse.data);
        } catch
            (e) {
            console.error(e);
            setError("Gegevens aanpassen niet gelukt");
        }
    }

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
                         src={preview || `http://localhost:8080/uploads/${artist.profilePicture}`}
                         alt={artist.username}/>
                    <div className="image-change-wrapper" onClick={openFilePicker}>
                        <img src={profilePicture} alt="change-picture"/>
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
                    <h2>{artist.firstName} {artist.lastName}</h2>
                    <h3>{artist.username}</h3>
                    <p className="art-tag">{artist.typeOfArt}</p>
                    <p>{artist.city}</p>
                    <p>Hier sinds {artist.dateOfRegistration}</p>
                    <p className="user-biography">{artist.biography}</p>
                </article>
                <aside className="user-sidebar">
                    <Button className="button-default button-tertiary"
                            type="button"
                            label="Profiel aanpassen"
                            onClick={() => setChangeProfile(true)}
                    />
                    <Button className="button-default button-tertiary"
                            type="button"
                            label="Kunstwerk toevoegen"
                            onClick={() => navigate("/new-artwork")}
                    />
                </aside>

                {changeProfile && (
                    <div className="profile-form-overlay" onClick={() => setChangeProfile(false)}>
                        <div className="profile-form" onClick={(e) => e.stopPropagation()}>
                            <div className="close-button">
                                <img src={closeSquare} alt="close form" onClick={() => setChangeProfile(false)}/>
                            </div>
                            <h2 className="form-header">Gegevens aanpassen</h2>
                            <form onSubmit={handleSubmit(handleFormSubmit)}>
                                <InputField as="input"
                                            type="text"
                                            label="Voornaam: "
                                            name="firstName"
                                            id="firstName"
                                            register={register}
                                />
                                <InputField as="input"
                                            type="text"
                                            label="Achternaam: "
                                            name="lastName"
                                            id="lastName"
                                            register={register}
                                />
                                <InputField as="input"
                                            type="text"
                                            label="Plaats: "
                                            name="city"
                                            id="city"
                                            register={register}
                                />
                                <InputField as="textarea"
                                            label="Biografie: "
                                            name="biography"
                                            id="biography"
                                            register={register}
                                            className="textarea"
                                />
                                <InputField as="input"
                                            type="text"
                                            label="E-mailadres: "
                                            name="email"
                                            id="email"
                                            register={register}
                                />
                                <Button className="button-default button-tertiary-reverse button-form"
                                        type="submit"
                                        label="Opslaan"
                                />
                            </form>
                        </div>
                    </div>
                )}

            </section>
            <section className="user-artworks-wrapper">
                <h2 className="user-artworks-header">Kunstwerken</h2>

                {artworksLoading && (
                    <Spinner size="small" text="Kunstwerken laden..."/>
                )}

                {!artworksLoading && artworks.length === 0 && (
                    <p className="artist-no-artworks">Deze kunstenaar heeft nog geen kunstwerken.</p>
                )}


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
                            onEdit={(id) => navigate(`/edit-artwork/${id}`)}
                        />
                    );
                })}
            </section>
            <section className="user-artworks-wrapper">
                <h2 className="user-artworks-header">Favorieten</h2>

                {artworksLoading && (
                    <Spinner size="small" text="Favorieten laden..."/>
                )}

                {!artworksLoading && artworks.length === 0 && (
                    <p className="artist-no-artworks">Je hebt nog geen favorieten</p>
                )}

                {/*AANPASSEN NAAR FAVORIETEN CONTEXT*/}
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
            </section>
        </div>
    )
}

export default UserArtist;