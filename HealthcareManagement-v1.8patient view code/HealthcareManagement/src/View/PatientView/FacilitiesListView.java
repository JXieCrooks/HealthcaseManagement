//
//package View.PatientView;
//
//import Controller.FacilitiesController;
//import Model.Facility;
//import Model.User;
//import View.LoginView;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.util.List;
//
//public class FacilitiesListView extends JPanel {
//
//    private JTable facilityTable;
//    private JButton prevPageButton, nextPageButton, exitButton;
//    private FacilitiesController facilitiesController;
//    private User currentUser;
//
//    // 分页变量
//    private int currentPage = 1;
//    private int rowsPerPage = 5; // 每页显示5条记录
//    private int totalPages = 1;
//    private List<Facility> allFacilities;
//
//    public FacilitiesListView(FacilitiesController controller, User user) {
//        this.facilitiesController = controller;
//        this.currentUser = user;
//
//        setLayout(new BorderLayout());
//
//        // 表格
//        facilityTable = new JTable();
//        JScrollPane scrollPane = new JScrollPane(facilityTable);
//        add(scrollPane, BorderLayout.CENTER);
//
//        // 底部按钮面板
//        JPanel buttonPanel = new JPanel();
//        prevPageButton = new JButton("Previous Page");
//        nextPageButton = new JButton("Next Page");
//        exitButton = new JButton("Exit");
//
//        buttonPanel.add(prevPageButton);
//        buttonPanel.add(nextPageButton);
//        buttonPanel.add(exitButton);
//
//        add(buttonPanel, BorderLayout.SOUTH);
//
//        // 按钮事件绑定
//        prevPageButton.addActionListener(e -> previousPage());
//        nextPageButton.addActionListener(e -> nextPage());
//        exitButton.addActionListener(e -> goBackToLogin());
//
//        // 表格点击事件：点击医院行 → 打开医院详情页
//        facilityTable.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                int row = facilityTable.getSelectedRow();
//                if (row >= 0) {
//                    int index = (currentPage - 1) * rowsPerPage + row;
//                    Facility f = allFacilities.get(index);
//                    // 跳转至详情页
//                    FacilitiesDetailView detailView = new FacilitiesDetailView(currentUser, f.getFacilityId());
//                    detailView.setVisible(true);
//                }
//            }
//        });
//
//        // 初始加载数据
//        loadFacilities();
//    }
//
//    private void loadFacilities() {
//        facilitiesController.loadFacilitiesFromCSV(); // 手动加载 CSV
//        allFacilities = facilitiesController.getAllFacilities();
//
//        totalPages = (int) Math.ceil((double) allFacilities.size() / rowsPerPage);
//        if (currentPage > totalPages) currentPage = totalPages;
//        if (currentPage < 1) currentPage = 1;
//
//        updateTable();
//    }
//
//    private void updateTable() {
//        int start = (currentPage - 1) * rowsPerPage;
//        int end = Math.min(start + rowsPerPage, allFacilities.size());
//
//        String[] columns = {"ID", "Name", "Type", "Address", "Phone"};
//        DefaultTableModel model = new DefaultTableModel(columns, 0);
//
//        for (int i = start; i < end; i++) {
//            Facility f = allFacilities.get(i);
//            model.addRow(new Object[]{
//                    f.getFacilityId(),
//                    f.getFacilityName(),
//                    f.getFacilityType(),
//                    f.getAddress(),
//                    f.getPhoneNumber()
//            });
//        }
//
//        facilityTable.setModel(model);
//    }
//
//    private void previousPage() {
//        if (currentPage > 1) {
//            currentPage--;
//            updateTable();
//        }
//    }
//
//    private void nextPage() {
//        if (currentPage < totalPages) {
//            currentPage++;
//            updateTable();
//        }
//    }
//
//    private void goBackToLogin() {
//        // 关闭当前窗口
//        SwingUtilities.getWindowAncestor(this).dispose();
//        // 打开登录页
//        LoginView loginView = new LoginView();
//        loginView.setVisible(true);
//    }
//}
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

    // 分页变量
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
        updateTable();
    }

    private void updateTable() {
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filteredFacilities.size());

        totalPages = (int) Math.ceil((double) filteredFacilities.size() / rowsPerPage);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        String[] columns = {"ID", "Name", "Type", "Address", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = start; i < end; i++) {
            Facility f = filteredFacilities.get(i);
            model.addRow(new Object[]{
                    f.getFacilityId(),
                    f.getFacilityName(),
                    f.getFacilityType(),
                    f.getAddress(),
                    f.getPhoneNumber()
            });
        }

        facilityTable.setModel(model);
    }

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTable();
        }
    }

    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updateTable();
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
        updateTable();
    }

    private void goBackToLogin() {
        SwingUtilities.getWindowAncestor(this).dispose();
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }
}
