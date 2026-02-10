import "./NavbarProfileMenu.css";
import {useContext, useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "../../context/AuthContext.js";
import profile from "../../assets/user-icon.svg";

function NavbarProfileMenu() {

    const {auth, logout} = useContext(AuthContext);
    const [isOpen, setIsOpen] = useState(false);
    const navigate = useNavigate();
    const dropdownRef = useRef(null);

    const toggleMenu = () => setIsOpen(prev => !prev);

    const handleLogout = () => {
        logout();
        setIsOpen(false);
    };

    const handleLoginClick = () => {
        navigate("/login");
        setIsOpen(false);
    };

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    return (
        <div className="navbar-profile-container" ref={dropdownRef}>
            <img
                src={profile}
                alt="profile"
                className="navbar-profile-icon"
                onClick={toggleMenu}
            />

            {isOpen && (
                <ul className="navbar-profile-options">
                    {!auth.isAuth ? (
                        <li className="navbar-profile-item" onClick={handleLoginClick}>
                            Inloggen
                        </li>
                    ) : (
                        <>
                            <li className="navbar-profile-item" onClick={() => {
                                navigate("/dashboard");
                                setIsOpen(false);
                            }}
                            >
                                Profiel
                            </li>
                            <li className="navbar-profile-item" onClick={handleLogout}>
                                Uitloggen
                            </li>
                        </>
                    )}
                </ul>
            )}
        </div>
    );
}

export default NavbarProfileMenu;