package View.StaffView;

import Controller.*;
import Model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PrescriptionManageView extends JPanel {

    private JTable table;
    private JButton addBtn, deleteBtn, updateBtn, refreshBtn, prevBtn, nextBtn, searchBtn;
    private JTextField searchField;
    private PrescriptionController prescriptionController;
    private ClinicianController clinicianController;
    private AppointmentController appointmentController;

    private List<Prescription> fullList;
    private List<Prescription> currentList;
    private int currentPage = 1;
    private final int pageSize = 10;

    public PrescriptionManageView(User user, PrescriptionController prescriptionController) {
        this.prescriptionController = prescriptionController;
        this.clinicianController = new ClinicianController();
        this.appointmentController = new AppointmentController();

        setLayout(new BorderLayout());
        initUI();
        loadTable();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchBtn = new JButton("Search");
        topPanel.add(new JLabel("Search by Patient ID or Clinician ID:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prevBtn = new JButton("Previous Page");
        nextBtn = new JButton("Next Page");
        addBtn = new JButton("Add");
        deleteBtn = new JButton("Delete");
        updateBtn = new JButton("Update");
        refreshBtn = new JButton("Refresh");
        bottomPanel.add(prevBtn);
        bottomPanel.add(nextBtn);
        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(updateBtn);
        bottomPanel.add(refreshBtn);


        add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        updateBtn.addActionListener(e -> showUpdateDialog());
        refreshBtn.addActionListener(e -> loadTable());
        prevBtn.addActionListener(e -> prevPage());
        nextBtn.addActionListener(e -> nextPage());
        searchBtn.addActionListener(e -> search());
    }

    private void loadTable() {
        fullList = prescriptionController.getAllPrescriptions();
        currentPage = 1;
        refreshTable();
    }

    private void refreshTable() {
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, fullList.size());
        currentList = fullList.subList(start, end);

        String[] columns = {
                "ID","Patient ID","Clinician ID","Appointment ID","Date",
                "Medication","Dosage","Frequency","Duration","Quantity",
                "Instructions","Pharmacy","Status","Issue Date","Collection Date"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Prescription p : currentList) {
            model.addRow(new Object[]{
                    p.getPrescriptionId(),
                    p.getPatientId(),
                    p.getClinicianId(),
                    p.getAppointmentId(),
                    p.getPrescriptionDate(),
                    p.getMedicationName(),
                    p.getDosage(),
                    p.getFrequency(),
                    p.getDurationDays(),
                    p.getQuantity(),
                    p.getInstructions(),
                    p.getPharmacyName(),
                    p.getStatus(),
                    p.getIssueDate(),
                    p.getCollectionDate()
            });
        }

        table.setModel(model);


        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {80,120,120,130,100,160,100,120,90,90,250,150,100,120,140};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        int totalPages = (int) Math.ceil((double) fullList.size() / pageSize);
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < totalPages);
    }


    private void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            refreshTable();
        }
    }

    private void nextPage() {
        int totalPages = (int) Math.ceil((double) fullList.size() / pageSize);
        if (currentPage < totalPages) {
            currentPage++;
            refreshTable();
        }
    }

    private void search() {
        String keyword = searchField.getText().trim().toLowerCase();
        fullList = prescriptionController.getAllPrescriptions().stream()
                .filter(p -> p.getPatientId().toLowerCase().contains(keyword)
                        || p.getClinicianId().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        currentPage = 1;
       refreshTable();
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Prescription", true);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        PatientController patientController = new PatientController();
        JComboBox<String> patientBox = new JComboBox<>();
        for (Patient p : patientController.getAllPatients())
            patientBox.addItem(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName());

        JComboBox<String> clinicianBox = new JComboBox<>();
        for (Clinician c : clinicianController.getAllClinicians())
            clinicianBox.addItem(c.getClinicianId() + " - " + c.getFirstName() + " " + c.getLastName());

        JComboBox<String> appointmentBox = new JComboBox<>();
        for (Appointment a : appointmentController.getAllAppointments())
            appointmentBox.addItem(a.getAppointmentId() + " - " + a.getAppointmentDate() + " " + a.getAppointmentTime());

        JTextField dateField = new JTextField();
        JTextField medField = new JTextField();
        JTextField dosageField = new JTextField();
        JTextField freqField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField instructionsField = new JTextField();
        JTextField pharmacyField = new JTextField();
        JTextField statusField = new JTextField();
        JTextField issueField = new JTextField();
        JTextField collectionField = new JTextField();

        panel.add(new JLabel("Patient:")); panel.add(patientBox);
        panel.add(new JLabel("Clinician:")); panel.add(clinicianBox);
        panel.add(new JLabel("Appointment:")); panel.add(appointmentBox);
        panel.add(new JLabel("Prescription Date:")); panel.add(dateField);
        panel.add(new JLabel("Medication Name:")); panel.add(medField);
        panel.add(new JLabel("Dosage:")); panel.add(dosageField);
        panel.add(new JLabel("Frequency:")); panel.add(freqField);
        panel.add(new JLabel("Duration (days):")); panel.add(durationField);
        panel.add(new JLabel("Quantity:")); panel.add(quantityField);
        panel.add(new JLabel("Instructions:")); panel.add(instructionsField);
        panel.add(new JLabel("Pharmacy Name:")); panel.add(pharmacyField);
        panel.add(new JLabel("Status:")); panel.add(statusField);
        panel.add(new JLabel("Issue Date:")); panel.add(issueField);
        panel.add(new JLabel("Collection Date:")); panel.add(collectionField);

        JButton okBtn = new JButton("Add");
        okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));
        panel.add(okBtn);


        okBtn.addActionListener(e -> {
            Prescription p = new Prescription(
                    prescriptionController.generateNewId(),
                    patientBox.getSelectedItem().toString().split(" - ")[0],
                    clinicianBox.getSelectedItem().toString().split(" - ")[0],
                    appointmentBox.getSelectedItem().toString().split(" - ")[0],
                    dateField.getText().trim(),
                    medField.getText().trim(),
                    dosageField.getText().trim(),
                    freqField.getText().trim(),
                    durationField.getText().trim(),
                    quantityField.getText().trim(),
                    instructionsField.getText().trim(),
                    pharmacyField.getText().trim(),
                    statusField.getText().trim(),
                    issueField.getText().trim(),
                    collectionField.getText().trim()
            );
            prescriptionController.addPrescription(p);
            dialog.dispose();
            loadTable();
        });
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);

    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = currentList.get(row).getPrescriptionId();
        prescriptionController.deletePrescription(id);
        loadTable();
    }

    private void showUpdateDialog() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Prescription selected = currentList.get(row);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Prescription", true);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        PatientController patientController = new PatientController();
        JComboBox<String> patientBox = new JComboBox<>();
        for (Patient p : patientController.getAllPatients())
            patientBox.addItem(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName());
        for (int i = 0; i < patientBox.getItemCount(); i++) {
            if (patientBox.getItemAt(i).startsWith(selected.getPatientId())) {
                patientBox.setSelectedIndex(i);
                break;
            }
        }

        JComboBox<String> clinicianBox = new JComboBox<>();
        for (Clinician c : clinicianController.getAllClinicians())
            clinicianBox.addItem(c.getClinicianId() + " - " + c.getFirstName() + " " + c.getLastName());
        for (int i = 0; i < clinicianBox.getItemCount(); i++) {
            if (clinicianBox.getItemAt(i).startsWith(selected.getClinicianId())) {
                clinicianBox.setSelectedIndex(i);
                break;
            }
        }

        JComboBox<String> appointmentBox = new JComboBox<>();
        for (Appointment a : appointmentController.getAllAppointments())
            appointmentBox.addItem(a.getAppointmentId() + " - " + a.getAppointmentDate() + " " + a.getAppointmentTime());
        for (int i = 0; i < appointmentBox.getItemCount(); i++) {
            if (appointmentBox.getItemAt(i).startsWith(selected.getAppointmentId())) {
                appointmentBox.setSelectedIndex(i);
                break;
            }
        }

        JTextField dateField = new JTextField(selected.getPrescriptionDate());
        JTextField medField = new JTextField(selected.getMedicationName());
        JTextField dosageField = new JTextField(selected.getDosage());
        JTextField freqField = new JTextField(selected.getFrequency());
        JTextField durationField = new JTextField(selected.getDurationDays());
        JTextField quantityField = new JTextField(selected.getQuantity());
        JTextField instructionsField = new JTextField(selected.getInstructions());
        JTextField pharmacyField = new JTextField(selected.getPharmacyName());
        JTextField statusField = new JTextField(selected.getStatus());
        JTextField issueField = new JTextField(selected.getIssueDate());
        JTextField collectionField = new JTextField(selected.getCollectionDate());

        panel.add(new JLabel("Patient:")); panel.add(patientBox);
        panel.add(new JLabel("Clinician:")); panel.add(clinicianBox);
        panel.add(new JLabel("Appointment:")); panel.add(appointmentBox);
        panel.add(new JLabel("Prescription Date:")); panel.add(dateField);
        panel.add(new JLabel("Medication Name:")); panel.add(medField);
        panel.add(new JLabel("Dosage:")); panel.add(dosageField);
        panel.add(new JLabel("Frequency:")); panel.add(freqField);
        panel.add(new JLabel("Duration (days):")); panel.add(durationField);
        panel.add(new JLabel("Quantity:")); panel.add(quantityField);
        panel.add(new JLabel("Instructions:")); panel.add(instructionsField);
        panel.add(new JLabel("Pharmacy Name:")); panel.add(pharmacyField);
        panel.add(new JLabel("Status:")); panel.add(statusField);
        panel.add(new JLabel("Issue Date:")); panel.add(issueField);
        panel.add(new JLabel("Collection Date:")); panel.add(collectionField);

        JButton okBtn = new JButton("Update");
        okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));
        panel.add(okBtn);



        okBtn.addActionListener(e -> {
            Prescription updated = new Prescription(
                    selected.getPrescriptionId(),
                    patientBox.getSelectedItem().toString().split(" - ")[0],
                    clinicianBox.getSelectedItem().toString().split(" - ")[0],
                    appointmentBox.getSelectedItem().toString().split(" - ")[0],
                    dateField.getText().trim(),
                    medField.getText().trim(),
                    dosageField.getText().trim(),
                    freqField.getText().trim(),
                    durationField.getText().trim(),
                    quantityField.getText().trim(),
                    instructionsField.getText().trim(),
                    pharmacyField.getText().trim(),
                    statusField.getText().trim(),
                    issueField.getText().trim(),
                    collectionField.getText().trim()
            );
            prescriptionController.updatePrescription(updated);
            dialog.dispose();
            loadTable();
        });
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

}
