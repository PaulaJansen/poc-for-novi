import './NewArtwork.css';
import axios from "axios";
import {useForm} from "react-hook-form";
import {useState} from "react";
import InputField from "../../components/inputField/InputField.jsx";
import Button from "../../components/button/Button.jsx";

function NewArtwork() {

    const {register, handleSubmit, reset} = useForm();
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

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

    return (
        <div className="new-artwork-container">
            <h2>Kunstwerk toevoegen</h2>
            <form onSubmit={handleSubmit(handleFormSubmit)}>
                <InputField as="input"
                            type="text"
                            label="Titel: "
                            name="title"
                            id="title"
                            register={register}
                            required
                />
                <InputField as="input"
                            type="text"
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
                            label="Beschikbaarheid: "
                            name="availability"
                            id="availability"
                            register={register}
                            required
                            options={[
                                {value: "AVAILABLETOBUY", label: "Te koop"},
                                {value: "AVAILABLETOLOAN", label: "Te huur"},
                                {value: "AVAILABLE", label: "Beschikbaar"},
                                {value: "SOLD", label: "Verkocht"},
                                {value: "ONLOAN", label: "Verhuurd"}
                            ]}
                />
                <div className="dimensions">
                    <InputField as="input"
                                type="number"
                                label="Breedte (cm): "
                                name="widthInCm"
                                id="widthInCm"
                                register={register}
                                required
                    />
                    <InputField as="input"
                                type="number"
                                label="Lengte (cm): "
                                name="lengthInCm"
                                id="lengthInCm"
                                register={register}
                                required
                    />
                    <InputField as="input"
                                type="number"
                                label="Hoogte (cm): "
                                name="heightInCm"
                                id="heightInCm"
                                register={register}
                                required
                    />
                </div>
                <InputField as="input"
                            type="file"
                            label="Afbeeldingen: "
                            name="images"
                            id="images"
                            register={register}
                            required
                            multiple
                />
                <Button className="button-default button-tertiary-reverse button-form"
                        type="submit"
                        label="Kunstwerk opslaan"
                />
            </form>
        </div>
    )
}

export default NewArtwork;