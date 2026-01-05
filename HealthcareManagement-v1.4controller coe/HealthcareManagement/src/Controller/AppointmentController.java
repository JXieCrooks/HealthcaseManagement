package Controller;

import Model.Appointment;
import Utils.CSVReader;
import Utils.FileWriterUtil;

import java.util.ArrayList;
import java.util.List;

public class AppointmentController {

    private final String filePath = "data/appointments.csv";
    private List<Appointment> appointments;

    public AppointmentController() {
        appointments = new ArrayList<>();
        loadAppointmentsFromCSV();
    }
    
    public void loadAppointmentsFromCSV() {
        appointments.clear();
        List<String[]> rows = CSVReader.readCSV(filePath);
        for (String[] r : rows) {
            if (r.length < 12 || r[0].equals("appointment_id")) continue;
            Appointment appt = new Appointment(
                    r[0], r[1], r[2], r[3], r[4], r[5], r[6],
                    r[7], r[8], r[9], r[10],
                    r[11],
                    r.length > 12 ? r[12] : r[11]
            );
            appointments.add(appt);
        }
    }

    public List<Appointment> getAllAppointments() {
        return appointments;
    }

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        List<Appointment> list = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getPatientId().equals(patientId)) {
                list.add(a);
            }
        }
        return list;
    }

    public void addAppointment(Appointment appt) {
        appointments.add(appt);
        saveAppointmentsToCSV();
    }

    public void cancelAppointment(String appointmentId) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equals(appointmentId)) {
                a.setStatus("Cancelled");
                a.setLastModified(java.time.LocalDateTime.now().toString());
                break;
            }
        }
        saveAppointmentsToCSV();
    }
    public Appointment getAppointmentById(String id) {
        for (Appointment a : getAllAppointments()) {
            if (a.getAppointmentId().equals(id)) return a;
        }
        return null;
    }
    private void saveAppointmentsToCSV() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{
                "appointment_id", "patient_id", "clinician_id", "facility_id",
                "appointment_date", "appointment_time", "duration_minutes",
                "appointment_type", "status", "reason_for_visit", "notes",
                "created_date", "last_modified"
        });

        for (Appointment a : appointments) {
            rows.add(new String[]{
                    a.getAppointmentId(),
                    a.getPatientId(),
                    a.getClinicianId(),
                    a.getFacilityId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getDurationMinutes(),
                    a.getAppointmentType(),
                    a.getStatus(),
                    a.getReasonForVisit(),
                    a.getNotes(),
                    a.getCreatedDate(),
                    a.getLastModified()
            });
        }

        List<String> lines = new ArrayList<>();
        for (String[] row : rows) {
            lines.add(String.join(",", row));
        }

        FileWriterUtil.writeFile(filePath, lines);
    }

    public String generateNewId() {
        int max = 0;
        for (Appointment a : appointments) {
            String numStr = a.getAppointmentId().replaceAll("\\D", "");
            if (!numStr.isEmpty()) {
                int num = Integer.parseInt(numStr);
                if (num > max) max = num;
            }
        }
        return String.format("A%03d", max + 1);
    }
}
