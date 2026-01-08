

package View.StaffView;

import Controller.*;
import Model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReferralManageView extends JPanel {

    private JTable table;
    private JButton prevButton, nextButton, addButton, editButton, deleteButton, refreshButton, searchButton;
    private JTextField searchField;

    private ReferralController referralController;
    private ClinicianController clinicianController;
    private PatientController patientController;
    private AppointmentController appointmentController;
    private FacilitiesController facilityController;
    private StaffController staffController;

    private List<Referral> allReferrals;
    private List<Referral> filteredReferrals;
    private int page = 1;
    private final int PAGE_SIZE = 10;

    private User currentUser;

    public ReferralManageView(User user, ReferralController controller) {
        this.currentUser = user;
        this.referralController = controller;
        this.clinicianController = new ClinicianController();
        this.patientController = new PatientController();
        this.appointmentController = new AppointmentController();
        this.facilityController = new FacilitiesController();
        this.staffController = new StaffController();

        setLayout(new BorderLayout());


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search by Patient ID or Referring Clinician ID:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);


        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadReferrals();

        prevButton.addActionListener(e -> previousPage());
        nextButton.addActionListener(e -> nextPage());
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showUpdateDialog());
        deleteButton.addActionListener(e -> deleteSelected());
        refreshButton.addActionListener(e -> loadReferrals());
        searchButton.addActionListener(e -> searchReferrals());
    }

    private void loadReferrals() {
        referralController.reload();
        allReferrals = referralController.getAllReferrals();
        filteredReferrals = allReferrals;
        page = 1;


        table.setModel(new DefaultTableModel());
        refreshTable();
    }

    private void refreshTable() {
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filteredReferrals.size());

        String[] columns = {"Referral ID","Patient ID","Referring Clinician ID","Referred To Clinician ID",
                "Referring Facility ID","Referred Facility ID","Referral Date","Urgency","Reason","Clinical Summary",
                "Investigations","Status","Appointment ID","Notes","Created","Last Updated"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = start; i < end; i++) {
            Referral r = filteredReferrals.get(i);
            model.addRow(new Object[]{
                    r.getReferralId(),
                    r.getPatientId(),
                    r.getReferringClinicianId(),
                    r.getReferredToClinicianId(),
                    r.getReferringFacilityId(),
                    r.getReferredToFacilityId(),
                    r.getReferralDate(),
                    r.getUrgencyLevel(),
                    r.getReferralReason(),
                    r.getClinicalSummary(),
                    r.getRequestedInvestigations(),
                    r.getStatus(),
                    r.getAppointmentId(),
                    r.getNotes(),
                    r.getCreatedDate(),
                    r.getLastUpdated()
            });
        }

        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // 允许横向滚动

        int[] columnWidths = {100, 100, 120, 120, 120, 120, 100, 80, 150, 200, 150, 80, 100, 200, 120, 120};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }


    private void previousPage() {
        if (page > 1) {
            page--;
            refreshTable();
        }
    }

    private void nextPage() {
        int maxPage = (int) Math.ceil((double) filteredReferrals.size() / PAGE_SIZE);
        if (page < maxPage) {
            page++;
            refreshTable();
        }
    }

    private void searchReferrals() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            filteredReferrals = allReferrals;
        } else {
            filteredReferrals = allReferrals.stream()
                    .filter(r -> r.getPatientId().toLowerCase().contains(keyword) ||
                            r.getReferringClinicianId().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }
        page = 1;
        refreshTable();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int index = (page - 1) * PAGE_SIZE + row;
        if (index >= filteredReferrals.size()) return;
        String id = filteredReferrals.get(index).getReferralId();
        referralController.deleteReferral(id);
        loadReferrals();
    }
    private void showAddDialog() {
        facilityController.loadFacilitiesFromCSV();
        List<Facility> facilities = facilityController.getAllFacilities();
        if (facilities.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No facilities loaded!");
            return;
        }

        List<Patient> patients = patientController.getAllPatients();
        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patients loaded!");
            return;
        }

        List<Clinician> clinicians = clinicianController.getAllClinicians();
        if (clinicians.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No clinicians loaded!");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Referral", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(16, 2, 5, 5));

        JComboBox<String> patientBox = new JComboBox<>();
        for (Patient p : patients) patientBox.addItem(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName());

        JComboBox<String> referredClinicianBox = new JComboBox<>();
        for (Clinician c : clinicians) referredClinicianBox.addItem(c.getClinicianId() + " - " + c.getFirstName() + " " + c.getLastName());

        JComboBox<String> referredFacilityBox = new JComboBox<>();
        for (Facility f : facilities) referredFacilityBox.addItem(f.getFacilityId() + " - " + f.getFacilityName());

        String referringClinicianId = currentUser.getUserId();
        Staff staff = staffController.getStaffById(currentUser.getReferenceId());
        if (staff == null) {
            JOptionPane.showMessageDialog(this, "Cannot find staff for current user");
            return;
        }
        String referringFacilityId = staff.getFacilityId();

        JComboBox<String> appointmentBox = new JComboBox<>();
        for (Appointment a : appointmentController.getAllAppointments()) {
            appointmentBox.addItem(a.getAppointmentId() + " - " + a.getAppointmentDate() + " " + a.getAppointmentTime());
        }

        JTextField referralDateField = new JTextField();
        JTextField urgencyField = new JTextField();
        JTextField reasonField = new JTextField();
        JTextField summaryField = new JTextField();
        JTextField investigationsField = new JTextField();
        JTextField statusField = new JTextField();
        JTextField notesField = new JTextField();
        JTextField createdField = new JTextField();
        JTextField updatedField = new JTextField();

        panel.add(new JLabel("Patient:")); panel.add(patientBox);
        panel.add(new JLabel("Referring Clinician ID:")); panel.add(new JLabel(referringClinicianId));
        panel.add(new JLabel("Referred Clinician:")); panel.add(referredClinicianBox);
        panel.add(new JLabel("Referring Facility ID:")); panel.add(new JLabel(referringFacilityId));
        panel.add(new JLabel("Referred Facility:")); panel.add(referredFacilityBox);
        panel.add(new JLabel("Appointment:")); panel.add(appointmentBox);
        panel.add(new JLabel("Referral Date:")); panel.add(referralDateField);
        panel.add(new JLabel("Urgency Level:")); panel.add(urgencyField);
        panel.add(new JLabel("Referral Reason:")); panel.add(reasonField);
        panel.add(new JLabel("Clinical Summary:")); panel.add(summaryField);
        panel.add(new JLabel("Requested Investigations:")); panel.add(investigationsField);
        panel.add(new JLabel("Status:")); panel.add(statusField);
        panel.add(new JLabel("Notes:")); panel.add(notesField);
        panel.add(new JLabel("Created Date:")); panel.add(createdField);
        panel.add(new JLabel("Last Updated:")); panel.add(updatedField);

        JButton okBtn = new JButton("Add");
        panel.add(new JLabel()); panel.add(okBtn);

        okBtn.addActionListener(e -> {
            Object patientSel = patientBox.getSelectedItem();
            Object referredClinSel = referredClinicianBox.getSelectedItem();
            Object referredFacSel = referredFacilityBox.getSelectedItem();
            Object appointmentSel = appointmentBox.getSelectedItem();
            if(patientSel == null || referredClinSel == null || referredFacSel == null || appointmentSel == null) return;

            Referral r = new Referral(
                    referralController.generateNewId(),
                    patientSel.toString().split(" - ")[0],
                    referringClinicianId,
                    referredClinSel.toString().split(" - ")[0],
                    referringFacilityId,
                    referredFacSel.toString().split(" - ")[0],
                    referralDateField.getText(),
                    urgencyField.getText(),
                    reasonField.getText(),
                    summaryField.getText(),
                    investigationsField.getText(),
                    statusField.getText(),
                    appointmentSel.toString().split(" - ")[0],
                    notesField.getText(),
                    createdField.getText(),
                    updatedField.getText()
            );

            referralController.addReferral(r);
            dialog.dispose();
            loadReferrals();
        });

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void showUpdateDialog() {
        if (filteredReferrals == null || filteredReferrals.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No referrals to update!");
            return;
        }

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!");
            return;
        }

        int index = (page - 1) * PAGE_SIZE + row;
        if (index >= filteredReferrals.size()) {
            JOptionPane.showMessageDialog(this, "Invalid selection!");
            return;
        }

        Referral r = filteredReferrals.get(index);

        facilityController.loadFacilitiesFromCSV();
        List<Facility> facilities = facilityController.getAllFacilities();
        if (facilities.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No facilities loaded!");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Referral", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(16, 2, 5, 5));

        JComboBox<String> patientBox = new JComboBox<>();
        for (Patient p : patientController.getAllPatients()) {
            patientBox.addItem(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName());
        }
        patientBox.setSelectedItem(r.getPatientId() + " - " + getPatientName(r.getPatientId()));

        JComboBox<String> referredClinicianBox = new JComboBox<>();
        for (Clinician c : clinicianController.getAllClinicians()) {
            referredClinicianBox.addItem(c.getClinicianId() + " - " + c.getFirstName() + " " + c.getLastName());
        }
        referredClinicianBox.setSelectedItem(r.getReferredToClinicianId() + " - " + getClinicianName(r.getReferredToClinicianId()));

        String referringClinicianId = r.getReferringClinicianId();
        Staff staff = staffController.getStaffById(currentUser.getReferenceId());
        if (staff == null) {
            JOptionPane.showMessageDialog(this, "Cannot find staff for current user");
            return;
        }
        String referringFacilityId = staff.getFacilityId();

        JComboBox<String> referredFacilityBox = new JComboBox<>();
        for (Facility f : facilities) {
            referredFacilityBox.addItem(f.getFacilityId() + " - " + f.getFacilityName());
        }
        referredFacilityBox.setSelectedItem(r.getReferredToFacilityId() + " - " + getFacilityName(r.getReferredToFacilityId()));

        JComboBox<String> appointmentBox = new JComboBox<>();
        for (Appointment a : appointmentController.getAllAppointments()) {
            appointmentBox.addItem(a.getAppointmentId() + " - " + a.getAppointmentDate() + " " + a.getAppointmentTime());
        }
        appointmentBox.setSelectedItem(r.getAppointmentId() + " - " + getAppointmentInfo(r.getAppointmentId()));

        JTextField referralDateField = new JTextField(r.getReferralDate());
        JTextField urgencyField = new JTextField(r.getUrgencyLevel());
        JTextField reasonField = new JTextField(r.getReferralReason());
        JTextField summaryField = new JTextField(r.getClinicalSummary());
        JTextField investigationsField = new JTextField(r.getRequestedInvestigations());
        JTextField statusField = new JTextField(r.getStatus());
        JTextField notesField = new JTextField(r.getNotes());
        JTextField createdField = new JTextField(r.getCreatedDate());
        JTextField updatedField = new JTextField(r.getLastUpdated());

        panel.add(new JLabel("Patient:")); panel.add(patientBox);
        panel.add(new JLabel("Referring Clinician ID:")); panel.add(new JLabel(referringClinicianId));
        panel.add(new JLabel("Referred Clinician:")); panel.add(referredClinicianBox);
        panel.add(new JLabel("Referring Facility ID:")); panel.add(new JLabel(referringFacilityId));
        panel.add(new JLabel("Referred Facility:")); panel.add(referredFacilityBox);
        panel.add(new JLabel("Appointment:")); panel.add(appointmentBox);
        panel.add(new JLabel("Referral Date:")); panel.add(referralDateField);
        panel.add(new JLabel("Urgency Level:")); panel.add(urgencyField);
        panel.add(new JLabel("Referral Reason:")); panel.add(reasonField);
        panel.add(new JLabel("Clinical Summary:")); panel.add(summaryField);
        panel.add(new JLabel("Requested Investigations:")); panel.add(investigationsField);
        panel.add(new JLabel("Status:")); panel.add(statusField);
        panel.add(new JLabel("Notes:")); panel.add(notesField);
        panel.add(new JLabel("Created Date:")); panel.add(createdField);
        panel.add(new JLabel("Last Updated:")); panel.add(updatedField);

        JButton okBtn = new JButton("Update");
        panel.add(new JLabel()); panel.add(okBtn);

        okBtn.addActionListener(e -> {
            Referral updated = new Referral(
                    r.getReferralId(),
                    patientBox.getSelectedItem().toString().split(" - ")[0],
                    referringClinicianId,
                    referredClinicianBox.getSelectedItem().toString().split(" - ")[0],
                    referringFacilityId,
                    referredFacilityBox.getSelectedItem().toString().split(" - ")[0],
                    referralDateField.getText(),
                    urgencyField.getText(),
                    reasonField.getText(),
                    summaryField.getText(),
                    investigationsField.getText(),
                    statusField.getText(),
                    appointmentBox.getSelectedItem().toString().split(" - ")[0],
                    notesField.getText(),
                    createdField.getText(),
                    updatedField.getText()
            );
            referralController.updateReferral(updated);
            dialog.dispose();
            loadReferrals();
        });

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private String getPatientName(String id) {
        Patient p = patientController.getPatientById(id);
        return p == null ? "" : p.getFirstName() + " " + p.getLastName();
    }

    private String getClinicianName(String id) {
        Clinician c = clinicianController.getClinicianById(id);
        return c == null ? "" : c.getFirstName() + " " + c.getLastName();
    }

    private String getFacilityName(String id) {
        Facility f = facilityController.getFacilityById(id);
        return f == null ? "" : f.getFacilityName();
    }

    private String getAppointmentInfo(String id) {
        Appointment a = appointmentController.getAppointmentById(id);
        return a == null ? "" : a.getAppointmentDate() + " " + a.getAppointmentTime();
    }
}
