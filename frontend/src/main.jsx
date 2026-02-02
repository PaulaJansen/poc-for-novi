import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import {BrowserRouter as Router} from "react-router-dom";
import FavoritesContextProvider from "./context/FavoritesContextProvider.jsx";
import AuthContextProvider from "./context/AuthContextProvider.jsx";

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <Router>
            <AuthContextProvider>
                <FavoritesContextProvider>
                    <App/>
                </FavoritesContextProvider>
            </AuthContextProvider>
        </Router>
    </StrictMode>,
)
