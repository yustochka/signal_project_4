package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for generating patient data.
 */
public interface PatientDataGenerator {

    /**
     * Generates data for a specific patient.
     *
     * @param patientId The ID of the patient.
     * @param outputStrategy The strategy to use for outputting the generated data.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
