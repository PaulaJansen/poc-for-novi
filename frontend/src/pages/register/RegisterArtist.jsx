import './Register.css';
import InputField from "../../components/inputField/InputField.jsx";
import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import axios from "axios";
import {useForm} from "react-hook-form";
import Button from "../../components/button/Button.jsx";

function RegisterArtist() {

    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const {register, handleSubmit} = useForm();

    async function handleFormSubmit(data) {
        try {
            await axios.post(`http://localhost:8080/artists/register`, data);
            console.log("Kunstenaar is geregistreerd!");
            navigate("/login");
        } catch (e) {
            console.error(e);
            setError("Registreren niet gelukt");
        }
    }

    return (
        <div className="register-container">
            <h2 className="header-register">Registreer als kunstenaar</h2>
            <form onSubmit={handleSubmit(handleFormSubmit)}>
                <InputField as="input"
                            type="text"
                            id="email"
                            name="email"
                            register={register}
                            placeholder="Email"
                            labelClassName="label-quarternary"
                            required
                />
                <InputField as="input"
                            type="text"
                            id="username"
                            name="username"
                            register={register}
                            placeholder="Gebruikersnaam"
                            labelClassName="label-quarternary"
                            required
                />
                <InputField as="input"
                            type="password"
                            id="password"
                            name="password"
                            register={register}
                            placeholder="Wachtwoord"
                            labelClassName="label-quarternary"
                            required
                />
                <Button className="button-default button-tertiary form-button"
                        type="submit"
                        label="Registreer"
                />
            </form>
            <p className="navigate-register">
                Liever registreren als artlover? <Link className="link-register" to="/register-artlover">Dat doe je hier {">>"}</Link>
            </p>
            {error && (
                <p className="error">{error}</p>
            )}
        </div>
    )
}

export default RegisterArtist;