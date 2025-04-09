package com.explaincode.plugin.services;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service for analyzing code elements and providing detailed information about them.
 * This service is language-agnostic and works with any PSI element.
 */
public class CodeAnalyzerService {

    /**
     * Analyzes the given PSI element and its context to provide a detailed explanation.
     *
     * @param element The PSI element to analyze
     * @param selectedText The text that was selected by the user
     * @return A detailed explanation of the code
     */
    public String analyzeCode(@NotNull PsiElement element, @NotNull String selectedText) {
        StringBuilder explanation = new StringBuilder();
        explanation.append("Selected Code: ").append(selectedText).append("\n\n");

        // Find the containing element of interest
        PsiElement contextElement = findContextElement(element);
        
        if (contextElement != null) {
            explanation.append("Context: ").append(getElementType(contextElement)).append("\n\n");
            
            // Add generic information about the element
            analyzeGenericElement(contextElement, explanation);
        } else {
            // If we couldn't find a specific context, just provide basic information
            explanation.append("Element Type: ").append(element.getClass().getSimpleName()).append("\n");
            explanation.append("Text: ").append(element.getText()).append("\n");
        }

        return explanation.toString();
    }

    /**
     * Analyzes a generic PSI element and adds information to the explanation.
     */
    private void analyzeGenericElement(@NotNull PsiElement element, @NotNull StringBuilder explanation) {
        // Basic element information
        explanation.append("Element Type: ").append(element.getClass().getSimpleName()).append("\n");
        explanation.append("Text: ").append(element.getText().length() > 100 ? 
                element.getText().substring(0, 97) + "..." : element.getText()).append("\n");
        
        // File information
        PsiFile containingFile = element.getContainingFile();
        if (containingFile != null) {
            explanation.append("File: ").append(containingFile.getName()).append("\n");
        }
        
        // Text range information
        explanation.append("Text Range: ").append(element.getTextRange().getStartOffset())
                  .append(" - ").append(element.getTextRange().getEndOffset()).append("\n");
        
        // Add information about the element's children
        PsiElement[] children = element.getChildren();
        if (children.length > 0) {
            explanation.append("\nContains ").append(children.length).append(" child elements.\n");
            explanation.append("Child element types: ");
            for (int i = 0; i < Math.min(5, children.length); i++) {
                if (i > 0) explanation.append(", ");
                explanation.append(children[i].getClass().getSimpleName());
            }
            if (children.length > 5) {
                explanation.append(", ...");
            }
            explanation.append("\n");
        }
        
        // Add information about the element's parent
        PsiElement parent = element.getParent();
        if (parent != null) {
            explanation.append("\nParent Element Type: ").append(parent.getClass().getSimpleName()).append("\n");
        }
        
        // Add references information if available
        PsiReference[] references = element.getReferences();
        if (references.length > 0) {
            explanation.append("\nReferences: ").append(references.length).append("\n");
        }
        
        // Add navigation information
        if (element instanceof PsiNamedElement) {
            PsiNamedElement namedElement = (PsiNamedElement) element;
            String name = namedElement.getName();
            if (name != null) {
                explanation.append("Name: ").append(name).append("\n");
            }
        }
    }

    /**
     * Finds the most relevant containing element for the given PSI element.
     * This method is language-agnostic and works with any PSI element.
     */
    @Nullable
    private PsiElement findContextElement(@NotNull PsiElement element) {
        // Start with the element itself
        PsiElement current = element;
        
        // If the element is just whitespace or very small, look for a parent
        if (element.getText().trim().isEmpty() || element.getTextLength() < 3) {
            current = element.getParent();
        }
        
        // Find a meaningful parent element if the current one is too small
        while (current != null && current.getTextLength() < 10 && !(current instanceof PsiFile)) {
            current = current.getParent();
        }
        
        return current;
    }

    /**
     * Checks if the element is fully contained within the range of the container.
     */
    private boolean isElementWithinRange(@NotNull PsiElement element, @NotNull PsiElement container) {
        int elementStart = element.getTextRange().getStartOffset();
        int elementEnd = element.getTextRange().getEndOffset();
        int containerStart = container.getTextRange().getStartOffset();
        int containerEnd = container.getTextRange().getEndOffset();
        
        return elementStart >= containerStart && elementEnd <= containerEnd;
    }

    /**
     * Gets a human-readable description of the element type.
     * This method is language-agnostic and works with any PSI element.
     */
    private String getElementType(@NotNull PsiElement element) {
        String className = element.getClass().getSimpleName();
        
        // Remove common prefixes and suffixes to make the name more readable
        className = className.replace("Psi", "").replace("Impl", "");
        
        // Try to make the name more human-readable
        if (className.endsWith("Statement")) {
            return "Statement";
        } else if (className.endsWith("Expression")) {
            return "Expression";
        } else if (className.endsWith("Declaration")) {
            return "Declaration";
        } else if (className.endsWith("Element")) {
            return className.replace("Element", "");
        } else if (className.endsWith("Reference")) {
            return "Reference";
        } else if (className.endsWith("Literal")) {
            return "Literal";
        } else if (className.endsWith("File")) {
            return "File";
        } else if (className.endsWith("Comment")) {
            return "Comment";
        } else if (className.endsWith("Definition")) {
            return "Definition";
        } else if (className.endsWith("Function")) {
            return "Function";
        } else if (className.endsWith("Method")) {
            return "Method";
        } else if (className.endsWith("Class")) {
            return "Class";
        } else if (className.endsWith("Parameter")) {
            return "Parameter";
        } else if (className.endsWith("Variable")) {
            return "Variable";
        } else {
            return className;
        }
    }
}