//package View.PatientView;
//
//import Controller.AppointmentController;
//import Model.Appointment;
//import Model.User;
//import View.LoginView;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.List;
//
//public class MyAppointmentsView extends JPanel {
//
//    private static final int PAGE_SIZE = 10;
//
//    private User currentUser;
//    private JTable appointmentsTable;
//    private AppointmentController appointmentController;
//
//    private JButton prevButton, nextButton, exitButton;
//    private int currentPage = 1;
//    private int totalPages = 1;
//    private List<Appointment> patientAppointments;
//
//    public MyAppointmentsView(User user) {
//        this.currentUser = user;
//        setLayout(new BorderLayout());
//
//        appointmentController = new AppointmentController();
//        patientAppointments = appointmentController.getAppointmentsByPatient(currentUser.getReferenceId());
//
//        totalPages = (int) Math.ceil((double) patientAppointments.size() / PAGE_SIZE);
//
//        // 表格
//        appointmentsTable = new JTable();
//        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
//        add(scrollPane, BorderLayout.CENTER);
//
//        // 分页与操作按钮面板
//        JPanel bottomPanel = new JPanel();
//        prevButton = new JButton("Previous");
//        nextButton = new JButton("Next");
//        exitButton = new JButton("Exit");
//
//        bottomPanel.add(prevButton);
//        bottomPanel.add(nextButton);
//        bottomPanel.add(exitButton);
//
//        add(bottomPanel, BorderLayout.SOUTH);
//
//        // 按钮事件
//        prevButton.addActionListener(e -> goToPreviousPage());
//        nextButton.addActionListener(e -> goToNextPage());
//        exitButton.addActionListener(e -> exitPanel());
//
//        // 鼠标右键操作菜单
//        JPopupMenu popupMenu = new JPopupMenu();
//        JMenuItem cancelItem = new JMenuItem("Cancel Appointment");
//        JMenuItem detailsItem = new JMenuItem("View Details");
//
//        popupMenu.add(cancelItem);
//        popupMenu.add(detailsItem);
//
//        appointmentsTable.setComponentPopupMenu(popupMenu);
//
//        cancelItem.addActionListener(e -> cancelSelectedAppointment());
//        detailsItem.addActionListener(e -> viewAppointmentDetails());
//
//        loadPage(currentPage);
//    }
//
//    private void loadPage(int page) {
//        int start = (page - 1) * PAGE_SIZE;
//        int end = Math.min(start + PAGE_SIZE, patientAppointments.size());
//
//        String[] columns = {"Appointment ID", "Doctor ID", "Facility ID", "Date", "Time", "Status"};
//        DefaultTableModel model = new DefaultTableModel(columns, 0);
//
//        for (int i = start; i < end; i++) {
//            Appointment a = patientAppointments.get(i);
//            model.addRow(new Object[]{
//                    a.getAppointmentId(),
//                    a.getClinicianId(),
//                    a.getFacilityId(),
//                    a.getAppointmentDate(),
//                    a.getAppointmentTime(),
//                    a.getStatus()
//            });
//        }
//
//        appointmentsTable.setModel(model);
//    }
//
//    private void goToPreviousPage() {
//        if (currentPage > 1) {
//            currentPage--;
//            loadPage(currentPage);
//        }
//    }
//
//    private void goToNextPage() {
//        if (currentPage < totalPages) {
//            currentPage++;
//            loadPage(currentPage);
//        }
//    }
//
//    private void cancelSelectedAppointment() {
//        int selectedRow = appointmentsTable.getSelectedRow();
//        if (selectedRow < 0) {
//            JOptionPane.showMessageDialog(this, "Please select an appointment.");
//            return;
//        }
//        String appointmentId = appointmentsTable.getValueAt(selectedRow, 0).toString();
//        boolean success = appointmentController.cancelAppointment(appointmentId);
//        if (success) {
//            JOptionPane.showMessageDialog(this, "Appointment cancelled.");
//            refreshAppointments();
//        } else {
//            JOptionPane.showMessageDialog(this, "Failed to cancel appointment.");
//        }
//    }
//
//    private void viewAppointmentDetails() {
//        int selectedRow = appointmentsTable.getSelectedRow();
//        if (selectedRow < 0) {
//            JOptionPane.showMessageDialog(this, "Please select an appointment.");
//            return;
//        }
//        String appointmentId = appointmentsTable.getValueAt(selectedRow, 0).toString();
//        Appointment appt = null;
//        for (Appointment a : patientAppointments) {
//            if (a.getAppointmentId().equals(appointmentId)) {
//                appt = a;
//                break;
//            }
//        }
//        if (appt != null) {
//            String message = String.format(
//                    "Appointment ID: %s\nPatient ID: %s\nDoctor ID: %s\nFacility ID: %s\nDate: %s\nTime: %s\nDuration: %s\nType: %s\nStatus: %s\nReason: %s\nNotes: %s\nCreated: %s\nLast Modified: %s",
//                    appt.getAppointmentId(), appt.getPatientId(), appt.getClinicianId(), appt.getFacilityId(),
//                    appt.getAppointmentDate(), appt.getAppointmentTime(), appt.getDurationMinutes(),
//                    appt.getAppointmentType(), appt.getStatus(), appt.getReasonForVisit(), appt.getNotes(),
//                    appt.getCreatedDate(), appt.getLastModified()
//            );
//            JOptionPane.showMessageDialog(this, message, "Appointment Details", JOptionPane.INFORMATION_MESSAGE);
//        }
//    }
//
//    private void refreshAppointments() {
//        patientAppointments = appointmentController.getAppointmentsByPatient(currentUser.getReferenceId());
//        totalPages = (int) Math.ceil((double) patientAppointments.size() / PAGE_SIZE);
//        if (currentPage > totalPages) currentPage = totalPages;
//        if (currentPage == 0) currentPage = 1;
//        loadPage(currentPage);
//    }
//
//    private void exitPanel() {
//        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
//        topFrame.dispose();
//        // 回到登录页
//        LoginView loginView = new LoginView();
//        loginView.setVisible(true);
//    }
//}
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

        String[] columns = {"Appointment ID", "Doctor ID", "Facility ID", "Date", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = start; i < end; i++) {
            Appointment a = allAppointments.get(i);
            model.addRow(new Object[]{
                    a.getAppointmentId(),
                    a.getClinicianId(),
                    a.getFacilityId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getStatus()
            });
        }

        appointmentsTable.setModel(model);
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
