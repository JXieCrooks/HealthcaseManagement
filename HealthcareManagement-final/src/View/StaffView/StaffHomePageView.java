
package View.StaffView;

import Controller.*;
import Model.*;

import javax.swing.*;
import java.awt.*;

public class StaffHomePageView extends JFrame {

    private User currentUser;
    private JPanel mainPanel;

    private PatientController patientController;
    private AppointmentController appointmentController;
    private PrescriptionController prescriptionController;
    private ReferralController referralController;
    public StaffHomePageView(User user) {
        this.currentUser = user;

        this.patientController = new PatientController();
        this.appointmentController = new AppointmentController();
        this.prescriptionController = new PrescriptionController();
        this.referralController = ReferralController.getInstance();
        setTitle("Staff Home Page - " + user.getUsername());
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        mainPanel = new JPanel(new CardLayout());
        add(mainPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton patientButton = new JButton("Patient Management");
        JButton appointmentButton = new JButton("Appointment Management");
        JButton prescriptionButton = new JButton("Prescription Management");
        JButton referralButton = new JButton("ReferralManage Management");
        topPanel.add(patientButton);
        topPanel.add(appointmentButton);
        topPanel.add(prescriptionButton);
        topPanel.add(referralButton);
        add(topPanel, BorderLayout.NORTH);

        PatientManageView patientManageView = new PatientManageView(currentUser, patientController);
        mainPanel.add(patientManageView, "patient");

        AppointmentManageView appointmentManageView = new AppointmentManageView(currentUser, appointmentController);
        mainPanel.add(appointmentManageView, "appointment");

        PrescriptionManageView prescriptionManageView = new PrescriptionManageView(currentUser, prescriptionController);
        mainPanel.add(prescriptionManageView, "prescription");

        ReferralManageView referralManageView=new ReferralManageView(currentUser,referralController);
        mainPanel.add(referralManageView, "referral");
        CardLayout cl = (CardLayout) mainPanel.getLayout();

        patientButton.addActionListener(e -> cl.show(mainPanel, "patient"));
        appointmentButton.addActionListener(e -> cl.show(mainPanel, "appointment"));
        prescriptionButton.addActionListener(e -> cl.show(mainPanel, "prescription"));
        referralButton.addActionListener(e -> cl.show(mainPanel, "referral"));
    }


    public void showHomePage() {
        setVisible(true);
    }

    public static void main(String[] args) {
        User dummyUser = new User();
        dummyUser.setUsername("staff01");

        StaffHomePageView view = new StaffHomePageView(dummyUser);
        view.showHomePage();
    }
}
