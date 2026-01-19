import "./Button.css";

function Button({className, type, onClick, label}) {
    return (
        <button className={className} type={type} onClick={onClick}>
            {label}
        </button>
    )
}

export default Button;