import { AuthContext } from "./AuthContext";
import {useEffect, useMemo, useState, useCallback} from "react";
import {useNavigate} from "react-router-dom";
import {jwtDecode} from "jwt-decode";
import axios from "axios";
import isTokenValid from "../helpers/isTokenValid.js";
import {toast} from "react-toastify";
import Spinner from "../components/spinner/Spinner.jsx";
import API from "../helpers/api.js";

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
            console.log("Gebruiker is uitgelogd");
            toast.info("Je bent uitgelogd!")
        }, []
    );

    const fetchUserInformation = useCallback(
        async (userId, token, redirectUrl = null) => {

            try {
                const response = await API.get(`http://localhost:8080/users/id/${userId}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        }
                    })

                setAuth({
                    isAuth: true,
                    user: response.data,
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
        async (token) => {
            try {
                if (!token) throw new Error("Token ontbreekt");
                localStorage.setItem("token", token);

                const decodedToken = jwtDecode(token);
                const userId = decodedToken.userId;

                if (!userId) throw new Error("Token heeft geen userId");

                await fetchUserInformation(userId, token, "/");
                console.log("Gebruiker is ingelogd");
                toast.success("Je bent ingelogd!");
            } catch (e) {
                console.error("Login mislukt: ", e);
                toast.error("Inloggen mislukt, probeer opnieuw!");
                logout();
            }
        },
        [fetchUserInformation, logout]
    );

    useEffect(() => {
        const token = localStorage.getItem("token");

        if (token) {
            try {
                const decoded = jwtDecode(token);
                const userId = decoded.userId;

                if (!userId) throw new Error("Token bevat geen userId");

                void fetchUserInformation(userId, token);
            } catch (e) {
                console.error("Token ongeldig:", e);
                logout();
            }
        } else {
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
