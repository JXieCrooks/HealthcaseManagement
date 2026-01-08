package View.StaffView;


import Controller.AppointmentController;
import Model.Appointment;
import Model.User;
import View.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppointmentManageView extends JPanel {

    private AppointmentController appointmentController;
    private JTable table;
    private JTextField searchField;
    private JButton searchButton, prevButton, nextButton, exitButton;

    private int currentPage = 1;
    private final int ROWS_PER_PAGE = 10;
    private List<Appointment> allAppointments;
    private User currentUser;

    public AppointmentManageView(User user,AppointmentController controller) {
        this.appointmentController = controller;
        this.currentUser = user;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search by Patient ID or Clinician ID:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("Previous Page");
        nextButton = new JButton("Next Page");
        exitButton = new JButton("Exit");
        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(exitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadAppointments();

        prevButton.addActionListener(e -> previousPage());
        nextButton.addActionListener(e -> nextPage());
        exitButton.addActionListener(e -> goBackToLogin());

        searchButton.addActionListener(e -> searchAppointments());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int index = (currentPage - 1) * ROWS_PER_PAGE + row;
                    showDetail(allAppointments.get(index));
                }
            }
        });
    }

    private void loadAppointments() {
        allAppointments = appointmentController.getAllAppointments();
        currentPage = 1;
        refreshTable();
    }
    private void refreshTable() {
        int start = (currentPage - 1) * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, allAppointments.size());

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
                "Notes"
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
                    a.getNotes()
            });
        }

        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {120,120,120,120,100,80,120,120,100,200,300};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            refreshTable();
        }
    }

    private void nextPage() {
        int totalPages = (int) Math.ceil(allAppointments.size() / (double) ROWS_PER_PAGE);
        if (currentPage < totalPages) {
            currentPage++;
            refreshTable();
        }
    }

    private void goBackToLogin() {
        SwingUtilities.getWindowAncestor(this).dispose();
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }

    private void searchAppointments() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadAppointments();
            return;
        }
        allAppointments = appointmentController.getAllAppointments().stream()
                .filter(a -> a.getPatientId().toLowerCase().contains(keyword) ||
                        a.getClinicianId().toLowerCase().contains(keyword))
                .toList();
        currentPage = 1;
        refreshTable();
    }

    private void showDetail(Appointment a) {
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
                .append("Notes: ").append(a.getNotes()).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString(), "Appointment Detail", JOptionPane.INFORMATION_MESSAGE);
    }
}
