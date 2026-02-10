package nl.novi.endassignment.pocbackend.models;

import lombok.Getter;

@Getter
public enum AvailabilityType {
    AVAILABLETOBUY("Te koop"),
    AVAILABLETOLOAN("Te huur"),
    AVAILABLE("Beschikbaar"),
    SOLD("Verkocht"),
    ONLOAN("Uitgehuurd");

    private final String label;

    AvailabilityType(String label) {
        this.label = label;
    }
}


