# AI Based Recipe Suggestion App using Java
   The AI-Based Recipe Suggestion App is a Java application that generates recipe ideas from user-provided ingredients. Using the Google Gemini API, it retrieves personalized recipes and saves suggestions in an Apache Derby database, offering a user-friendly interface for easy exploration of cooking inspirations.

# Technologies Used

  * Java: The primary programming language for developing the application.
  * Apache Derby: A lightweight, embeddable database used for storing recipe suggestions and ingredients.
  * Google Gemini API: An API for generating recipe ideas based on user input.
  * Swing: A Java library for building the graphical user interface (GUI) of the application.
  * OkHttp: A Java library for making HTTP requests to the Google Gemini API.
  * JSON: Data format used for exchanging data between the application and the API.

# Description of the AI-Based Recipe Suggestion App
  * User Input: User enters ingredients in a text area.
  * Submit Action: User clicks the "Get Recipe Suggestions" button.
  * Validation: Check if the input is empty. If empty, show a message to enter ingredients. If not empty, proceed to the next step.
  * API Request: Create a JSON payload with the entered ingredients. Send a POST request to the Google Gemini API using OkHttp.
  * API Response Handling: Receive the API response. If successful, extract the recipe suggestions. If failed, display an error message.
  * Display Suggestions: Show the recipe suggestions in the response area.
  * Save to Database: Save the entered ingredients and recipe suggestions to the Apache Derby database.
  * Clear Action (if user clicks "Clear"): Clear the input and response areas.





