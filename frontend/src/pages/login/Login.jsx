import "../register/Register.css";
import {useContext, useState} from "react";
import {useForm} from "react-hook-form";
import axios from "axios";
import InputField from "../../components/inputField/InputField.jsx";
import Button from "../../components/button/Button.jsx";
import {AuthContext} from "../../context/AuthContext.js";
import {useNavigate} from "react-router-dom";
import {toast} from "react-toastify";

function Login() {

    const [error, setError] = useState(null);
    const {register, handleSubmit} = useForm();
    const {login} = useContext(AuthContext);
    const navigate = useNavigate();

    const handleFormSubmit = async (data) => {
        try {
            const response = await axios.post("http://localhost:8080/auth", data);
            const token = response.data.token;

            if (!token) {
                throw new Error("Geen token ontvangen van backend");
            }

            login(token);
            navigate("/");
        } catch (e) {
            console.error("Login error:", e);
            setError("Inloggen niet gelukt. Controleer je gebruikersnaam en wachtwoord.");
            toast.error("Inloggen mislukt!");
        }
    };

    return (
        <div className="register-container">
            <h2 className="header-register">Log in</h2>
            <form onSubmit={handleSubmit(handleFormSubmit)}>
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