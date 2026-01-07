package Controller;

import Model.Patient;
import Utils.CSVReader;
import Utils.FileWriterUtil;

import java.util.ArrayList;
import java.util.List;

public class PatientController {

    private final String filePath = "data/patients.csv";
    private List<Patient> patients;

    public PatientController() {
        patients = new ArrayList<>();
        loadPatientsFromCSV();
    }

    public void loadPatientsFromCSV() {
        patients.clear();
        List<String[]> rows = CSVReader.readCSV(filePath);

        for (String[] r : rows) {
            if (r.length < 14 || r[0].equals("patientId")) continue;
            Patient p = new Patient(
                    r[0],  // patientId
                    r[1],  // firstName
                    r[2],  // lastName
                    r[3],  // dateOfBirth
                    r[4],  // nhsNumber
                    r[5],  // gender
                    r[6],  // phoneNumber
                    r[7],  // email
                    r[8],  // address
                    r[9],  // postcode
                    r[10], // emergencyContactName
                    r[11], // emergencyContactPhone
                    r[12], // registrationDate
                    r[13]  // gpSurgeryId
            );
            patients.add(p);
        }
    }

    public List<Patient> getAllPatients() {
        return patients;
    }

    public List<Patient> searchPatients(String idKeyword, String nameKeyword) {
        idKeyword = idKeyword.toLowerCase();
        nameKeyword = nameKeyword.toLowerCase();

        List<Patient> result = new ArrayList<>();
        for (Patient p : patients) {
            String fullName = (p.getFirstName() + " " + p.getLastName()).toLowerCase();
            if (p.getPatientId().toLowerCase().contains(idKeyword) && fullName.contains(nameKeyword)) {
                result.add(p);
            }
        }
        return result;
    }

    public void addPatient(Patient p) {
        patients.add(p);
        savePatientsToCSV();
    }

    public void updatePatient(Patient updated) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getPatientId().equals(updated.getPatientId())) {
                patients.set(i, updated);
                break;
            }
        }
        savePatientsToCSV();
    }

    public void deletePatient(String patientId) {
        patients.removeIf(p -> p.getPatientId().equals(patientId));
        savePatientsToCSV();
    }

    private void savePatientsToCSV() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{
                "patientId", "firstName", "lastName", "dateOfBirth", "nhsNumber",
                "gender", "phoneNumber", "email", "address", "postcode",
                "emergencyContactName", "emergencyContactPhone", "registrationDate", "gpSurgeryId"
        });

        for (Patient p : patients) {
            rows.add(new String[]{
                    p.getPatientId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getDateOfBirth(),
                    p.getNhsNumber(),
                    p.getGender(),
                    p.getPhoneNumber(),
                    p.getEmail(),
                    p.getAddress(),
                    p.getPostcode(),
                    p.getEmergencyContactName(),
                    p.getEmergencyContactPhone(),
                    p.getRegistrationDate(),
                    p.getGpSurgeryId()
            });
        }

        List<String> lines = new ArrayList<>();
        for (String[] row : rows) {
            lines.add(String.join(",", row));
        }

        FileWriterUtil.writeFile(filePath, lines);
    }
    public Patient getPatientById(String id) {
        for (Patient p : getAllPatients()) {
            if (p.getPatientId().equals(id)) return p;
        }
        return null;
    }

    public String generateNewId() {
        int max = 0;
        for (Patient p : patients) {
            String numStr = p.getPatientId().replaceAll("\\D", "");
            if (!numStr.isEmpty()) {
                int num = Integer.parseInt(numStr);
                if (num > max) max = num;
            }
        }
        return String.format("P%03d", max + 1);
    }
}
