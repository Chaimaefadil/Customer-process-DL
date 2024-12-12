import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        parseJsonThread("src/data/input.json");
    }

    public static void parseJsonThread(String inputFile) {
        new Thread(() -> {
            try {
                // Lire le fichier JSON
                String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(inputFile)));
                JSONArray ordersArray = new JSONArray(content);

                // Connexion à la base de données
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/mydatabase", "root", "");

                // Préparer les fichiers de sortie
                BufferedWriter outputWriter = new BufferedWriter(new FileWriter("src/data/output.json"));
                BufferedWriter errorWriter = new BufferedWriter(new FileWriter("src/data/error.json"));

                List<JSONObject> outputList = new ArrayList<>();
                List<JSONObject> errorList = new ArrayList<>();

                for (int i = 0; i < ordersArray.length(); i++) {
                    JSONObject orderJson = ordersArray.getJSONObject(i);

                    int id = orderJson.getInt("id");
                    double amount = orderJson.getDouble("amount");
                    int customerId = orderJson.getInt("customer_id");
                    long date = System.currentTimeMillis();

                    // Vérifier si le client existe
                    PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM customer WHERE id = ?");
                    stmt.setInt(1, customerId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0) {
                        // Client existe : ajouter dans la table order
                        PreparedStatement insertStmt = connection.prepareStatement(
                                "INSERT INTO `order` (id, date, amount, customer_id) VALUES (?, ?, ?, ?)");
                        insertStmt.setInt(1, id);
                        insertStmt.setLong(2, date);
                        insertStmt.setDouble(3, amount);
                        insertStmt.setInt(4, customerId);
                        insertStmt.executeUpdate();

                        // Ajouter à la liste de sortie
                        outputList.add(orderJson);
                    } else {
                        // Client n'existe pas : ajouter au fichier error
                        errorList.add(orderJson);
                    }
                }

                // Écrire dans les fichiers JSON
                outputWriter.write(outputList.toString());
                errorWriter.write(errorList.toString());

                // Fermer les fichiers et la connexion
                outputWriter.close();
                errorWriter.close();
                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
