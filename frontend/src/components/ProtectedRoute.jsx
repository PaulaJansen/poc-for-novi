import {useContext, useEffect, useRef} from "react";
import {AuthContext} from '../context/AuthContext';
import {Navigate} from "react-router-dom";
import Spinner from "./spinner/Spinner.jsx";
import {toast} from "react-toastify";

export default function ProtectedRoute({children, requiredRole}) {

    const {auth} = useContext(AuthContext);
    const toastShown = useRef(false);

    useEffect(() => {
        if (requiredRole && auth.user && !auth.user.roleNames.includes(requiredRole) && !toastShown.current) {
            toast.error("Geen toegang tot deze actie");
            toastShown.current = true;
        }
    }, [auth.user, requiredRole]);

    if (auth.status !== "done") return <Spinner size="default" text="Laden..."/>;

    if (!auth.isAuth) return <Navigate to="/login" replace/>

    if (requiredRole && auth.user && !auth.user.roleNames.includes(requiredRole))
        return <Navigate to="/dashboard" replace />;

    return children;
}
