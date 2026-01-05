package Controller;

import Model.Staff;
import Utils.CSVReader;
import Utils.FileWriterUtil;

import java.util.ArrayList;
import java.util.List;

public class StaffController {

    private final String filePath = "data/staff.csv";
    private List<Staff> staffList;

    public StaffController() {
        staffList = new ArrayList<>();
        loadFromCSV();
    }

    public void loadFromCSV() {
        staffList.clear();
        List<String[]> rows = CSVReader.readCSV(filePath);
        for (String[] r : rows) {
            if (r.length < 12 || r[0].equals("staff_id")) continue;
            Staff s = new Staff(
                    r[0],  // staff_id
                    r[1],  // first_name
                    r[2],  // last_name
                    r[3],  // role
                    r[4],  // department
                    r[5],  // facility_id
                    r[6],  // phone_number
                    r[7],  // email
                    r[8],  // employment_status
                    r[9],  // start_date
                    r[10], // line_manager
                    r[11]  // access_level
            );
            staffList.add(s);
        }
    }


    public List<Staff> getAllStaff() {
        return staffList;
    }

    public Staff getStaffById(String staffId) {
        if (staffList == null) return null;
        for (Staff s : staffList) {
            if (s.getStaffId().trim().equalsIgnoreCase(staffId.trim())) {
                return s;
            }
        }
        return null;
    }

}
