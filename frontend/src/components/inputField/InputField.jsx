import "./InputField.css";

function InputField({label, as = "input", type, className, name, id, register, value, options = [], placeholder = "", onChange}) {
    const Component = as === "textarea" ? "textarea" :
        as === "select" ? "select" : "input";
    const wrapInputInLabel = type === "radio" || type === "checkbox";

    if (as === "select") {
        return (
            <label htmlFor={id} className="label-primary">
                <span>{label}</span>
                <Component
                    className={className}
                    id={id}
                    {...(typeof register === "function" ? register(name) : {})}>
                    {options?.map((option) => (
                        <option key={option.value} value={option.value} disabled={option.disabled}
                                hidden={option.hidden}>
                            {option.label}
                        </option>))}
                </Component>
            </label>
        )
    } else if (as === "textarea") {
        return (
            <label htmlFor={id} className="label-tertiary">
                <span>{label}</span>
                <Component
                    className={className}
                    {...(typeof register === "function" ? register(name) : {})}
                    id={id}
                    placeholder={placeholder}
                />
            </label>
        )
    } else {
        return wrapInputInLabel ? (
            <label htmlFor={id} className="label-secondary">
                <Component
                    className={className}
                    {...(typeof register === "function" ? register(name) : {})}
                    id={id}
                    value={value}
                    type={type}
                    onChange={e => onChange(e.target.value)}
                    placeholder={placeholder}
                />
                <span>{label}</span>
            </label>
        ) : (
            <label htmlFor={id} className="label-primary">
                <span>{label}</span>
                <Component
                    className={className}
                    {...(typeof register === "function" ? register(name) : {})}
                    id={id}
                    type={type}
                    onChange={onChange}
                    placeholder={placeholder}
                />
            </label>
        )
    }
}

export default InputField;