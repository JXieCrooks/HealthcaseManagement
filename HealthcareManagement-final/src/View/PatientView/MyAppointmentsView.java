
package View.PatientView;

import Controller.AppointmentController;
import Model.Appointment;
import Model.User;
import View.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyAppointmentsView extends JPanel {

    private User currentUser;
    private JTable appointmentsTable;
    private JButton prevButton, nextButton, detailsButton, cancelButton, exitButton;

    private AppointmentController appointmentController;

    private int page = 1;
    private final int PAGE_SIZE = 5;
    private List<Appointment> allAppointments;

    public MyAppointmentsView(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        appointmentController = new AppointmentController();
        appointmentsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        detailsButton = new JButton("Details");
        cancelButton = new JButton("Cancel");
        exitButton = new JButton("Exit");

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(detailsButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadAppointments();

        prevButton.addActionListener(e -> {
            if (page > 1) page--;
            refreshTable();
        });

        nextButton.addActionListener(e -> {
            int maxPage = (int) Math.ceil(allAppointments.size() / (double) PAGE_SIZE);
            if (page < maxPage) page++;
            refreshTable();
        });

        detailsButton.addActionListener(e -> showDetails());
        cancelButton.addActionListener(e -> cancelSelected());
        exitButton.addActionListener(e -> goBackToLogin());
    }

    private void loadAppointments() {
        String patientId = currentUser.getReferenceId();
        allAppointments = appointmentController.getAppointmentsByPatient(patientId);
        page = 1;
        refreshTable();
    }

    private void refreshTable() {
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allAppointments.size());

        String[] columns = {
                "Appointment ID",
                "Patient ID",
                "Clinician ID",
                "Facility ID",
                "Date",
                "Time",
                "Duration (min)",
                "Type",
                "Status",
                "Reason",
                "Notes",
                "Created",
                "Last Modified"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = start; i < end; i++) {
            Appointment a = allAppointments.get(i);
            model.addRow(new Object[]{
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

        appointmentsTable.setModel(model);
        appointmentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] columnWidths = {120, 100, 120, 120, 100, 80, 100, 100, 80, 150, 150, 100, 120};
        for (int i = 0; i < columnWidths.length; i++) {
            appointmentsTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }


    private void showDetails() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.");
            return;
        }
        int index = (page - 1) * PAGE_SIZE + selectedRow;
        Appointment a = allAppointments.get(index);

        StringBuilder sb = new StringBuilder();
        sb.append("Appointment ID: ").append(a.getAppointmentId()).append("\n")
                .append("Patient ID: ").append(a.getPatientId()).append("\n")
                .append("Clinician ID: ").append(a.getClinicianId()).append("\n")
                .append("Facility ID: ").append(a.getFacilityId()).append("\n")
                .append("Date: ").append(a.getAppointmentDate()).append("\n")
                .append("Time: ").append(a.getAppointmentTime()).append("\n")
                .append("Duration: ").append(a.getDurationMinutes()).append("\n")
                .append("Type: ").append(a.getAppointmentType()).append("\n")
                .append("Status: ").append(a.getStatus()).append("\n")
                .append("Reason: ").append(a.getReasonForVisit()).append("\n")
                .append("Notes: ").append(a.getNotes()).append("\n")
                .append("Created: ").append(a.getCreatedDate()).append("\n")
                .append("Last Modified: ").append(a.getLastModified()).append("\n");

        JOptionPane.showMessageDialog(this, sb.toString(), "Appointment Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelSelected() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.");
            return;
        }
        int index = (page - 1) * PAGE_SIZE + selectedRow;
        Appointment a = allAppointments.get(index);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this appointment?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            appointmentController.cancelAppointment(a.getAppointmentId());
            JOptionPane.showMessageDialog(this, "Appointment cancelled.");
            loadAppointments();
        }
    }

    private void goBackToLogin() {
        SwingUtilities.getWindowAncestor(this).dispose();
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }
}
