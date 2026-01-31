import './NewArtwork.css';
import axios from "axios";
import {useForm} from "react-hook-form";
import {useEffect, useRef, useState} from "react";
import InputField from "../../components/inputField/InputField.jsx";
import Button from "../../components/button/Button.jsx";

function NewArtwork() {

    const {register, handleSubmit, setValue} = useForm();
    const fileInputRef = useRef(null);

    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const [selectedImages, setSelectedImages] = useState([]);

    const MAX_IMAGES = 8;

    useEffect(() => {
        return () => {
            selectedImages.forEach(file =>
                URL.revokeObjectURL(file)
            );
        };
    }, [selectedImages]);

    async function handleFormSubmit(data) {
        setLoading(true);
        try {
            const formData = new FormData();
            formData.append("title", data.title);
            formData.append("price", data.price);
            formData.append("availability", data.availability);

            if (data.genreNames) {
                data.genreNames.forEach(g => formData.append("genreNames", g));
            }

            if (data.images) {
                Array.from(data.images).forEach(file => formData.append("images", file));
            }

            formData.append("widthInCm", data.widthInCm || 0);
            formData.append("lengthInCm", data.lengthInCm || 0);
            formData.append("heightInCm", data.heightInCm || 0);

            await axios.post(`http://localhost:8080/artworks`, formData,
                {
                    headers: {"Content-Type": "multipart/form-data"}
                });

            console.log("Kunstwerk is opgeslagen");
        } catch (e) {
            console.error(e);
            setError("Kunstwerk opslaan niet gelukt");
        } finally {
            setLoading(false);
        }
    }

    function handleImageChange(e) {
        const newFiles = Array.from(e.target.files);

        setSelectedImages(prev => {
            const combined = [...prev, ...newFiles].slice(0, MAX_IMAGES);
            setValue("images", combined);
            return combined;
        });

        e.target.value = null;
    }

    function handleDrop(e) {
        e.preventDefault();
        const files = Array.from(e.dataTransfer.files).filter(
            file => file.type.startsWith("image/")
        );

        setSelectedImages(prev => {
            const combined = [...prev, ...files].slice(0, MAX_IMAGES);
            setValue("images", combined);
            return combined;
        });
    }

    function handleDragOver(e) {
        e.preventDefault();
    }

    function removeImage(index) {
        setSelectedImages(prev => {
            const updated = prev.filter((_, i) => i !== index);
            setValue("images", updated);
            return updated;
        });
    }

    function moveImageUp(index) {
        if (index === 0) return;

        setSelectedImages(prev => {
            const copy = [...prev];
            [copy[index - 1], copy[index]] = [copy[index], copy[index - 1]];
            setValue("images", copy);
            return copy;
        });
    }

    function moveImageDown(index) {
        setSelectedImages(prev => {
            if (index === prev.length - 1) return prev;
            const copy = [...prev];
            [copy[index + 1], copy[index]] = [copy[index], copy[index + 1]];
            setValue("images", copy);
            return copy;
        });
    }

    return (
        <div className="new-artwork-container">
            <h2 className="new-artwork-header">Kunstwerk toevoegen</h2>
            <form className="new-artwork-form" onSubmit={handleSubmit(handleFormSubmit)}>
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
                            onChange={handleImageChange}
                />
                <div className="image-preview-grid">
                    {selectedImages.map((file, index) => (
                        <div key={index} className="image-preview-item">
                            <img
                                src={URL.createObjectURL(file)}
                                alt="preview"
                            />
                            <button
                                type="button"
                                onClick={() => removeImage(index)}
                            >
                                ✕
                            </button>
                        </div>
                    ))}
                </div>
                <button type="button" onClick={() => moveImageUp(index)}>↑</button>
                <button type="button" onClick={() => moveImageDown(index)}>↓</button>
                <Button className="button-default button-tertiary-reverse button-form"
                        type="submit"
                        label="Kunstwerk opslaan"
                />
            </form>
        </div>
    )
}

export default NewArtwork;