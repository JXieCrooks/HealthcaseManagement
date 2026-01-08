package View.StaffView;

import Controller.PatientController;
import Model.Patient;
import Model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PatientManageView extends JPanel {

    private PatientController patientController;
    private JTable patientTable;
    private JButton prevButton, nextButton, addButton, editButton, deleteButton, exitButton, searchButton, refreshButton;
    private JTextField searchField;

    private List<Patient> allPatients;
    private List<Patient> filteredPatients;
    private int page = 1;
    private final int PAGE_SIZE = 10;

    public PatientManageView(User user, PatientController controller) {

        this.patientController = controller;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search by Name or NHS: "));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        patientTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(patientTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        prevButton = new JButton("Previous Page");
        nextButton = new JButton("Next Page");
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        exitButton = new JButton("Exit");

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadPatients();

        prevButton.addActionListener(e -> previousPage());
        nextButton.addActionListener(e -> nextPage());
        addButton.addActionListener(e -> addPatient());
        editButton.addActionListener(e -> editPatient());
        deleteButton.addActionListener(e -> deletePatient());
        refreshButton.addActionListener(e -> refreshPatients());
        exitButton.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());
        searchButton.addActionListener(e -> searchPatients());
    }

    private void loadPatients() {
        allPatients = patientController.getAllPatients();
        filteredPatients = allPatients;
        page = 1;
        refreshTable();
    }

    private void refreshPatients() {
        patientController.loadPatientsFromCSV();
        loadPatients();
    }

    private void refreshTable() {
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filteredPatients.size());

        String[] columns = {
                "Patient ID", "First Name", "Last Name", "DOB",
                "NHS Number", "Gender", "Phone", "Email",
                "Address", "Postcode",
                "Emergency Contact", "Emergency Phone",
                "Registration Date", "GP Surgery ID"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = start; i < end; i++) {
            Patient p = filteredPatients.get(i);
            model.addRow(new Object[]{
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
        patientTable.setModel(model);
        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    }

    private void previousPage() {
        if (page > 1) {
            page--;
            refreshTable();
        }
    }

    private void nextPage() {
        int maxPage = (int) Math.ceil((double) filteredPatients.size() / PAGE_SIZE);
        if (page < maxPage) {
            page++;
            refreshTable();
        }
    }

    private void addPatient() {
        JPanel panel = createPatientForm(null);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Patient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Patient p = readPatientFromForm(panel);
            patientController.addPatient(p);
            loadPatients();
        }
    }

    private void editPatient() {
        int row = patientTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient to edit.");
            return;
        }
        int index = (page - 1) * PAGE_SIZE + row;
        Patient p = filteredPatients.get(index);

        JPanel panel = createPatientForm(p);
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Patient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Patient updated = readPatientFromForm(panel);
            patientController.updatePatient(updated);
            loadPatients();
        }
    }

    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.");
            return;
        }
        int index = (page - 1) * PAGE_SIZE + row;
        Patient p = filteredPatients.get(index);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this patient?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            patientController.deletePatient(p.getPatientId());
            loadPatients();
        }
    }

    private void searchPatients() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            filteredPatients = allPatients;
        } else {
            filteredPatients = allPatients.stream()
                    .filter(p -> p.getFirstName().toLowerCase().contains(keyword)
                            || p.getLastName().toLowerCase().contains(keyword)
                            || p.getNhsNumber().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }
        page = 1;
        refreshTable();
    }

    private JPanel createPatientForm(Patient p) {
        JPanel panel = new JPanel(new GridLayout(14, 2));
        JTextField idField = new JTextField(p != null ? p.getPatientId() : "");
        JTextField firstNameField = new JTextField(p != null ? p.getFirstName() : "");
        JTextField lastNameField = new JTextField(p != null ? p.getLastName() : "");
        JTextField dobField = new JTextField(p != null ? p.getDateOfBirth() : "");
        JTextField nhsField = new JTextField(p != null ? p.getNhsNumber() : "");
        JTextField genderField = new JTextField(p != null ? p.getGender() : "");
        JTextField phoneField = new JTextField(p != null ? p.getPhoneNumber() : "");
        JTextField emailField = new JTextField(p != null ? p.getEmail() : "");
        JTextField addressField = new JTextField(p != null ? p.getAddress() : "");
        JTextField postcodeField = new JTextField(p != null ? p.getPostcode() : "");
        JTextField emergencyNameField = new JTextField(p != null ? p.getEmergencyContactName() : "");
        JTextField emergencyPhoneField = new JTextField(p != null ? p.getEmergencyContactPhone() : "");
        JTextField registrationDateField = new JTextField(p != null ? p.getRegistrationDate() : "");
        JTextField gpIdField = new JTextField(p != null ? p.getGpSurgeryId() : "");

        panel.add(new JLabel("Patient ID:")); panel.add(idField);
        panel.add(new JLabel("First Name:")); panel.add(firstNameField);
        panel.add(new JLabel("Last Name:")); panel.add(lastNameField);
        panel.add(new JLabel("DOB:")); panel.add(dobField);
        panel.add(new JLabel("NHS Number:")); panel.add(nhsField);
        panel.add(new JLabel("Gender:")); panel.add(genderField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Address:")); panel.add(addressField);
        panel.add(new JLabel("Postcode:")); panel.add(postcodeField);
        panel.add(new JLabel("Emergency Name:")); panel.add(emergencyNameField);
        panel.add(new JLabel("Emergency Phone:")); panel.add(emergencyPhoneField);
        panel.add(new JLabel("Registration Date:")); panel.add(registrationDateField);
        panel.add(new JLabel("GP Surgery ID:")); panel.add(gpIdField);

        return panel;
    }

    private Patient readPatientFromForm(JPanel panel) {
        Component[] comps = panel.getComponents();
        return new Patient(
                ((JTextField) comps[1]).getText(),
                ((JTextField) comps[3]).getText(),
                ((JTextField) comps[5]).getText(),
                ((JTextField) comps[7]).getText(),
                ((JTextField) comps[9]).getText(),
                ((JTextField) comps[11]).getText(),
                ((JTextField) comps[13]).getText(),
                ((JTextField) comps[15]).getText(),
                ((JTextField) comps[17]).getText(),
                ((JTextField) comps[19]).getText(),
                ((JTextField) comps[21]).getText(),
                ((JTextField) comps[23]).getText(),
                ((JTextField) comps[25]).getText(),
                ((JTextField) comps[27]).getText()
        );
    }
}
