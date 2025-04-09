package com.explaincode.plugin.services;

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

    /**
     * Analyzes the given PSI element and its context to provide a detailed explanation.
     * Makes a REST call to an external API to get the explanation.
     *
     * @param element      The PSI element to analyze
     * @param selectedText The text that was selected by the user
     * @return A detailed explanation of the code
     */
    public String analyzeCode(@NotNull PsiElement element, @NotNull String selectedText) {
        try {
            // Get surrounding context
            String context = getSurroundingContext(element);

            // Create JSON payload
            String jsonPayload = String.format("{\"selectedCode\": %s, \"context\": %s}",
                    escapeJsonString(selectedText),
                    escapeJsonString(context));

            // Make the API call
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(
                    request, HttpResponse.BodyHandlers.ofString());

            HttpResponse<String> response = responseFuture.get();

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "Error: Failed to get explanation from API. Status code: " + response.statusCode() +
                        "\nResponse: " + response.body();
            }
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
     * Escapes a string for use in JSON.
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\').append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < ' ') {
                        String hex = Integer.toHexString(c);
                        sb.append("\\u");
                        for (int j = 0; j < 4 - hex.length(); j++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
