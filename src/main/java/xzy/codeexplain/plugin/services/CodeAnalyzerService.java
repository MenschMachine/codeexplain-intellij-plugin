package xzy.codeexplain.plugin.services;

import xzy.codeexplain.plugin.models.CodeAnalysisRequest;
import com.google.gson.Gson;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service for analyzing code elements by making REST calls to an external API.
 * This service sends the selected code and its context to the API and returns the explanation.
 * Implements AutoCloseable to properly clean up resources when the plugin is unloaded.
 */
public class CodeAnalyzerService implements AutoCloseable, Disposable {

    private static final Logger LOG = Logger.getInstance(CodeAnalyzerService.class);
    private final String API_URL = "https://api.codeexplain.xyz/api/v1/explain";
    private final HttpClient httpClient;
    private final Gson gson;
    private final ExecutorService executorService;

    public CodeAnalyzerService() {
        executorService = Executors.newCachedThreadPool();
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .executor(executorService)
                .build();
        gson = new Gson();
    }

    /**
     * Analyzes the given PSI element and its context to provide a detailed explanation.
     * Makes an asynchronous REST call to an external API to get the explanation.
     *
     * @param element      The PSI element to analyze
     * @param selectedText The text that was selected by the user
     * @return A CompletableFuture that will complete with the explanation
     */
    public CompletableFuture<String> analyzeCodeAsync(@NotNull PsiElement element, @NotNull String selectedText, @NotNull String context) {

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

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is called when the plugin is unloaded.
     */
    @Override
    public void close() throws Exception {
        LOG.info("Closing CodeAnalyzerService and releasing resources");
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Disposes of resources used by this service.
     * This method is called when the plugin is unloaded.
     */
    @Override
    public void dispose() {
        LOG.info("Disposing CodeAnalyzerService");
        try {
            close();
        } catch (Exception e) {
            LOG.error("Error disposing CodeAnalyzerService", e);
        }
    }
}
