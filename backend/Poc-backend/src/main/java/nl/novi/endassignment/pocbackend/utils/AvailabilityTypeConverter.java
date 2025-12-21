package nl.novi.endassignment.pocbackend.utils;

import org.springframework.core.convert.converter.Converter;
import nl.novi.endassignment.pocbackend.models.AvailabilityType;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityTypeConverter implements Converter<String, AvailabilityType> {

    @Override
    public AvailabilityType convert(String source) {
        try {
            return AvailabilityType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Ongeldige availability: " + source);
        }
    }
}
