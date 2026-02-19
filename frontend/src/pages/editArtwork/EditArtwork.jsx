import "../newArtwork/NewArtwork.css";
import {useNavigate, useParams} from "react-router-dom";
import {useContext, useEffect, useRef, useState} from "react";
import {useForm} from "react-hook-form";
import axios from "axios";
import Spinner from "../../components/spinner/Spinner.jsx";
import InputField from "../../components/inputField/InputField.jsx";
import removeSquare from "../../assets/x-square-fill.svg";
import placeholder from "../../assets/art-gallery.jpg";
import Button from "../../components/button/Button.jsx";
import useImageUpload from "../../customHooks/useImageUpload.jsx";
import {AuthContext} from "../../context/AuthContext.js";
import {toast} from "react-toastify";

function EditArtwork() {

    const {id} = useParams();
    const navigate = useNavigate();
    const fileInputRef = useRef(null);
    const {register, handleSubmit, reset, setValue} = useForm();
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
                const response = await axios.get(`http://localhost:8080/artworks/${id}`);
                const data = response.data;

                if (auth.user.id !== data.artistId) {
                    toast.error("Je mag dit kunstwerk niet bewerken");
                    navigate("/dashboard");
                    return;
                }

                reset({
                    title: data.title,
                    price: data.price,
                    availability: data.availability,
                    widthInCm: data.widthInCm,
                    lengthInCm: data.lengthInCm,
                    heightInCm: data.heightInCm,
                    genreNames: [...data.genreNames],
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
                price: data.price,
                availability: data.availability,
                genreNames: Array.isArray(data.genreNames)
                    ? data.genreNames
                    : data.genreNames
                        ? data.genreNames.split(",").map(g => g.trim())
                        : [],
                widthInCm: data.widthInCm,
                lengthInCm: data.lengthInCm,
                heightInCm: data.heightInCm,
                removeImages: rawImages
                    .filter(img => img.removed && img.dbPath)
                    .map(img => img.dbPath),
            };

            formData.append("artwork",
                new Blob([JSON.stringify(artworkData)], {type: "application/json"}));

            rawImages
                .filter(img => img.file && !img.removed)
                .forEach(img => formData.append("images", img.file));


            await axios.patch(`http://localhost:8080/artworks/${id}`,
                formData, {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`
                    }
                });

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
            <h2 className="new-artwork-header">Kunstwerk bewerken</h2>
            <form className="new-artwork-form"
                  onSubmit={handleSubmit(handleFormSubmit)}
                  style={{opacity: loading ? 0.6 : 1}}
            >
                <div className="new-artwork-wrapper">
                    <InputField as="input"
                                type="text"
                                labelClassName="label-quinary"
                                label="Titel: "
                                name="title"
                                id="title"
                                register={register}
                                required
                    />
                    <InputField as="input"
                                type="text"
                                labelClassName="label-quinary"
                                label="Genres (scheid genres met komma's: "
                                name="genreNames"
                                id="genreNames"
                                register={register}
                                placeholder="bijv. schilderij, abstract, modern"
                                multiple
                                required
                    />
                    <InputField as="input"
                                type="number"
                                labelClassName="label-quinary"
                                label="Prijs: "
                                name="price"
                                id="price"
                                register={register}
                                min="0"
                                step="0.01"
                                placeholder="€"
                                required
                    />
                    <InputField as="select"
                                labelClassName="label-quinary"
                                label="Beschikbaarheid: "
                                name="availability"
                                id="availability"
                                register={register}
                                required
                                options={[
                                    {value: "AVAILABLE", label: "Beschikbaar"},
                                    {value: "AVAILABLETOBUY", label: "Te koop"},
                                    {value: "AVAILABLETOLOAN", label: "Te huur"},
                                    {value: "SOLD", label: "Verkocht"},
                                    {value: "ONLOAN", label: "Verhuurd"}
                                ]}
                    />
                </div>
                <div className="new-artwork-dimensions">
                    <InputField as="input"
                                type="number"
                                labelClassName="label-quinary"
                                label="Breedte (cm): "
                                name="widthInCm"
                                id="widthInCm"
                                register={register}
                                required
                    />
                    <InputField as="input"
                                type="number"
                                labelClassName="label-quinary"
                                label="Lengte (cm): "
                                name="lengthInCm"
                                id="lengthInCm"
                                register={register}
                                required
                    />
                    <InputField as="input"
                                type="number"
                                labelClassName="label-quinary"
                                label="Hoogte (cm): "
                                name="heightInCm"
                                id="heightInCm"
                                register={register}
                                required
                    />
                </div>
                <div className="image-dropzone"
                     onClick={() => fileInputRef.current.click()}
                     onDrop={handleDrop}
                     onDragOver={handleDragOver}
                >
                    Sleep afbeeldingen hierheen of klik om te kiezen
                </div>
                <InputField as="input"
                            type="file"
                            className="file-input-hidden"
                            name="images"
                            id="images"
                            register={register}
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