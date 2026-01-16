import "./Spinner.css";

function Spinner({size = "default", overlay = false, text}) {

    if (overlay) {
        return (
            <div className="spinner-overlay">
                <div className={`spinner spinner-${size}`}/>
                {text && <p>{text}</p>}
            </div>
        );
    }

    return (
        <span className="spinner-page">
            <span className={`spinner spinner-${size}`}/>
            {text && <span className="spinner-text">{text}</span>}
        </span>
    );
}

export default Spinner;