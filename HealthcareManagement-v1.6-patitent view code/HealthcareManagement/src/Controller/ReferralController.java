package Controller;

import Model.Referral;
import Utils.FileWriterUtil;
import Utils.CSVReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReferralController {

    private static ReferralController instance;
    private final String filePath = "data/referrals.csv";
    private final String outputFilePath = "output/referrals_output.txt";
    private List<Referral> referrals = new ArrayList<>();

    private ReferralController() {
        loadFromCSV();
        exportToReferralFile();
    }

    // Get Instance
    public static synchronized ReferralController getInstance() {
        if (instance == null) {
            instance = new ReferralController();
            // Register to the global singleton registry
            Utils.SingletonRegistry.getInstance().register("ReferralController", instance);
        }
        return instance;
    }

    public void loadFromCSV() {
        referrals.clear();
        List<String[]> rows = CSVReader.readCSV(filePath);

        for (String[] r : rows) {
            if (r.length < 16 || "referral_id".equals(r[0])) continue;

            referrals.add(new Referral(
                    r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7],
                    r[8], r[9], r[10], r[11], r[12], r[13], r[14], r[15]
            ));
        }
    }

    public List<Referral> getAllReferrals() {
        return new ArrayList<>(referrals);
    }

    public void reload() {
        loadFromCSV();
        exportToReferralFile();
    }

    public void addReferral(Referral r) {
        referrals.add(r);
        saveAll();
        exportToReferralFile();
    }

    public void updateReferral(Referral updated) {
        for (int i = 0; i < referrals.size(); i++) {
            if (referrals.get(i).getReferralId().equals(updated.getReferralId())) {
                referrals.set(i, updated);
                break;
            }
        }
        saveAll();
        exportToReferralFile();
    }

    public void deleteReferral(String referralId) {
        referrals.removeIf(r -> r.getReferralId().equals(referralId));
        saveAll();
        exportToReferralFile();
    }

    private void saveAll() {
        List<String> lines = new ArrayList<>();
        lines.add(String.join(",", new String[]{
                "referral_id","patient_id","referring_clinician_id","referred_to_clinician_id",
                "referring_facility_id","referred_to_facility_id","referral_date","urgency_level",
                "referral_reason","clinical_summary","requested_investigations","status",
                "appointment_id","notes","created_date","last_updated"
        }));

        for (Referral r : referrals) {
            lines.add(String.join(",", new String[]{
                    r.getReferralId(), r.getPatientId(), r.getReferringClinicianId(),
                    r.getReferredToClinicianId(), r.getReferringFacilityId(), r.getReferredToFacilityId(),
                    r.getReferralDate(), r.getUrgencyLevel(), r.getReferralReason(), r.getClinicalSummary(),
                    r.getRequestedInvestigations(), r.getStatus(), r.getAppointmentId(),
                    r.getNotes(), r.getCreatedDate(), r.getLastUpdated()
            }));
        }

        FileWriterUtil.writeFile(filePath, lines);
    }

    public void exportToReferralFile() {
        try {
            File folder = new File("output");
            if (!folder.exists()) folder.mkdirs();

            FileWriter writer = new FileWriter(outputFilePath);

            writer.write(String.join(",", new String[]{
                    "referral_id","patient_id","referring_clinician_id","referred_to_clinician_id",
                    "referring_facility_id","referred_to_facility_id","referral_date","urgency_level",
                    "referral_reason","clinical_summary","requested_investigations","status",
                    "appointment_id","notes","created_date","last_updated"
            }) + "\n");

            for (Referral r : referrals) {
                writer.write(String.join(",", new String[]{
                        r.getReferralId(), r.getPatientId(), r.getReferringClinicianId(),
                        r.getReferredToClinicianId(), r.getReferringFacilityId(), r.getReferredToFacilityId(),
                        r.getReferralDate(), r.getUrgencyLevel(), r.getReferralReason(), r.getClinicalSummary(),
                        r.getRequestedInvestigations(), r.getStatus(), r.getAppointmentId(),
                        r.getNotes(), r.getCreatedDate(), r.getLastUpdated()
                }) + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateNewId() {
        int max = 0;
        for (Referral r : referrals) {
            try {
                int n = Integer.parseInt(r.getReferralId().replaceAll("\\D", ""));
                if (n > max) max = n;
            } catch (Exception ignored) {}
        }
        return String.format("R%03d", max + 1);
    }
}
