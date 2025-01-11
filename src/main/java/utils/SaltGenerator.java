package utils;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SaltGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateSalt() {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : saltBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void updateSalts(Connection connection) throws Exception {
        String query = "SELECT id FROM users";
        String updateQuery = "UPDATE users SET salt = ? WHERE id = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(query);
             ResultSet resultSet = selectStmt.executeQuery();
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String salt = generateSalt();

                updateStmt.setString(1, salt);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();
            }
        }
    }
}
