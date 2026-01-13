import "./App.css"
import {NavLink, Route, Routes} from "react-router-dom";
import Home from "./pages/home/Home.jsx";
import Overview from "./pages/overview/Overview.jsx";
import Artist from "./pages/artist/Artist.jsx";
import Artwork from "./pages/artwork/Artwork.jsx";
import UserVisitor from "./pages/userVisitor/UserVisitor.jsx";
import UserArtist from "./pages/userArtist/UserArtist.jsx";
import NewArtwork from "./pages/newArtwork/NewArtwork.jsx";
import Register from "./pages/register/Register.jsx";
import logo from "./assets/logo-aloa-nbg.png";
import NavLinkItem from "./components/navLinkItem/NavLinkItem.jsx";
import profile from "./assets/user-icon.svg";

function App() {

    return (
        <>
            <nav className="navbar">
                <img className="navbar-logo" src={logo} alt="logo" />
                <ul className="navbar-menu">
                    <NavLinkItem to={"/"} title="Home" />
                    <NavLinkItem to={"/overview"} title="All Art" />
                    <NavLinkItem to={"/register"} title="Register" />
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
                <Route path="/register" element={<Register/>}/>
            </Routes>
        </>
    )
}

export default App
