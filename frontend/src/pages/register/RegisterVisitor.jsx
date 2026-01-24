import "./Register.css";
import {useState} from "react";
import {useNavigate} from "react-router-dom";
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
                            label="E-mailadres: "
                            id="email"
                            name="email"
                            register={register}
                            placeholder="Email"
                            labelClassName="label-quarternary"
                            required
                />
                <InputField as="input"
                            type="text"
                            label="Gebruikersnaam: "
                            id="username"
                            name="username"
                            register={register}
                            placeholder="Gebruikersnaam"
                            labelClassName="label-quarternary"
                            required
                />
                <InputField as="input"
                            type="password"
                            label="Wachtwoord: "
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
            {error && (
                <p className="error">{error}</p>
            )}
        </div>
    )
}

export default RegisterVisitor;
