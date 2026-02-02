import { AuthContext } from "./AuthContext";
import {useEffect, useMemo, useState, useCallback} from "react";
import {useNavigate} from "react-router-dom";
import {jwtDecode} from "jwt-decode";
import axios from "axios";
import isTokenValid from "../helpers/isTokenValid.js";
import {toast} from "react-toastify";
import Spinner from "../components/spinner/Spinner.jsx";

export default function AuthContextProvider({children}) {

    const [auth, setAuth] = useState({
        isAuth: false,
        user: null,
        status: "pending",
    });

    function resetAuthState() {
        setAuth({
            isAuth: false,
            user: null,
            status: "done",
        });
    }

    const navigate = useNavigate();

    const logout = useCallback(
        () => {
            localStorage.removeItem("token");
            setAuth({
                isAuth: false,
                user: null,
                status: "done"
            });
            navigate("/");
            console.log("Gebruiker is uitgelogd");
            toast.success("Je bent uitgelogd!")
        }, [navigate]
    );

    const fetchUserInformation = useCallback(
        async (decodedToken, token, redirectUrl = null) => {
            const { userId, roleNames } = decodedToken;

            try {
                const response = await axios.get(`http://localhost:8080/users/${userId}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        }
                    })

                setAuth({
                    isAuth: true,
                    user: {
                        id: response.data.id,
                        email: response.data.email,
                        role: roleNames,
                    },
                    status: "done",
                });

                if (redirectUrl) {
                    navigate(redirectUrl);
                }
            } catch (e) {
                console.error(e);
                logout();
            }
        }, [navigate, logout]
    );

    const login = useCallback(
        (userDetails) => {

            localStorage.setItem("token", userDetails.token);

            try {
                const decodedToken = jwtDecode(userDetails.token);
                void fetchUserInformation(decodedToken, userDetails.token, "/");
                console.log("Gebruiker is ingelogd");
                toast.success("Je bent ingelogd!")
            } catch {
                console.error("Token ongeldig");
                toast.error("Inloggen mislukt, probeer opnieuw!");
                logout();
            }
        },
        [fetchUserInformation, logout]
    );

    useEffect(() => {
        const token = localStorage.getItem("token");

        if (!token) {
            resetAuthState();
            return;
        }

        try {
            const decodedToken = jwtDecode(token);

            if (!isTokenValid(decodedToken)) {
                resetAuthState();
                return;
            }

            void fetchUserInformation(decodedToken, token);
        } catch {
            resetAuthState();
        }
    }, [fetchUserInformation, logout]);

    const contextData = useMemo(
        () => ({
            auth,
            login,
            logout,
        }),
        [auth, login, logout]
    );

    return (
        <AuthContext.Provider value={contextData}>
            {auth.status === 'done' ? children : <Spinner size="default" text="Laden..."/>}
        </AuthContext.Provider>
    )
}
