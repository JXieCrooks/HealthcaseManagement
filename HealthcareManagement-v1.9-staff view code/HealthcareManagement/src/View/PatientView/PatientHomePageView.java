package View.PatientView;

import Controller.FacilitiesController;
import Model.User;

import javax.swing.*;
import java.awt.*;

public class PatientHomePageView extends JFrame {

    private JTabbedPane tabbedPane;
    private FacilitiesController facilitiesController;

    public PatientHomePageView(User user) {
        setTitle("Patient Home Page");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        facilitiesController = new FacilitiesController();

        tabbedPane = new JTabbedPane();

        FacilitiesListView facilitiesListView = new FacilitiesListView(facilitiesController, user);
        tabbedPane.addTab("Hospitals", facilitiesListView);


         MyAppointmentsView myAppointmentsView = new MyAppointmentsView(user);
         tabbedPane.addTab("My Appointments", myAppointmentsView);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void showHomePage() {
        setVisible(true);
    }
}
