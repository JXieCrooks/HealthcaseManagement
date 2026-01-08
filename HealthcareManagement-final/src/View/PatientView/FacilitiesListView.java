
package View.PatientView;

import Controller.FacilitiesController;
import Model.Facility;
import Model.User;
import View.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

public class FacilitiesListView extends JPanel {

    private JTable facilityTable;
    private JButton prevPageButton, nextPageButton, exitButton, searchButton;
    private JTextField nameField, typeField;
    private FacilitiesController facilitiesController;
    private User currentUser;
    private int currentPage = 1;
    private int rowsPerPage = 10;
    private int totalPages = 1;
    private List<Facility> allFacilities;
    private List<Facility> filteredFacilities;

    public FacilitiesListView(FacilitiesController controller, User user) {
        this.facilitiesController = controller;
        this.currentUser = user;

        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Name:"));
        nameField = new JTextField(10);
        searchPanel.add(nameField);

        searchPanel.add(new JLabel("Type:"));
        typeField = new JTextField(10);
        searchPanel.add(typeField);

        searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        facilityTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(facilityTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        prevPageButton = new JButton("Previous Page");
        nextPageButton = new JButton("Next Page");
        exitButton = new JButton("Exit");

        buttonPanel.add(prevPageButton);
        buttonPanel.add(nextPageButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        prevPageButton.addActionListener(e -> previousPage());
        nextPageButton.addActionListener(e -> nextPage());
        exitButton.addActionListener(e -> goBackToLogin());
        searchButton.addActionListener(e -> searchFacilities());
        facilityTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = facilityTable.getSelectedRow();
                if (row >= 0) {
                    int index = (currentPage - 1) * rowsPerPage + row;
                    Facility f = filteredFacilities.get(index);
                    FacilitiesDetailView detailView = new FacilitiesDetailView(currentUser, f.getFacilityId());
                    detailView.setVisible(true);
                }
            }
        });

        loadFacilities();
    }

    private void loadFacilities() {
        facilitiesController.loadFacilitiesFromCSV(); // 手动加载 CSV
        allFacilities = facilitiesController.getAllFacilities();
        filteredFacilities = allFacilities; // 初始过滤列表等于所有数据
        currentPage = 1;
        refreshTable();
    }


    private void refreshTable() {
       int start = (currentPage - 1) * rowsPerPage;
       int end = Math.min(start + rowsPerPage, filteredFacilities.size());
       totalPages = (int) Math.ceil((double) filteredFacilities.size() / rowsPerPage);
       if (totalPages == 0) totalPages = 1;
       if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;
       String[] columns = {"ID", "Name", "Type", "Address", "Postcode", "Phone", "Email",
            "Opening Hours", "Manager", "Capacity", "Specialities Offered"};

       DefaultTableModel model = new DefaultTableModel(columns, 0);

       for (int i = start; i < end; i++) {
        Facility f = filteredFacilities.get(i);
        model.addRow(new Object[]{
                f.getFacilityId(),
                f.getFacilityName(),
                f.getFacilityType(),
                f.getAddress(),
                f.getPostcode(),
                f.getPhoneNumber(),
                f.getEmail(),
                f.getOpeningHours(),
                f.getManagerName(),
                f.getCapacity(),
                f.getSpecialitiesOffered()
        });
    }
    facilityTable.setModel(model);
    facilityTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    int[] columnWidths = {80, 120, 100, 150, 80, 100, 150, 120, 100, 80, 150};
    for (int i = 0; i < columnWidths.length; i++) {
        facilityTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
    }
}

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            refreshTable();
        }
    }

    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            refreshTable();
        }
    }

    private void searchFacilities() {
        String nameKeyword = nameField.getText().trim().toLowerCase();
        String typeKeyword = typeField.getText().trim().toLowerCase();

        filteredFacilities = allFacilities.stream()
                .filter(f -> f.getFacilityName().toLowerCase().contains(nameKeyword)
                        && f.getFacilityType().toLowerCase().contains(typeKeyword))
                .collect(Collectors.toList());

        currentPage = 1;
        refreshTable();
    }

    private void goBackToLogin() {
        SwingUtilities.getWindowAncestor(this).dispose();
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }
}
