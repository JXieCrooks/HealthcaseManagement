package View.PatientView;

import Controller.AppointmentController;
import Controller.ClinicianController;
import Model.Appointment;
import Model.Clinician;
import Model.User;
import View.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FacilitiesDetailView extends JFrame {

    private User currentUser;
    private String facilityId;

    private JTable doctorTable;
    private JButton bookAppointmentButton, exitButton;

    private ClinicianController clinicianController;
    private AppointmentController appointmentController;

    public FacilitiesDetailView(User user, String facilityId) {
        this.currentUser = user;
        this.facilityId = facilityId;

        setTitle("Hospital Details - Doctors");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        clinicianController = new ClinicianController();
        appointmentController = new AppointmentController();

        initUI();
        loadDoctors();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        doctorTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        bookAppointmentButton = new JButton("Book Appointment");
        exitButton = new JButton("Exit");

        buttonPanel.add(bookAppointmentButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        bookAppointmentButton.addActionListener(e -> showBookingDialog());
        exitButton.addActionListener(e -> goBackToLogin());
    }

    private void loadDoctors() {
        List<Clinician> clinicians = clinicianController.getCliniciansByFacility(facilityId);

        String[] columns = {"Clinician ID", "First Name", "Last Name", "Title", "Speciality",
                "GMC Number", "Phone Number", "Email", "Workplace ID", "Workplace Type",
                "Employment Status", "Start Date"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Clinician c : clinicians) {
            model.addRow(new Object[]{
                    c.getClinicianId(),
                    c.getFirstName(),
                    c.getLastName(),
                    c.getTitle(),
                    c.getSpeciality(),
                    c.getGmcNumber(),
                    c.getPhoneNumber(),
                    c.getEmail(),
                    c.getWorkplaceId(),
                    c.getWorkplaceType(),
                    c.getEmploymentStatus(),
                    c.getStartDate()
            });
        }

        doctorTable.setModel(model);

        doctorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] columnWidths = {100, 100, 100, 80, 120, 100, 120, 180, 100, 100, 120, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            doctorTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    private void showBookingDialog() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor.");
            return;
        }

        String doctorId = doctorTable.getValueAt(selectedRow, 0).toString();

        JTextField dateField = new JTextField(10);
        JTextField timeField = new JTextField(5);
        JTextField durationField = new JTextField(5);
        JTextField typeField = new JTextField(15);
        JTextField reasonField = new JTextField(20);
        JTextField notesField = new JTextField(20);

        durationField.setText("30");
        typeField.setText("Checkup");

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("Appointment Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Appointment Time (HH:mm):"));
        panel.add(timeField);
        panel.add(new JLabel("Duration (minutes):"));
        panel.add(durationField);
        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Reason for visit:"));
        panel.add(reasonField);
        panel.add(new JLabel("Notes:"));
        panel.add(notesField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Book Appointment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String duration = durationField.getText().trim();
        String type = typeField.getText().trim();
        String reason = reasonField.getText().trim();
        String notes = notesField.getText().trim();

        if (date.isEmpty() || time.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date and time cannot be empty.");
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String createdDate = LocalDate.now().format(dateFormatter);
        String lastModified = createdDate;

        Appointment appt = new Appointment(
                appointmentController.generateNewId(),
                currentUser.getReferenceId(),
                doctorId,
                facilityId,
                date,
                time,
                duration.isEmpty() ? "30" : duration,
                type.isEmpty() ? "Checkup" : type,
                "Scheduled",
                reason,
                notes,
                createdDate,
                lastModified
        );

        appointmentController.addAppointment(appt);
        JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
    }

    private void goBackToLogin() {
        this.dispose();
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }

    public void showGUI() {
        setVisible(true);
    }
}
