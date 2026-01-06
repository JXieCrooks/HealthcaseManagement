package Controller;

import Model.Prescription;
import Utils.FileWriterUtil;
import Utils.CSVReader;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionController {

    private final String csvPath = "data/prescriptions.csv";
    private final String outputPath = "output/prescriptions_output.txt"; // output file
    private List<Prescription> prescriptions;

    public PrescriptionController() {
        prescriptions = new ArrayList<>();
        loadFromCSV();
        exportToOutput();
    }

    public void loadFromCSV() {
        prescriptions.clear();
        List<String[]> rows = CSVReader.readCSV(csvPath);
        for (String[] r : rows) {
            if (r.length < 15 || "prescription_id".equals(r[0])) continue;

            prescriptions.add(new Prescription(
                    r[0], r[1], r[2], r[3], r[4],
                    r[5], r[6], r[7], r[8], r[9],
                    r[10], r[11], r[12], r[13], r[14]
            ));
        }
    }

    public List<Prescription> getAllPrescriptions() {
        return new ArrayList<>(prescriptions);
    }

    public void reload() {
        loadFromCSV();
    }

    public void addPrescription(Prescription p) {
        prescriptions.add(p);
        saveAll();
        exportToOutput();
    }

    public void updatePrescription(Prescription updated) {
        for (int i = 0; i < prescriptions.size(); i++) {
            if (prescriptions.get(i).getPrescriptionId().equals(updated.getPrescriptionId())) {
                prescriptions.set(i, updated);
                break;
            }
        }
        saveAll();
        exportToOutput();
    }

    public void deletePrescription(String prescriptionId) {
        prescriptions.removeIf(p -> p.getPrescriptionId().equals(prescriptionId));
        saveAll();
        exportToOutput();
    }

    private void saveAll() {
        List<String> lines = new ArrayList<>();
        lines.add(String.join(",", new String[]{
                "prescription_id","patient_id","clinician_id","appointment_id","prescription_date",
                "medication_name","dosage","frequency","duration_days","quantity","instructions",
                "pharmacy_name","status","issue_date","collection_date"
        }));

        for (Prescription p : prescriptions) {
            lines.add(String.join(",", new String[]{
                    p.getPrescriptionId(), p.getPatientId(), p.getClinicianId(),
                    p.getAppointmentId(), p.getPrescriptionDate(),
                    p.getMedicationName(), p.getDosage(), p.getFrequency(),
                    p.getDurationDays(), p.getQuantity(), p.getInstructions(),
                    p.getPharmacyName(), p.getStatus(), p.getIssueDate(), p.getCollectionDate()
            }));
        }

        FileWriterUtil.writeFile(csvPath, lines);
    }

    public void exportToOutput() {
        List<String> lines = new ArrayList<>();
        if (prescriptions.isEmpty()) {
            lines.add("No prescriptions available.");
        } else {
            for (Prescription p : prescriptions) {
                lines.add(
                        p.getPrescriptionId() + "," +
                                p.getPatientId() + "," +
                                p.getClinicianId() + "," +
                                p.getAppointmentId() + "," +
                                p.getPrescriptionDate() + "," +
                                p.getMedicationName() + "," +
                                p.getDosage() + "," +
                                p.getFrequency() + "," +
                                p.getDurationDays() + "," +
                                p.getQuantity() + "," +
                                p.getInstructions() + "," +
                                p.getPharmacyName() + "," +
                                p.getStatus() + "," +
                                p.getIssueDate() + "," +
                                p.getCollectionDate()
                );
            }
        }
        FileWriterUtil.writeFile(outputPath, lines);
    }

    public String generateNewId() {
        int max = 0;
        for (Prescription p : prescriptions) {
            try {
                int n = Integer.parseInt(p.getPrescriptionId().replaceAll("\\D", ""));
                if (n > max) max = n;
            } catch (Exception ignored) {}
        }
        return String.format("RX%03d", max + 1);
    }

    public List<Prescription> queryByPatient(String patientId) {
        List<Prescription> result = new ArrayList<>();
        for (Prescription p : prescriptions) {
            if (p.getPatientId().equals(patientId)) result.add(p);
        }
        return result;
    }

    public List<Prescription> queryByClinician(String clinicianId) {
        List<Prescription> result = new ArrayList<>();
        for (Prescription p : prescriptions) {
            if (p.getClinicianId().equals(clinicianId)) result.add(p);
        }
        return result;
    }

    public List<Prescription> queryByAppointment(String appointmentId) {
        List<Prescription> result = new ArrayList<>();
        for (Prescription p : prescriptions) {
            if (p.getAppointmentId().equals(appointmentId)) result.add(p);
        }
        return result;
    }
}
