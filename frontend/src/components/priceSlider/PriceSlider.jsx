import "./PriceSlider.css";
import Slider from "rc-slider";
import "rc-slider/assets/index.css";

function PriceSlider({ filters, setFilters, min = 0, max = 90000 }) {
    const onChange = (value) => {
        setFilters({
            ...filters,
            minPrice: value[0],
            maxPrice: value[1],
        });
    };

    return (
        <div className="slider-wrapper">
            <label>Prijs:</label>
            <Slider
                range
                min={min}
                max={max}
                value={[Number(filters.minPrice) || min, Number(filters.maxPrice) || max]}
                onChange={onChange}
                allowCross={false}
                tipFormatter={(value) => `€${value}`}
            />
            <div>
                <span>Min: €{filters.minPrice || min}</span>{" "}
                <span>Max: €{filters.maxPrice || max}</span>
            </div>
        </div>
    );
}

export default PriceSlider;