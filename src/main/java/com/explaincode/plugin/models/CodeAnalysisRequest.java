package com.explaincode.plugin.models;

/**
 * Model class representing a code analysis request payload.
 * Used for serializing to JSON when making API requests.
 */
public class CodeAnalysisRequest {
    private String format;
    private String selectedCode;
    private String context;

    /**
     * Constructor for creating a code analysis request.
     *
     * @param selectedCode The code selected by the user
     * @param context      The surrounding context of the selected code
     */
    public CodeAnalysisRequest(String selectedCode, String context, String format) {
        this.selectedCode = selectedCode;
        this.context = context;
        this.format = format;
    }

    /**
     * Gets the selected code.
     *
     * @return The selected code
     */
    public String getSelectedCode() {
        return selectedCode;
    }

    /**
     * Sets the selected code.
     *
     * @param selectedCode The code selected by the user
     */
    public void setSelectedCode(String selectedCode) {
        this.selectedCode = selectedCode;
    }

    /**
     * Gets the context.
     *
     * @return The context
     */
    public String getContext() {
        return context;
    }

    /**
     * Sets the context.
     *
     * @param context The surrounding context of the selected code
     */
    public void setContext(String context) {
        this.context = context;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}