package com.gmail.pzalejko.jooq.example;

import com.google.common.base.Preconditions;

import java.time.Instant;

public record MeasurementValue(double value, Instant timestamp, Unit unit) {

    public MeasurementValue {
        Preconditions.checkArgument(value > 0);
        Preconditions.checkNotNull(unit);
        Preconditions.checkNotNull(timestamp);
    }
}
