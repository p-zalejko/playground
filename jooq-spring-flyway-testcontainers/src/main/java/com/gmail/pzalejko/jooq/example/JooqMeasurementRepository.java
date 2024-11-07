package com.gmail.pzalejko.jooq.example;

import com.gmail.pzalejko.joo.examle.db.enums.Measurementunit;
import com.gmail.pzalejko.joo.examle.db.tables.Measurement;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Repository
class JooqMeasurementRepository {

    private final DSLContext dsl;

    JooqMeasurementRepository(DSLContext dsl) {
        this.dsl = Objects.requireNonNull(dsl);
    }

    void saveNew(MeasurementValue value) {
        Objects.requireNonNull(value);
        dsl.insertInto(Measurement.MEASUREMENT)
                .set(Measurement.MEASUREMENT.VALUE, BigDecimal.valueOf(value.value()))
                .set(Measurement.MEASUREMENT.TIMESTAMP, LocalDateTime.ofInstant(value.timestamp(), ZoneOffset.UTC))
                .set(Measurement.MEASUREMENT.UNIT, DSL.cast(Measurementunit.valueOf(value.unit().name()), Measurementunit.class))
                .execute();
    }

    int count() {
        //bad practice of having such a method, but it's just hello world example app...
        return dsl.fetchCount(Measurement.MEASUREMENT);
    }
}
