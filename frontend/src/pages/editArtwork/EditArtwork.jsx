import "../newArtwork/NewArtwork.css";
import "./EditArtwork.css";
import {useNavigate, useParams} from "react-router-dom";
import {useContext, useEffect, useRef, useState} from "react";
import {useForm} from "react-hook-form";
import Spinner from "../../components/spinner/Spinner.jsx";
import removeSquare from "../../assets/x-square-fill.svg";
import placeholder from "../../assets/art-gallery.jpg";
import Button from "../../components/button/Button.jsx";
import useImageUpload from "../../customHooks/useImageUpload.jsx";
import {AuthContext} from "../../context/AuthContext.js";
import {toast} from "react-toastify";
import API from "../../helpers/api.js";

function EditArtwork() {

    const {id} = useParams();
    const navigate = useNavigate();
    const fileInputRef = useRef(null);
    const {register, handleSubmit, reset} = useForm();
    const {auth} = useContext(AuthContext);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const imageUpload = useImageUpload({maxImages: 8});

    const {
        images,
        rawImages,
        removeImage,
        handleFileInput,
        handleDrop,
        handleDragOver,
        handleDragStart,
        handleDragEnter,
        handleDragEnd,
        setInitialImages
    } = imageUpload;

    useEffect(() => {
        async function fetchArtwork() {

            if (!id) {
                setLoading(false);
                return;
            }

            try {
                const response = await API.get(`/artworks/${id}`);
                const data = response.data;

                if (auth.user.id !== data.artistId) {
                    toast.error("Je mag dit kunstwerk niet bewerken");
                    navigate("/dashboard");
                    return;
                }

                reset({
                    title: data.title,
                    price: parseFloat(data.price),
                    availability: data.availability,
                    widthInCm: data.widthInCm,
                    lengthInCm: data.lengthInCm,
                    heightInCm: data.heightInCm,
                    genreNames: data.genreNames?.join(", ") || "",
                });

                if (data.images?.length) {
                    setInitialImages(data.images);
                }

            } catch (e) {
                console.error(e);
                setError("Kunstwerk ophalen mislukt");
            } finally {
                setLoading(false);
            }
        }

        fetchArtwork();
    }, [auth.user.id, id, navigate, reset, setInitialImages]);

    async function handleFormSubmit(data) {

        setLoading(true);
        try {
            const formData = new FormData();
            const artworkData = {
                title: data.title,
                price: parseFloat(data.price),
                availability: data.availability,
                genreNames: Array.isArray(data.genreNames)
                    ? data.genreNames
                    : data.genreNames
                        ? data.genreNames.split(",").map(g => g.trim())
                        : [],
                widthInCm: parseFloat(data.widthInCm),
                lengthInCm: parseFloat(data.lengthInCm),
                heightInCm: parseFloat(data.heightInCm),
                removeImages: rawImages
                    .filter(img => img.removed && img.dbPath)
                    .map(img => img.dbPath),
            };

            formData.append("artwork", JSON.stringify(artworkData));

            rawImages
                .filter(img => img.file && !img.removed)
                .forEach(img => formData.append("images", img.file));

            await API.patch(`/artworks/${id}`, formData);

            navigate(`/artwork/${id}`, {
                state: {edited: true}
            });
        } catch (e) {
            console.error(e);
            setError("Opslaan niet gelukt");
            toast.error("Opslaan mislukt, probeer opnieuw!");
        } finally {
            setLoading(false);
        }
    }

    async function handleDeleteArtwork(id) {
        if (!window.confirm("Weet je zeker dat je dit kunstwerk wilt verwijderen?")) return;

        try {
            setLoading(true);
            await API.delete(`/artworks/${id}`);
            toast.success("Kunstwerk verwijderd!");
            navigate("/dashboard");
        } catch (e) {
            console.error(e);
            toast.error("Verwijderen mislukt, probeer opnieuw!");
        } finally {
            setLoading(false);
        }
    }

    if (loading) {
        return (
            <Spinner size="default" text="Kunstwerk wordt geladen"/>
        );
    }

    if (error) {
        return (
            <div className="artwork-container">
                <p className="error-message">{error}</p>
            </div>
        );
    }

    return (
        <div className="new-artwork-container">
            <Button
                className="button-default button-delete"
                type="button"
                label="Kunstwerk verwijderen"
                onClick={() => handleDeleteArtwork(id)}
            />
            <h2 className="new-artwork-header">Kunstwerk bewerken</h2>
            <form className="edit-artwork-form"
                  onSubmit={handleSubmit(handleFormSubmit)}
                  style={{opacity: loading ? 0.6 : 1}}
            >
                <div className="edit-artwork-wrapper">
                    <label className="label-quinary">Titel:
                        <input className="input-field" {...register("title", {required: true})} />
                    </label>
                    <label className="label-primary label-quinary">Genres (scheid genres met komma's):
                        <input className="input-field" {...register("genreNames")} />
                    </label>

                    <label className="label-quinary">Prijs:
                        <input className="input-field" type="number" step="0.01" min="0"
                               placeholder="€" {...register("price", {required: true})} />
                    </label>

                    <label className="label-quinary">Beschikbaarheid:
                        <select className="input-field" {...register("availability", {required: true})}>
                            <option value="AVAILABLE">Beschikbaar</option>
                            <option value="AVAILABLETOBUY">Te koop</option>
                            <option value="AVAILABLETOLOAN">Te huur</option>
                            <option value="SOLD">Verkocht</option>
                            <option value="ONLOAN">Verhuurd</option>
                        </select>
                    </label>
                </div>
                <div className="edit-artwork-dimensions">
                    <label className="label-quinary">Breedte (cm):
                        <input className="input-field" type="number" {...register("widthInCm")} />
                    </label>
                    <label className="label-quinary">Lengte (cm):
                        <input className="input-field" type="number" {...register("lengthInCm")} />
                    </label>
                    <label className="label-quinary">Hoogte (cm):
                        <input className="input-field" type="number" {...register("heightInCm")} />
                    </label>
                </div>
                <div className="edit-artwork-images">
                    <div className="image-dropzone"
                         onClick={() => fileInputRef.current.click()}
                         onDrop={handleDrop}
                         onDragOver={handleDragOver}
                    >
                        Sleep afbeeldingen hierheen of klik om te kiezen
                    </div>
                    <input
                        type="file"
                        className="file-input-hidden"
                        id="images"
                        multiple
                        accept="image/*"
                        ref={fileInputRef}
                        onChange={(e) => handleFileInput(e.target.files)}
                    />
                    <div className="image-preview-grid">
                        {images.length === 0 && <p>⚠️ Geen afbeeldingen toegevoegd</p>}
                        {images.map((img) => (
                            <div key={img.id}
                                 className="image-preview-item"
                                 draggable
                                 onDragStart={() => handleDragStart(img.id)}
                                 onDragEnter={() => handleDragEnter(img.id)}
                                 onDragEnd={handleDragEnd}
                            >
                                <img className="image-preview"
                                     src={
                                         img.file
                                             ? URL.createObjectURL(img.file)
                                             : img.url

                                     }
                                     alt="preview"
                                     onError={(e) => {
                                         e.target.src = placeholder
                                     }}
                                />
                                <div className="remove-image">
                                    <img src={removeSquare}
                                         alt="verwijder afbeelding"
                                         onClick={() => {
                                             removeImage(img.id);
                                         }}
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
                <div className="button-form">
                    <Button className="button-default button-tertiary-reverse"
                            type="submit"
                            disabled={loading}
                            label={loading ? <Spinner/> : "Kunstwerk opslaan"}
                    />
                </div>
            </form>
            {
                error && <p className="error-message">{error}</p>
            }
        </div>
    )
}

export default EditArtwork;