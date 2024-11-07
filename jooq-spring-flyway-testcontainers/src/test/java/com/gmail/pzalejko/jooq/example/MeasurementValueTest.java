package com.gmail.pzalejko.jooq.example;

import com.gmail.pzalejko.joo.examle.db.tables.Measurement;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test-containers")
public class MeasurementValueTest {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private JooqMeasurementRepository repository;

    @BeforeEach
    public void setUp() {
        assertThat(repository.count()).isEqualTo(0);
    }

    @AfterEach
    public void tearDown() {
        dsl.deleteFrom(Measurement.MEASUREMENT).execute();
    }

    @Test
    public void shouldCreateNewMeasurement() {
        // given
        MeasurementValue measurementValue = new MeasurementValue(1, Instant.now(), Unit.CM);

        // when
        repository.saveNew(measurementValue);

        // then
        assertThat(repository.count()).isEqualTo(1);
    }
}
