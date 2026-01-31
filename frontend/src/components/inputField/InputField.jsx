import "./InputField.css";
import {forwardRef} from "react";

const InputField = forwardRef(function InputField({
                                                      label,
                                                      as = "input",
                                                      type,
                                                      className,
                                                      labelClassName,
                                                      name,
                                                      id,
                                                      register,
                                                      required,
                                                      value,
                                                      options = [],
                                                      placeholder = "",
                                                      onChange
                                                  }, ref
    ) {
        const Component = as === "textarea" ? "textarea" :
            as === "select" ? "select" : "input";

        const wrapInputInLabel = type === "radio" || type === "checkbox";
        const hasLabel = Boolean(label);

        const registerProps =
            typeof register === "function" ? register(name, {required}) : {};

    const combinedRef = (el) => {
        if (ref) {
            if (typeof ref === "function") ref(el);
            else ref.current = el;
        }
        if (registerProps?.ref) registerProps.ref(el);
    };

        if (as === "select") {
            return (
                <label htmlFor={id} className={hasLabel ? `label-primary ${labelClassName}` : ""}>
                    {hasLabel && <span>{label}</span>}
                    <Component
                        className={`${className || ""} input-field`}
                        id={id}
                        onChange={onChange}
                        value={value}
                        ref={combinedRef}
                        {...registerProps}
                    >
                        {options?.map((option) => (
                            <option key={option.value}
                                    value={option.value}
                                    disabled={option.disabled}
                                    hidden={option.hidden}>
                                {option.label}
                            </option>))}
                    </Component>
                </label>
            );
        }

        if (as === "textarea") {
            return (
                <label htmlFor={id} className={hasLabel ? `label-tertiary ${labelClassName}` : ""}>
                    {hasLabel && <span>{label}</span>}
                    <Component
                        className={className}
                        {...registerProps}
                        id={id}
                        placeholder={placeholder}
                        ref={combinedRef}
                    />
                </label>
            );
        }

        return wrapInputInLabel ? (
            <label htmlFor={id} className={hasLabel ? `label-secondary ${labelClassName}` : ""}>
                <Component
                    className={`${className || ""} input-field`}
                    {...registerProps}
                    id={id}
                    value={value}
                    type={type}
                    onChange={e => onChange(e.target.value)}
                    placeholder={placeholder}
                    ref={combinedRef}
                />
                {hasLabel && <span>{label}</span>}
            </label>
        ) : (
            <label htmlFor={id} className={hasLabel ? `label-primary ${labelClassName}` : ""}>
                {hasLabel && <span>{label}</span>}
                <Component
                    className={`${className || ""} input-field`}
                    {...registerProps}
                    id={id}
                    type={type}
                    onChange={onChange}
                    placeholder={placeholder}
                    ref={combinedRef}
                />
            </label>
        );
    }
);


export default InputField;