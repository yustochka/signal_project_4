package com.data_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alerts.AlertFactory;
import com.alerts.AlertGenerator;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {

    // Thread-safe map for concurrent access
    private static volatile DataStorage instance;
    private final ConcurrentMap<Integer, PatientRecordContainer> patientMap;

    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    protected DataStorage() {
        this.patientMap = new ConcurrentHashMap<>();
    }


    /**
     * Returns the singleton instance of DataStorage.
     * This method ensures that only one instance of DataStorage exists in the application.
     *
     * @return the singleton instance of DataStorage
     */
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    /**
     * Sets the singleton instance of DataStorage.
     * This method is used for testing purposes to use a mock or alternative instance.
     *
     * @param testInstance the test instance of DataStorage to set
     */
    public static synchronized void setInstance(DataStorage testInstance) {
        instance = testInstance;
    }


    /**
     * Adds or updates patient data in real-time, deduplicating by timestamp and type.
     * Uses a per-patient lock to handle concurrent updates.
     *
     * @param patientId        patient's ID
     * @param measurementValue the value of the health metric
     * @param recordType       type of record (e.g., "HeartRate")
     * @param timestamp        reading time in millis
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        // Get or create container atomically
        PatientRecordContainer container =
                patientMap.computeIfAbsent(patientId, id -> new PatientRecordContainer(new Patient(id)));

        // Delegate to container which handles locking and deduplication
        container.addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieves records for a given patient within a time range.
     * @param patientId the ID of the patient
     * @return a list of PatientRecord objects within the specified time range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        PatientRecordContainer container = patientMap.get(patientId);
        if (container != null) {
            return container.getRecords(startTime, endTime);
        }
        return new ArrayList<>();
    }

    /**
     * Returns all patients currently stored.
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        for (PatientRecordContainer container : patientMap.values()) {
            list.add(container.getPatient());
        }
        return list;
    }

    /**
     * Container pairing a Patient with a ReadWriteLock and deduplication logic.
     */
    private static class PatientRecordContainer {
        private final Patient patient;
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        PatientRecordContainer(Patient patient) {
            this.patient = patient;
        }
        /**
         * Adds a record to the patient, ensuring no duplicates by timestamp and type.
         * Uses a write lock to ensure thread safety.
         *
         * @param value     the measurement value
         * @param type      the type of record
         * @param timestamp the timestamp of the record
         */

        void addRecord(double value, String type, long timestamp) {
            lock.writeLock().lock();
            try {
                //deduplicate before adding
                if (patient.getRecords(timestamp, timestamp).stream()
                        .noneMatch(record -> record.getRecordType().equals(type))) {
                    patient.addRecord(value, type, timestamp);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        /**
         * Retrieves records for the patient within a specified time range.
         * Uses a read lock to ensure thread safety.
         *
         * @param  start the start time
         * @param end   the end time
         * @return a list of PatientRecord objects within the specified time range
         */

        List<PatientRecord> getRecords(long start, long end) {
            lock.readLock().lock();
            try {
                return patient.getRecords(start, end);
            } finally {
                lock.readLock().unlock();
            }
        }

        Patient getPatient() {
            return patient;
        }
    }
    /**
     * The main method for the DataStorage class.
     * Initializes the system, reads data into storage, and continuously monitors
     * and evaluates patient data.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // DataReader is not defined in this scope, should be initialized appropriately.
        // DataReader reader = new SomeDataReaderImplementation("path/to/data");
        DataStorage storage = new DataStorage();

        // Assuming the reader has been properly initialized and can read data into the
        // storage
        // reader.readData(storage);

        // Example of using DataStorage to retrieve and print records for a patient
        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);
        for (PatientRecord record : records) {
            System.out.println("Record for Patient ID: " + record.getPatientId() +
                    ", Type: " + record.getRecordType() +
                    ", Data: " + record.getMeasurementValue() +
                    ", Timestamp: " + record.getTimestamp());
        }

        // Initialize the AlertGenerator with the storage
        AlertGenerator alertGenerator = new AlertGenerator(storage, new ArrayList<>(), new AlertFactory());
        // Evaluate all patients' data to check for conditions that may trigger alerts
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
    }

}
