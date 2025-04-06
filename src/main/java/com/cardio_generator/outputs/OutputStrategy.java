package com.cardio_generator.outputs;

/**
 * Interface for outputting generated data.
 */
public interface OutputStrategy {

    /**
     * Outputs the generated data.
     *
     * @param patientId The ID of the patient.
     * @param timestamp The timestamp of the data.
     * @param label The label for the data.
     * @param data The actual data to be outputted.
     */
    void output(int patientId, long timestamp, String label, String data);
}
