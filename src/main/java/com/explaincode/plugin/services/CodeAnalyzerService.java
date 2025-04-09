package com.explaincode.plugin.services;

import com.explaincode.plugin.models.CodeAnalysisRequest;
import com.google.gson.Gson;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for analyzing code elements by making REST calls to an external API.
 * This service sends the selected code and its context to the API and returns the explanation.
 */
public class CodeAnalyzerService {

    private static final String API_URL = "https://api.codeexplain.xyz/api/v1/explain";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson gson = new Gson();

    /**
     * Analyzes the given PSI element and its context to provide a detailed explanation.
     * Makes an asynchronous REST call to an external API to get the explanation.
     *
     * @param element      The PSI element to analyze
     * @param selectedText The text that was selected by the user
     * @return A CompletableFuture that will complete with the explanation
     */
    public CompletableFuture<String> analyzeCodeAsync(@NotNull PsiElement element, @NotNull String selectedText) {
        // Get surrounding context
        String context = getSurroundingContext(element);

        // Create request object
        CodeAnalysisRequest requestObj = new CodeAnalysisRequest(selectedText, context, "markdown");

        // Serialize to JSON
        String jsonPayload = gson.toJson(requestObj);

        // Make the API call
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(30))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        // Parse the JSON response to extract the explanation
                        String responseBody = response.body();
                        String explanation = extractExplanationFromJson(responseBody);
                        return explanation != null ? explanation :
                                "Error: Could not extract explanation from API response: " + responseBody;
                    } else {
                        return "Error: Failed to get explanation from API. Status code: " + response.statusCode() +
                                "\nResponse: " + response.body();
                    }
                })
                .exceptionally(e -> "Error: Failed to get explanation from API. Exception: " + e.getMessage());
    }

    /**
     * Analyzes the given PSI element and its context to provide a detailed explanation.
     * Makes a REST call to an external API to get the explanation.
     * This is a blocking version of the method that waits for the result.
     *
     * @param element      The PSI element to analyze
     * @param selectedText The text that was selected by the user
     * @return A detailed explanation of the code
     * @deprecated Use analyzeCodeAsync instead for better UI responsiveness
     */
    @Deprecated
    public String analyzeCode(@NotNull PsiElement element, @NotNull String selectedText) {
        try {
            return analyzeCodeAsync(element, selectedText).get();
        } catch (InterruptedException | ExecutionException e) {
            return "Error: Failed to get explanation from API. Exception: " + e.getMessage();
        }
    }

    /**
     * Gets the surrounding context of the selected code.
     * This extracts a larger portion of code around the selected element.
     */
    private String getSurroundingContext(@NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile != null) {
            // Get the entire file content as context
            // In a more sophisticated implementation, you might want to get just
            // the surrounding function/method/class
            return containingFile.getText();
        }

        // If we can't get the file, just use the element's text
        return element.getText();
    }

    /**
     * Extracts the explanation value from a JSON response.
     *
     * @param jsonResponse The JSON response from the API
     * @return The extracted explanation value, or null if not found
     */
    private String extractExplanationFromJson(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return null;
        }

        try {
            // Parse the JSON response using Gson
            com.google.gson.JsonObject jsonObject = gson.fromJson(jsonResponse, com.google.gson.JsonObject.class);

            // Extract the explanation field
            if (jsonObject.has("explanation")) {
                return jsonObject.get("explanation").getAsString();
            }
        } catch (Exception e) {
            // Handle parsing errors
            return null;
        }

        return null;
    }

}
