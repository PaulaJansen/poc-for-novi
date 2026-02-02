import "./App.css"
import {Link, Route, Routes, useNavigate} from "react-router-dom";
import Home from "./pages/home/Home.jsx";
import Overview from "./pages/overview/Overview.jsx";
import Artist from "./pages/artist/Artist.jsx";
import Artwork from "./pages/artwork/Artwork.jsx";
import UserVisitor from "./pages/userDashboard/UserVisitor.jsx";
import UserArtist from "./pages/userDashboard/UserArtist.jsx";
import NewArtwork from "./pages/newArtwork/NewArtwork.jsx";
import RegisterArtist from "./pages/register/RegisterArtist.jsx";
import RegisterVisitor from "./pages/register/RegisterVisitor.jsx";
import Login from "./pages/login/Login.jsx";
import logo from "./assets/logo-aloa-nbg.png";
import logoSmall from "./assets/logo-aloa-small.png";
import NavLinkItem from "./components/navLinkItem/NavLinkItem.jsx";
import profile from "./assets/user-icon.svg";
import {getCurrentYear} from "./helpers/getCurrentYear.js";
import Button from "./components/button/Button.jsx";
import {ToastContainer} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import EditArtwork from "./pages/editArtwork/EditArtwork.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import NavbarProfileMenu from "./components/navbarProfileMenu/NavbarProfileMenu.jsx";
import UserDashboard from "./pages/userDashboard/UserDashboard.jsx";

function App() {

    const navigate = useNavigate();

    return (
        <div className="app-container">
            <nav className="navbar">
                <Link to="/">
                    <img className="navbar-logo" src={logo} alt="logo"/>
                </Link>
                <ul className="navbar-menu">
                    <NavLinkItem to={"/"} title="Home"/>
                    <NavLinkItem to={"/overview"} title="All Art"/>
                    <NavLinkItem to={"/register-artlover"} title="Register"/>
                    <NavbarProfileMenu/>
                </ul>
            </nav>
            <div className="main-container">
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/overview" element={<Overview/>}/>
                    <Route path="/artist/:id" element={<Artist/>}/>
                    <Route path="/artwork/:id" element={<Artwork/>}/>
                    <Route path="/dashboard" element={
                        <ProtectedRoute>
                            <UserDashboard/>
                        </ProtectedRoute>
                    }
                    />
                    <Route path="/artlover-dashboard/:id" element={
                        <ProtectedRoute>
                            <UserVisitor/>
                        </ProtectedRoute>
                    }
                    />
                    <Route path="/artist-dashboard/:id" element={
                        <ProtectedRoute>
                            <UserArtist/>
                        </ProtectedRoute>
                    }
                    />
                    <Route path="/new-artwork" element={
                        <ProtectedRoute requiredRole="ROLE_ARTIST">
                            <NewArtwork/>
                        </ProtectedRoute>
                    }
                    />
                    <Route path="/register-artist" element={<RegisterArtist/>}/>
                    <Route path="/register-artlover" element={<RegisterVisitor/>}/>
                    <Route path="/login" element={<Login/>}/>
                    <Route path="/edit-artwork/:id" element={
                        <ProtectedRoute requiredRole="ROLE_ARTIST">
                            <EditArtwork/>
                        </ProtectedRoute>
                    }
                    />
                </Routes>
            </div>
            <section className="footer">
                <div className="footer-wrapper">
                    <div className="footer-info-wrapper">
                        <img className="footer-logo" src={logoSmall} alt="logo"/>
                        <p className="footer-text">© {getCurrentYear()}</p>
                    </div>
                    <Button className="button-default button-tertiary-reverse footer-navlink-wrapper"
                            type="button"
                            onClick={() => navigate("/register-artist")}
                            label="Register als kunstenaar"
                    />
                </div>
                <div className="circle-small"></div>
            </section>
            <ToastContainer
                position="top-center"
                autoClose={3000}
                hideProgressBar={false}
            />
        </div>
    )
}

export default App
