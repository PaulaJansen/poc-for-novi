import "./NavLinkItem.css";
import {NavLink} from "react-router-dom";

function NavLinkItem({to, title, icon, alt}) {
    return (
        <li>
            <NavLink className={({isActive}) => isActive ? "nav-item-active" : "nav-item-default"} to={to}>
                {icon && <img src={icon} alt={alt} />}
                {title}
            </NavLink>
        </li>
    )
}

export default NavLinkItem;