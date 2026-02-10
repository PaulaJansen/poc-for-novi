import {useContext} from "react";
import UserArtist from "./UserArtist.jsx";
import UserVisitor from "./UserVisitor.jsx";
import Spinner from "../../components/spinner/Spinner.jsx";
import {AuthContext} from "../../context/AuthContext.js";
import {Link} from "react-router-dom";

function UserDashboard() {

    const {auth} = useContext(AuthContext);

    if (auth.status !== "done") return <Spinner size="default" text="Laden..."/>;

    if (!auth.isAuth) return (
        <p className="navigate-register">
            <Link className="link-register" to="/login">Log eerst in!</Link>
        </p>
    );

    if (auth.user?.roleNames?.includes("ARTIST")) return <UserArtist id={auth.user.id}/>;
    if (auth.user?.roleNames?.includes("VISITOR")) return <UserVisitor id={auth.user.id}/>;

    return <p className="error">Je hebt geen toegang tot deze pagina.</p>;
}

export default UserDashboard;