import './App.css'
import {Route, Routes} from "react-router-dom";
import Home from "./pages/home/Home.jsx";
import Overview from "./pages/overview/Overview.jsx";
import Artist from "./pages/artist/Artist.jsx";
import Artwork from "./pages/artwork/Artwork.jsx";
import UserVisitor from "./pages/userVisitor/UserVisitor.jsx";
import UserArtist from "./pages/userArtist/UserArtist.jsx";
import NewArtwork from "./pages/newArtwork/NewArtwork.jsx";
import Register from "./pages/register/Register.jsx";

function App() {

    return (
        <>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/overview" element={<Overview/>}/>
                <Route path="/artist/:id" element={<Artist/>}/>
                <Route path="/artwork/:id" element={<Artwork/>}/>
                <Route path="/user/:id" element={<UserVisitor/>}/>
                <Route path="/user-artist/:id" element={<UserArtist/>}/>
                <Route path="/new-artwork" element={<NewArtwork/>}/>
                <Route path="/register" element={<Register/>}/>
            </Routes>
        </>
    )
}

export default App
