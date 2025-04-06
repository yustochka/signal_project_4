package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * The ECGDataGenerator class generates ECG data for patients.
 */
public class AlertGenerator implements PatientDataGenerator {
    // Changed constant name from random_generator to RANDOM_GENERATOR (UPPER_SNAKE_CASE)
    public static final Random RANDOM_GENERATOR = new Random();
    // Changed variable name from AlertStates to alertStates (camelCase)
    private boolean[] alertStates; // false = resolved, true = pressed


    /**
     * Constructs an ECGDataGenerator for the specified number of patients.
     *
     * @param patientCount The number of patients.
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates ECG data for a specific patient.
     *
     * @param patientId The ID of the patient.
     * @param outputStrategy The output strategy to use for generating data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name from Lambda to lambda (camelCase)
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
