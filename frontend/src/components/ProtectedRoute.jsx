import {useContext} from "react";
import {AuthContext} from '../context/AuthContext';
import {Navigate} from "react-router-dom";
import Spinner from "./spinner/Spinner.jsx";

export default function ProtectedRoute({children}) {

    const {auth} = useContext(AuthContext);

    if (auth.status !== "done") return <Spinner size="default" text="Laden..."/>;

    if (!auth.isAuth) {
        return <Navigate to="/login"/>;
    }

    return children;
}
