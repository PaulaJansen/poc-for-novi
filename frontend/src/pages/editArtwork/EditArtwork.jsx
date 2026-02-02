import "./EditArtwork.css";
import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import {useForm} from "react-hook-form";
import axios from "axios";
import Spinner from "../../components/spinner/Spinner.jsx";
import InputField from "../../components/inputField/InputField.jsx";
import removeSquare from "../../assets/x-square-fill.svg";
import Button from "../../components/button/Button.jsx";
import useImageUpload from "../../customHooks/useImageUpload.jsx";

function EditArtwork() {

    const {id} = useParams();
    const navigate = useNavigate();
    const fileInputRef = useRef(null);
    const {register, handleSubmit, reset, setValue} = useForm();

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [artwork, setArtwork] = useState(null);

    const imageUpload = useImageUpload({
        setValue,
        maxImages: 8,
        initialImages: []
    });

    const {
        images,
        setImages,
        handleFileInput,
        handleDrop,
        handleDragOver,
        removeImage,
        handleDragStart,
        handleDragEnter,
        handleDragEnd
    } = imageUpload;

    useEffect(() => {
        async function fetchArtwork() {
            try {
                const response = await axios.get(`http://localhost:8080/artworks/${id}`);
                const data = response.data;

                reset({
                    title: data.title,
                    price: data.price,
                    availability: data.availability,
                    widthInCm: data.widthInCm,
                    lengthInCm: data.lengthInCm,
                    heightInCm: data.heightInCm,
                    genreNames: data.genreNames,
                });

                if (data.images?.length) {
                    const prefillImages = data.images.map(url => ({ file: null, url }));
                    imageUpload.setImages(prefillImages);
                    setValue("images", prefillImages);
                }

                setArtwork(data);
            } catch (e) {
                console.error(e);
                setError("Kunstwerk ophalen mislukt");
            } finally {
                setLoading(false);
            }
        }

        fetchArtwork();
    }, []);

    async function handleFormSubmit(data) {

        console.log("Form data:", data);
        console.log("Hook images:", images);
        setLoading(true);
        try {
            const formData = new FormData();
            formData.append("title", data.title);
            formData.append("price", data.price);
            formData.append("availability", data.availability);

            data.genreNames?.forEach(g =>
                formData.append("genreNames", g)
            );

            images.forEach(img => {
                if (img.file) formData.append("images", img.file);
            });

            formData.append("widthInCm", data.widthInCm || 0);
            formData.append("lengthInCm", data.lengthInCm || 0);
            formData.append("heightInCm", data.heightInCm || 0);

            await axios.patch(`http://localhost:8080/artworks/${id}`,
                formData, {
                    headers: {"Content-Type": "multipart/form-data"}
                });

            navigate(`/artwork/${id}`);
        } catch (e) {
            console.error(e);
            setError("Opslaan niet gelukt");
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
            <h2 className="new-artwork-header">Kunstwerk toevoegen</h2>
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
                    {images.map((img, index) => (
                        <div key={index}
                             className="image-preview-item"
                             draggable
                             onDragStart={() => handleDragStart(index)}
                             onDragEnter={() => handleDragEnter(index)}
                             onDragEnd={handleDragEnd}
                        >
                            <img className="image-preview"
                                 src={img.file ? URL.createObjectURL(img.file) : `http://localhost:8080/images/${img.url}`}
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