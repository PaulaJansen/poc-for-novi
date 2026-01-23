import "./App.css"
import {Route, Routes, useNavigate} from "react-router-dom";
import Home from "./pages/home/Home.jsx";
import Overview from "./pages/overview/Overview.jsx";
import Artist from "./pages/artist/Artist.jsx";
import Artwork from "./pages/artwork/Artwork.jsx";
import UserVisitor from "./pages/userVisitor/UserVisitor.jsx";
import UserArtist from "./pages/userArtist/UserArtist.jsx";
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

function App() {

    const navigate = useNavigate();

    return (
        <>
            <nav className="navbar">
                <img className="navbar-logo" src={logo} alt="logo"/>
                <ul className="navbar-menu">
                    <NavLinkItem to={"/"} title="Home"/>
                    <NavLinkItem to={"/overview"} title="All Art"/>
                    <NavLinkItem to={"/register-artlover"} title="Register"/>
                    <NavLinkItem to={"/profile"} icon={profile} alt="profile"/>
                </ul>
            </nav>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/overview" element={<Overview/>}/>
                <Route path="/artist/:id" element={<Artist/>}/>
                <Route path="/artwork/:id" element={<Artwork/>}/>
                <Route path="/user/:id" element={<UserVisitor/>}/>
                <Route path="/artist/:id" element={<UserArtist/>}/>
                <Route path="/new-artwork" element={<NewArtwork/>}/>
                <Route path="/register-artist" element={<RegisterArtist/>}/>
                <Route path="/register-artlover" element={<RegisterVisitor/>}/>
                <Route path="/login" element={<Login/>}/>
            </Routes>
            <section className="footer">
                <div className="footer-wrapper">
                    <div className="footer-info-wrapper">
                        <img className="footer-logo" src={logoSmall} alt="logo"/>
                        <p className="footer-text">© {getCurrentYear()}</p>
                    </div>
                    <Button className="button-default button-primary footer-navlink-wrapper"
                            type="button"
                            onClick={() => navigate("/register-artist")}
                            label="Register als kunstenaar"
                    />
                </div>
                <div className="circle-small"></div>
            </section>
        </>
    )
}

export default App
