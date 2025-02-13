import java.sql.*;

public class DatabaseSetup {
    public static void main(String[] args) {
        // URL der SQLite-Datenbank
        String url = "jdbc:sqlite:karte.db";

        // SQL-Anweisung zum Erstellen der Tabelle
        String sql = "CREATE TABLE IF NOT EXISTS Karten (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " code TEXT NOT NULL,\n"
                + " value TEXT NOT NULL,\n"
                + " typ TEXT NOT NULL,\n"
                + " name TEXT NOT NULL\n"
                + ");";

        // In eine Datenbank zb Statistiken

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // Verbindung zur SQLite-Datenbank herstellen
            if (conn != null) {
                // Tabelle erstellen
                stmt.execute(sql);
                System.out.println("Datenbank und Tabelle wurden erfolgreich erstellt.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}