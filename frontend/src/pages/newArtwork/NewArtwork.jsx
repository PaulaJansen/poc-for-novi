import './NewArtwork.css';
import axios from "axios";
import {useForm} from "react-hook-form";
import {useContext, useRef, useState} from "react";
import InputField from "../../components/inputField/InputField.jsx";
import Button from "../../components/button/Button.jsx";
import removeSquare from "../../assets/x-square-fill.svg"
import useImageUpload from "../../customHooks/useImageUpload.jsx";
import Spinner from "../../components/spinner/Spinner.jsx";
import {toast} from "react-toastify";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "../../context/AuthContext.js";

function NewArtwork() {

    const {register, handleSubmit, setValue, clearErrors, formState: {errors}} = useForm();
    const fileInputRef = useRef(null);
    const navigate = useNavigate();

    // const { auth } = useContext(AuthContext);
    // const artistId = auth.user.id;

    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const {
        images,
        handleFileInput,
        handleDrop,
        handleDragOver,
        removeImage,
        handleDragStart,
        handleDragEnter,
        handleDragEnd
    } = useImageUpload({setValue, maxImages: 8});

    async function handleFormSubmit(data) {
        setLoading(true);
        try {
            const formData = new FormData();
            formData.append("title", data.title);
            formData.append("price", data.price);
            formData.append("availability", data.availability);

            const genres = data.genreNames
                ?.split(",")
                .map(g => g.trim());

            genres?.forEach(g =>
                formData.append("genreNames", g)
            );

            images.forEach(img => {
                if (img.file) {
                    formData.append("images", img.file);
                }
            });

            formData.append("widthInCm", data.widthInCm || 0);
            formData.append("lengthInCm", data.lengthInCm || 0);
            formData.append("heightInCm", data.heightInCm || 0);

            const response = await axios.post(`http://localhost:8080/artworks`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    }
                }
            );

            navigate(`/artwork/${response.data.id}`, {
                state: {created: true}
            });
        } catch (e) {
            console.error(e);
            setError("Kunstwerk opslaan niet gelukt");
            toast.error("Kunstwerk opslaan mislukt, probeer opnieuw!",
                {
                    duration: 3000,
                    position: "top-center",
                });
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="new-artwork-container">
            <h2 className="new-artwork-header">Kunstwerk toevoegen</h2>
            <form className="new-artwork-form"
                  onSubmit={handleSubmit(handleFormSubmit)}
                  style={{opacity: loading ? 0.6 : 1}}
            >
                <div className="new-artwork-wrapper">

                    {errors.title && <p className="error-message">{errors.title.message}</p>}
                    <InputField as="input"
                                type="text"
                                labelClassName="label-quinary"
                                label="Titel: "
                                name="title"
                                id="title"
                                register={register("title", {required: "Voeg een titel toe"})}
                                required
                                onChange={() => clearErrors("title")}
                    />

                    {errors.genreNames && <p className="error-message">{errors.genreNames.message}</p>}
                    <InputField as="input"
                                type="text"
                                labelClassName="label-quinary"
                                label="Genres (scheid genres met komma's: "
                                name="genreNames"
                                id="genreNames"
                                register={register("genreNames", {required: "Voeg tenminste 1 genre toe"})}
                                placeholder="bijv. schilderij, abstract, modern"
                                onChange={() => clearErrors("genreNames")}
                    />

                    {errors.price && <p className="error-message">{errors.price.message}</p>}
                    <InputField as="input"
                                type="number"
                                labelClassName="label-quinary"
                                label="Prijs: "
                                name="price"
                                id="price"
                                register={register("price", {
                                    required: "Voeg een prijs toe",
                                    min: {value: 0, message: "Prijs moet €0,01 of hoger zijn"}
                                })}
                                step="0.01"
                                placeholder="€"
                                onChange={() => clearErrors("price")}
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
                    />
                    <InputField as="input"
                                type="number"
                                labelClassName="label-quinary"
                                label="Lengte (cm): "
                                name="lengthInCm"
                                id="lengthInCm"
                                register={register}
                    />
                    <InputField as="input"
                                type="number"
                                labelClassName="label-quinary"
                                label="Hoogte (cm): "
                                name="heightInCm"
                                id="heightInCm"
                                register={register}
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
                            register={register("images", {
                                validate: () => images.length > 0 || "Voeg tenminste 1 afbeelding toe"
                            })}
                            multiple
                            accept="image/*"
                            ref={fileInputRef}
                            onChange={(e) => {
                                handleFileInput(e.target.files);
                                clearErrors("images");
                            }}
                />
                {errors.images && (
                    <p className="error-message">{errors.images.message}</p>
                )}

                <div className="image-preview-grid">
                    {images.map((img, index) => (
                        <div key={index}
                             className="image-preview-item"
                             draggable
                             onDragStart={() => handleDragStart(img.id)}
                             onDragEnter={() => handleDragEnter(img.id)}
                             onDragEnd={handleDragEnd}
                        >
                            <img className="image-preview"
                                 src={img.file ? URL.createObjectURL(img.file) : img.url}
                                 alt="preview"
                            />
                            <div className="remove-image">
                                <img src={removeSquare}
                                     alt="close form"
                                     onClick={() => removeImage(img.id)}
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

export default NewArtwork;