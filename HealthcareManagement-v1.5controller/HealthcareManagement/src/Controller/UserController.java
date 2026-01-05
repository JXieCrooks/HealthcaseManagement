package Controller;

import Model.User;
import Utils.CSVReader;
import Utils.SingletonRegistry;

import java.util.List;

public class UserController {

    private final String userFilePath = "data/user.csv";

    public User login(String username, String password, String role) {

        List<String[]> rows = CSVReader.readCSV(userFilePath);

        for (String[] r : rows) {
            // r[1]=username, r[2]=password, r[3]=role
            if (r.length >= 5
                    && r[1].equals(username)
                    && r[2].equals(password)
                    && r[3].equalsIgnoreCase(role)) {

                User user = new User(
                        r[0], // user_id
                        r[1], // username
                        r[2], // password
                        r[3], // role
                        r[4]  // related_id
                );

                // 保存当前登录用户（单例）
                SingletonRegistry.getInstance()
                        .register("currentUser", user);

                return user;
            }
        }
        return null;
    }

    public User getCurrentUser() {
        return (User) SingletonRegistry.getInstance()
                .get("currentUser");
    }

    public void logout() {
        SingletonRegistry.getInstance()
                .register("currentUser", null);
    }
}
