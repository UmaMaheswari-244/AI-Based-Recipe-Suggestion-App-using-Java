import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.json.JSONObject;

public class RecipeSuggestionApp extends JFrame {

    // API URL and key
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=Your api key";

    // Derby DB credentials
    private static final String DB_URL = "jdbc:derby://localhost:1527/RecipeDataBase;create=true";
    private static final String USER = "app"; 
    private static final String PASSWORD = "app";

    private JTextArea inputArea;
    private JTextArea responseArea;
    private JButton submitButton;
    private JButton clearButton;

    public RecipeSuggestionApp() {
        setTitle("AI-based Recipe Suggestion App");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 240, 240));

        // Create UI components
        inputArea = new JTextArea(5, 45);
        inputArea.setBorder(BorderFactory.createTitledBorder("Enter ingredients (comma-separated):"));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        responseArea = new JTextArea(10, 45);
        responseArea.setEditable(false);
        responseArea.setBorder(BorderFactory.createTitledBorder("Recipe Suggestions:"));
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);

        submitButton = new JButton("Get Recipe Suggestions");
        submitButton.setIcon(new ImageIcon("path/to/submit_icon.png")); // Add a submit icon
        submitButton.setToolTipText("Click to get recipe suggestions");

        clearButton = new JButton("Clear");
        clearButton.setIcon(new ImageIcon("path/to/clear_icon.png")); // Add a clear icon
        clearButton.setToolTipText("Click to clear the input and suggestions");

        // Add action listener for submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ingredients = inputArea.getText().trim();
                if (!ingredients.isEmpty()) {
                    new Thread(() -> {
                        try {
                            String recipeSuggestion = getGeminiRecipeSuggestions(ingredients);
                            SwingUtilities.invokeLater(() -> responseArea.setText(recipeSuggestion));
                            saveToDatabase(ingredients, recipeSuggestion);
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> responseArea.setText("Error: " + ex.getMessage()));
                        }
                    }).start();
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter ingredients.");
                }
            }
        });

        // Add action listener for clear button
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                responseArea.setText("");
            }
        });

        // Layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to the frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JScrollPane(inputArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(submitButton, gbc);

        gbc.gridx = 1;
        add(clearButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(new JScrollPane(responseArea), gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RecipeSuggestionApp gui = new RecipeSuggestionApp();
            gui.setVisible(true);
        });
    }

    /**
     * Makes a POST request to Google Gemini API with the ingredients and returns the recipe suggestion.
     */
    private String getGeminiRecipeSuggestions(String ingredients) throws Exception {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String jsonPayload = "{"
                + "\"contents\": ["
                + "{ \"parts\": ["
                + "{\"text\": \"Generate a recipe using these ingredients: " + ingredients + "\"}"
                + "]} "
                + "] }";

        RequestBody body = RequestBody.create(jsonPayload, JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                return jsonResponse.getJSONArray("candidates")
                                   .getJSONObject(0)
                                   .getJSONObject("content")
                                   .getJSONArray("parts")
                                   .getJSONObject(0)
                                   .getString("text");
            } else {
                throw new Exception("API request failed with code: " + response.code());
            }
        }
    }

    /**
     * Saves the ingredients and recipe suggestion into the Derby database.
     */
    private void saveToDatabase(String ingredients, String recipe) {
        try {
            // Load the Derby JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
                String sql = "INSERT INTO RecipeSuggestions (ingredients, recipe, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, ingredients);
                    pstmt.setString(2, recipe);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Recipe saved successfully.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving to database: " + e.getMessage());
        }
    }
}
