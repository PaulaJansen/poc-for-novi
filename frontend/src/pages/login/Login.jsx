import "./Login.css";
import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import axios from "axios";
import InputField from "../../components/inputField/InputField.jsx";
import Button from "../../components/button/Button.jsx";

function Login() {

    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const {register, handleSubmit} = useForm();

    async function handleFormSubmit(data) {
        try {
            await axios.post(`http://localhost:8080/auth`, data);
            console.log("Kunstenaar is geregistreerd!");
            navigate("/login");
        } catch (e) {
            setError("Inloggen niet gelukt");
        }
    }
    return (
        <div className="register-container">
            <h2 className="header-register">Log in</h2>
            <form onSubmit={handleSubmit(handleFormSubmit)}>
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
                        label="Inloggen"
                />
            </form>
            {error && (
                <p className="error">{error}</p>
            )}
        </div>
    )
}

export default Login;