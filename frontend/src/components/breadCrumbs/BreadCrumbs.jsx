import "./BreadCrumbs.css";
import {Link} from "react-router-dom";

function Breadcrumbs({lastLabel}) {
    const paths = [
        { to: "/", label: "Uitgelicht" },
        { to: "/overview", label: "Overzicht" },
    ];

    return (
        <nav className="breadcrumbs">
            {paths.map((path) => (
                <span key={path.to}>
                    <Link className="breadcrumbs-link" to={path.to}>{path.label}</Link> /{" "}
                </span>
            ))}
            <span>{lastLabel || "Detail"}</span>
        </nav>
    );
}

export default Breadcrumbs;