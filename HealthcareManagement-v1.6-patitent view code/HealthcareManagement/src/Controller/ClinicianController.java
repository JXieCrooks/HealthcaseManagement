package Controller;

import Model.Clinician;
import Utils.CSVReader;

import java.util.ArrayList;
import java.util.List;

public class ClinicianController {

    private final String filePath = "data/clinicians.csv";
    private List<Clinician> clinicians = new ArrayList<>();

    public ClinicianController() {
        loadCliniciansFromCSV();
    }

    private void loadCliniciansFromCSV() {
        clinicians.clear();
        List<String[]> rows = CSVReader.readCSV(filePath);

        for (String[] r : rows) {
            if (r.length < 12 || r[0].equals("clinician_id")) continue;
            Clinician c = new Clinician(
                    r[0], r[1], r[2], r[3], r[4],
                    r[5], r[6], r[7],
                    r[8], r[9], r[10], r[11]
            );
            clinicians.add(c);
        }
    }

    public List<Clinician> getAllClinicians() {
        return clinicians;
    }

    public List<Clinician> getCliniciansByFacility(String facilityId) {
        List<Clinician> result = new ArrayList<>();
        for (Clinician c : clinicians) {
            if (c.getWorkplaceId().equals(facilityId)) {
                result.add(c);
            }
        }
        return result;
    }

    public Clinician getClinicianById(String clinicianId) {
        for (Clinician c : clinicians) {
            if (c.getClinicianId().equals(clinicianId)) return c;
        }
        return null;
    }
}
