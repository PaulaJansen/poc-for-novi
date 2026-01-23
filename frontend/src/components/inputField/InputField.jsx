import "./InputField.css";

function InputField({label, as = "input", type, className, labelClassName, name, id, register, required, value, options = [], placeholder = "", onChange}) {
    const Component = as === "textarea" ? "textarea" :
        as === "select" ? "select" : "input";
    const wrapInputInLabel = type === "radio" || type === "checkbox";

    if (as === "select") {
        return (
            <label htmlFor={id} className={`label-primary ${labelClassName}`}>
                <span>{label}</span>
                <Component
                    className={`${className || ""} input-field`}
                    id={id}
                    onChange={onChange}
                    value={value}
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
            <label htmlFor={id} className={`label-tertiary ${labelClassName}`}>
                <span>{label}</span>
                <Component
                    className={className}
                    {...(typeof register === "function" ? register(name, {required}) : {})}
                    id={id}
                    placeholder={placeholder}
                />
            </label>
        )
    } else {
        return wrapInputInLabel ? (
            <label htmlFor={id} className={`label-secondary ${labelClassName}`}>
                <Component
                    className={`${className || ""} input-field`}
                    {...(typeof register === "function" ? register(name, {required}) : {})}
                    id={id}
                    value={value}
                    type={type}
                    onChange={e => onChange(e.target.value)}
                    placeholder={placeholder}
                />
                <span>{label}</span>
            </label>
        ) : (
            <label htmlFor={id} className={`label-primary ${labelClassName}`}>
                <span>{label}</span>
                <Component
                    className={`${className || ""} input-field`}
                    {...(typeof register === "function" ? register(name, {required}) : {})}
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