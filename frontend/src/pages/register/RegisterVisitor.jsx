import "./Register.css";
import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import axios from "axios";
import InputField from "../../components/inputField/InputField.jsx";
import Button from "../../components/button/Button.jsx";

function RegisterVisitor() {
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const {register, handleSubmit} = useForm();

    async function handleFormSubmit(data) {
        try {
            await axios.post(`http://localhost:8080/visitors/register`, data);
            console.log("Bezoeker is geregistreerd!");
            navigate("/login");
        } catch (e) {
            setError("Registreren niet gelukt");
        }
    }

    return (
        <div className="register-container">
            <h2 className="header-register">Registreer</h2>
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
                Liever registreren als kunstenaar? <Link className="link-register" to="/register-artist">Dat doe je hier {">>"}</Link>
            </p>
            {error && (
                <p className="error">{error}</p>
            )}
        </div>
    )
}

export default RegisterVisitor;
