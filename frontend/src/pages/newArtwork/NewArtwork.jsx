import './NewArtwork.css';
import axios from "axios";
import {useForm} from "react-hook-form";
import {useRef, useState} from "react";
import InputField from "../../components/inputField/InputField.jsx";
import Button from "../../components/button/Button.jsx";
import removeSquare from "../../assets/x-square-fill.svg"
import useImageUpload from "../../customHooks/useImageUpload.jsx";
import Spinner from "../../components/spinner/Spinner.jsx";
import {toast} from "react-toastify";

function NewArtwork() {

    const {register, handleSubmit, setValue} = useForm();
    const fileInputRef = useRef(null);

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

            data.genreNames?.forEach(g =>
                formData.append("genreNames", g)
            );

            data.images?.forEach(file =>
                formData.append("images", file)
            );

            formData.append("widthInCm", data.widthInCm || 0);
            formData.append("lengthInCm", data.lengthInCm || 0);
            formData.append("heightInCm", data.heightInCm || 0);

            await axios.post(`http://localhost:8080/artworks`, formData,
                {
                    headers: {"Content-Type": "multipart/form-data"}
                });

            toast.success("Kunstwerk succesvol toegevoegd!",
                {
                    duration: 3000,
                    position: "top-center",
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
                            required
                            accept="image/*"
                            ref={fileInputRef}
                            onChange={(e) => handleFileInput(e.target.files)}
                />
                <div className="image-preview-grid">
                    {images.map((file, index) => (
                        <div key={index}
                             className="image-preview-item"
                             draggable
                             onDragStart={() => handleDragStart(index)}
                             onDragEnter={() => handleDragEnter(index)}
                             onDragEnd={handleDragEnd}
                        >
                            <img className="image-preview"
                                 src={URL.createObjectURL(file)}
                                 alt="preview"
                            />
                            <div className="remove-image">
                                <img src={removeSquare}
                                     alt="close form"
                                     onClick={() => removeImage(index)}
                                />
                            </div>
                        </div>
                    ))}
                </div>
                <div className="button-form">
                    <Button className="button-default button-tertiary-reverse"
                            type="submit"
                            disabled={loading}
                            label={loading ? <Spinner /> : "Kunstwerk opslaan"}
                    />
                </div>
            </form>
            {error && <p className="error-message">{error}</p>}
        </div>
    )
}

export default NewArtwork;