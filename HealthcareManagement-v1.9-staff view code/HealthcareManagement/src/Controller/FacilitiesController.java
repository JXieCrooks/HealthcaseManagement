package Controller;

import Model.Facility;
import Utils.CSVReader;

import java.util.ArrayList;
import java.util.List;

public class FacilitiesController {

    private final String filePath = "data/facilities.csv";
    private List<Facility> facilities;

    public FacilitiesController() {
        facilities = new ArrayList<>();
    }

    public void loadFacilitiesFromCSV() {
        facilities.clear();
        List<String[]> rows = CSVReader.readCSV(filePath);

        for (String[] r : rows) {
            Facility f = new Facility(
                    r[0], // facility_id
                    r[1], // facility_name
                    r[2], // facility_type
                    r[3], // address
                    r[4], // postcode
                    r[5], // phone_number
                    r[6], // email
                    r[7], // opening_hours
                    r[8], // manager_name
                    r[9], // capacity
                    r[10] // specialities_offered
            );
            facilities.add(f);
        }
    }

    public List<Facility> getAllFacilities() {
        return facilities;
    }
    public Facility getFacilityById(String id) {
        for (Facility f : getAllFacilities()) {
            if (f.getFacilityId().equals(id)) return f;
        }
        return null;
    }

    public List<Facility> searchFacilities(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return facilities;
        }
        String lower = keyword.toLowerCase();
        List<Facility> result = new ArrayList<>();
        for (Facility f : facilities) {
            if (f.getFacilityName().toLowerCase().contains(lower) ||
                    f.getFacilityType().toLowerCase().contains(lower)) {
                result.add(f);
            }
        }
        return result;
    }
}
