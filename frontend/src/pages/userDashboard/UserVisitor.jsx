import './UserDashboard.css';
import {useEffect, useState} from "react";
import axios from "axios";
import Spinner from "../../components/spinner/Spinner.jsx";
import {useChangeProfilePicture} from "../../customHooks/useChangeProfilePicture.jsx";
import profilePicture from "../../assets/user-switch.svg";
import closeSquare from "../../assets/x-square.svg";
import {useForm} from "react-hook-form";
import Button from "../../components/button/Button.jsx";
import InputField from "../../components/inputField/InputField.jsx";

function UserVisitor({id}) {

    const {register, handleSubmit, reset} = useForm();

    const [visitor, setVisitor] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [changeProfile, setChangeProfile] = useState(false);

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

    useEffect(() => {
        if (visitor) {
            reset({
                name: visitor.name,
                email: visitor.email,
            });
        }
    }, [visitor, reset]);

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
            ? `http://localhost:8080/uploads/${visitor.profilePicture}`
            : null
    );

    async function handleFormSubmit(data) {
        try {
            await axios.patch(`http://localhost:8080/visitors/${id}`, data);
            console.log("Gegevens zijn opgeslagen!");
            setChangeProfile(false);
        } catch (e) {
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

                <aside className="user-sidebar">
                    <Button className="button-default button-tertiary"
                            type="button"
                            label="Profiel aanpassen"
                            onClick={() => setChangeProfile(true)}
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
                                            label="Naam: "
                                            name="name"
                                            id="name"
                                            register={register}
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