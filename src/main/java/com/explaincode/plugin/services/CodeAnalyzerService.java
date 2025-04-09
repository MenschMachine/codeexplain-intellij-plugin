package com.explaincode.plugin.services;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service for analyzing code elements and providing detailed information about them.
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

        // Find the containing element of interest (method, class, field, etc.)
        PsiElement contextElement = findContextElement(element);
        
        if (contextElement != null) {
            explanation.append("Context: ").append(getElementType(contextElement)).append("\n\n");
            
            // Add specific information based on the type of element
            if (contextElement instanceof PsiMethod) {
                analyzeMethod((PsiMethod) contextElement, explanation);
            } else if (contextElement instanceof PsiClass) {
                analyzeClass((PsiClass) contextElement, explanation);
            } else if (contextElement instanceof PsiField) {
                analyzeField((PsiField) contextElement, explanation);
            } else if (contextElement instanceof PsiVariable) {
                analyzeVariable((PsiVariable) contextElement, explanation);
            } else if (contextElement instanceof PsiStatement) {
                analyzeStatement((PsiStatement) contextElement, explanation);
            } else if (contextElement instanceof PsiExpression) {
                analyzeExpression((PsiExpression) contextElement, explanation);
            }
        } else {
            // If we couldn't find a specific context, just provide basic information
            explanation.append("Element Type: ").append(element.getClass().getSimpleName()).append("\n");
            explanation.append("Text: ").append(element.getText()).append("\n");
        }

        return explanation.toString();
    }

    /**
     * Finds the most relevant containing element for the given PSI element.
     */
    @Nullable
    private PsiElement findContextElement(@NotNull PsiElement element) {
        // Try to find the most specific context element
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method != null && isElementWithinRange(element, method)) {
            return method;
        }

        PsiField field = PsiTreeUtil.getParentOfType(element, PsiField.class);
        if (field != null && isElementWithinRange(element, field)) {
            return field;
        }

        PsiStatement statement = PsiTreeUtil.getParentOfType(element, PsiStatement.class);
        if (statement != null && isElementWithinRange(element, statement)) {
            return statement;
        }

        PsiExpression expression = PsiTreeUtil.getParentOfType(element, PsiExpression.class);
        if (expression != null && isElementWithinRange(element, expression)) {
            return expression;
        }

        PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (clazz != null && isElementWithinRange(element, clazz)) {
            return clazz;
        }

        return element;
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
     */
    private String getElementType(@NotNull PsiElement element) {
        if (element instanceof PsiMethod) {
            return "Method";
        } else if (element instanceof PsiClass) {
            return "Class";
        } else if (element instanceof PsiField) {
            return "Field";
        } else if (element instanceof PsiLocalVariable) {
            return "Local Variable";
        } else if (element instanceof PsiParameter) {
            return "Parameter";
        } else if (element instanceof PsiStatement) {
            return "Statement";
        } else if (element instanceof PsiExpression) {
            return "Expression";
        } else {
            return element.getClass().getSimpleName();
        }
    }

    /**
     * Analyzes a method and adds information to the explanation.
     */
    private void analyzeMethod(@NotNull PsiMethod method, @NotNull StringBuilder explanation) {
        explanation.append("Method Name: ").append(method.getName()).append("\n");
        
        // Return type
        PsiType returnType = method.getReturnType();
        if (returnType != null) {
            explanation.append("Return Type: ").append(returnType.getPresentableText()).append("\n");
        }
        
        // Parameters
        PsiParameter[] parameters = method.getParameterList().getParameters();
        if (parameters.length > 0) {
            explanation.append("Parameters:\n");
            for (PsiParameter parameter : parameters) {
                explanation.append("  - ").append(parameter.getName())
                          .append(": ").append(parameter.getType().getPresentableText()).append("\n");
            }
        } else {
            explanation.append("Parameters: None\n");
        }
        
        // Modifiers
        PsiModifierList modifierList = method.getModifierList();
        explanation.append("Modifiers: ");
        if (modifierList.hasModifierProperty(PsiModifier.PUBLIC)) explanation.append("public ");
        if (modifierList.hasModifierProperty(PsiModifier.PROTECTED)) explanation.append("protected ");
        if (modifierList.hasModifierProperty(PsiModifier.PRIVATE)) explanation.append("private ");
        if (modifierList.hasModifierProperty(PsiModifier.STATIC)) explanation.append("static ");
        if (modifierList.hasModifierProperty(PsiModifier.FINAL)) explanation.append("final ");
        if (modifierList.hasModifierProperty(PsiModifier.ABSTRACT)) explanation.append("abstract ");
        if (modifierList.hasModifierProperty(PsiModifier.SYNCHRONIZED)) explanation.append("synchronized ");
        explanation.append("\n");
        
        // Containing class
        PsiClass containingClass = method.getContainingClass();
        if (containingClass != null) {
            explanation.append("Containing Class: ").append(containingClass.getName()).append("\n");
        }
    }

    /**
     * Analyzes a class and adds information to the explanation.
     */
    private void analyzeClass(@NotNull PsiClass clazz, @NotNull StringBuilder explanation) {
        explanation.append("Class Name: ").append(clazz.getName()).append("\n");
        
        // Type (class, interface, enum, etc.)
        if (clazz.isInterface()) {
            explanation.append("Type: Interface\n");
        } else if (clazz.isEnum()) {
            explanation.append("Type: Enum\n");
        } else if (clazz.isAnnotationType()) {
            explanation.append("Type: Annotation\n");
        } else {
            explanation.append("Type: Class\n");
        }
        
        // Modifiers
        PsiModifierList modifierList = clazz.getModifierList();
        if (modifierList != null) {
            explanation.append("Modifiers: ");
            if (modifierList.hasModifierProperty(PsiModifier.PUBLIC)) explanation.append("public ");
            if (modifierList.hasModifierProperty(PsiModifier.PROTECTED)) explanation.append("protected ");
            if (modifierList.hasModifierProperty(PsiModifier.PRIVATE)) explanation.append("private ");
            if (modifierList.hasModifierProperty(PsiModifier.STATIC)) explanation.append("static ");
            if (modifierList.hasModifierProperty(PsiModifier.FINAL)) explanation.append("final ");
            if (modifierList.hasModifierProperty(PsiModifier.ABSTRACT)) explanation.append("abstract ");
            explanation.append("\n");
        }
        
        // Super class
        PsiClass superClass = clazz.getSuperClass();
        if (superClass != null && !superClass.getQualifiedName().equals("java.lang.Object")) {
            explanation.append("Extends: ").append(superClass.getName()).append("\n");
        }
        
        // Interfaces
        PsiClass[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            explanation.append("Implements: ");
            for (int i = 0; i < interfaces.length; i++) {
                if (i > 0) explanation.append(", ");
                explanation.append(interfaces[i].getName());
            }
            explanation.append("\n");
        }
        
        // Methods count
        PsiMethod[] methods = clazz.getMethods();
        explanation.append("Methods: ").append(methods.length).append("\n");
        
        // Fields count
        PsiField[] fields = clazz.getFields();
        explanation.append("Fields: ").append(fields.length).append("\n");
    }

    /**
     * Analyzes a field and adds information to the explanation.
     */
    private void analyzeField(@NotNull PsiField field, @NotNull StringBuilder explanation) {
        explanation.append("Field Name: ").append(field.getName()).append("\n");
        
        // Type
        PsiType type = field.getType();
        explanation.append("Type: ").append(type.getPresentableText()).append("\n");
        
        // Modifiers
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null) {
            explanation.append("Modifiers: ");
            if (modifierList.hasModifierProperty(PsiModifier.PUBLIC)) explanation.append("public ");
            if (modifierList.hasModifierProperty(PsiModifier.PROTECTED)) explanation.append("protected ");
            if (modifierList.hasModifierProperty(PsiModifier.PRIVATE)) explanation.append("private ");
            if (modifierList.hasModifierProperty(PsiModifier.STATIC)) explanation.append("static ");
            if (modifierList.hasModifierProperty(PsiModifier.FINAL)) explanation.append("final ");
            if (modifierList.hasModifierProperty(PsiModifier.VOLATILE)) explanation.append("volatile ");
            if (modifierList.hasModifierProperty(PsiModifier.TRANSIENT)) explanation.append("transient ");
            explanation.append("\n");
        }
        
        // Containing class
        PsiClass containingClass = field.getContainingClass();
        if (containingClass != null) {
            explanation.append("Containing Class: ").append(containingClass.getName()).append("\n");
        }
        
        // Initial value
        PsiExpression initializer = field.getInitializer();
        if (initializer != null) {
            explanation.append("Initial Value: ").append(initializer.getText()).append("\n");
        }
    }

    /**
     * Analyzes a variable and adds information to the explanation.
     */
    private void analyzeVariable(@NotNull PsiVariable variable, @NotNull StringBuilder explanation) {
        explanation.append("Variable Name: ").append(variable.getName()).append("\n");
        
        // Type
        PsiType type = variable.getType();
        explanation.append("Type: ").append(type.getPresentableText()).append("\n");
        
        // Modifiers
        PsiModifierList modifierList = variable.getModifierList();
        if (modifierList != null) {
            explanation.append("Modifiers: ");
            if (modifierList.hasModifierProperty(PsiModifier.FINAL)) explanation.append("final ");
            explanation.append("\n");
        }
        
        // Initial value
        PsiExpression initializer = variable.getInitializer();
        if (initializer != null) {
            explanation.append("Initial Value: ").append(initializer.getText()).append("\n");
        }
        
        // Context
        if (variable instanceof PsiLocalVariable) {
            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(variable, PsiMethod.class);
            if (containingMethod != null) {
                explanation.append("Defined in Method: ").append(containingMethod.getName()).append("\n");
            }
        } else if (variable instanceof PsiParameter) {
            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(variable, PsiMethod.class);
            if (containingMethod != null) {
                explanation.append("Parameter of Method: ").append(containingMethod.getName()).append("\n");
            }
        }
    }

    /**
     * Analyzes a statement and adds information to the explanation.
     */
    private void analyzeStatement(@NotNull PsiStatement statement, @NotNull StringBuilder explanation) {
        explanation.append("Statement Type: ").append(statement.getClass().getSimpleName().replace("Psi", "").replace("Impl", "")).append("\n");
        
        // Specific statement types
        if (statement instanceof PsiIfStatement) {
            analyzeIfStatement((PsiIfStatement) statement, explanation);
        } else if (statement instanceof PsiForStatement) {
            analyzeForStatement((PsiForStatement) statement, explanation);
        } else if (statement instanceof PsiWhileStatement) {
            analyzeWhileStatement((PsiWhileStatement) statement, explanation);
        } else if (statement instanceof PsiReturnStatement) {
            analyzeReturnStatement((PsiReturnStatement) statement, explanation);
        } else if (statement instanceof PsiDeclarationStatement) {
            analyzeDeclarationStatement((PsiDeclarationStatement) statement, explanation);
        } else if (statement instanceof PsiExpressionStatement) {
            analyzeExpressionStatement((PsiExpressionStatement) statement, explanation);
        }
        
        // Containing method
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(statement, PsiMethod.class);
        if (containingMethod != null) {
            explanation.append("Contained in Method: ").append(containingMethod.getName()).append("\n");
        }
    }

    /**
     * Analyzes an if statement and adds information to the explanation.
     */
    private void analyzeIfStatement(@NotNull PsiIfStatement ifStatement, @NotNull StringBuilder explanation) {
        PsiExpression condition = ifStatement.getCondition();
        if (condition != null) {
            explanation.append("Condition: ").append(condition.getText()).append("\n");
        }
        
        PsiStatement thenBranch = ifStatement.getThenBranch();
        if (thenBranch != null) {
            explanation.append("Then Branch: ").append(thenBranch.getText().length() > 50 ? 
                    thenBranch.getText().substring(0, 47) + "..." : thenBranch.getText()).append("\n");
        }
        
        PsiStatement elseBranch = ifStatement.getElseBranch();
        if (elseBranch != null) {
            explanation.append("Else Branch: ").append(elseBranch.getText().length() > 50 ? 
                    elseBranch.getText().substring(0, 47) + "..." : elseBranch.getText()).append("\n");
        }
    }

    /**
     * Analyzes a for statement and adds information to the explanation.
     */
    private void analyzeForStatement(@NotNull PsiForStatement forStatement, @NotNull StringBuilder explanation) {
        PsiStatement initialization = forStatement.getInitialization();
        if (initialization != null) {
            explanation.append("Initialization: ").append(initialization.getText()).append("\n");
        }
        
        PsiExpression condition = forStatement.getCondition();
        if (condition != null) {
            explanation.append("Condition: ").append(condition.getText()).append("\n");
        }
        
        PsiStatement update = forStatement.getUpdate();
        if (update != null) {
            explanation.append("Update: ").append(update.getText()).append("\n");
        }
        
        PsiStatement body = forStatement.getBody();
        if (body != null) {
            explanation.append("Body: ").append(body.getText().length() > 50 ? 
                    body.getText().substring(0, 47) + "..." : body.getText()).append("\n");
        }
    }

    /**
     * Analyzes a while statement and adds information to the explanation.
     */
    private void analyzeWhileStatement(@NotNull PsiWhileStatement whileStatement, @NotNull StringBuilder explanation) {
        PsiExpression condition = whileStatement.getCondition();
        if (condition != null) {
            explanation.append("Condition: ").append(condition.getText()).append("\n");
        }
        
        PsiStatement body = whileStatement.getBody();
        if (body != null) {
            explanation.append("Body: ").append(body.getText().length() > 50 ? 
                    body.getText().substring(0, 47) + "..." : body.getText()).append("\n");
        }
    }

    /**
     * Analyzes a return statement and adds information to the explanation.
     */
    private void analyzeReturnStatement(@NotNull PsiReturnStatement returnStatement, @NotNull StringBuilder explanation) {
        PsiExpression returnValue = returnStatement.getReturnValue();
        if (returnValue != null) {
            explanation.append("Return Value: ").append(returnValue.getText()).append("\n");
            explanation.append("Return Type: ").append(returnValue.getType() != null ? 
                    returnValue.getType().getPresentableText() : "unknown").append("\n");
        } else {
            explanation.append("Return Value: void\n");
        }
    }

    /**
     * Analyzes a declaration statement and adds information to the explanation.
     */
    private void analyzeDeclarationStatement(@NotNull PsiDeclarationStatement declarationStatement, @NotNull StringBuilder explanation) {
        PsiElement[] declaredElements = declarationStatement.getDeclaredElements();
        explanation.append("Declared Elements: ").append(declaredElements.length).append("\n");
        
        for (PsiElement element : declaredElements) {
            if (element instanceof PsiVariable) {
                PsiVariable variable = (PsiVariable) element;
                explanation.append("  - ").append(variable.getName())
                          .append(": ").append(variable.getType().getPresentableText());
                
                PsiExpression initializer = variable.getInitializer();
                if (initializer != null) {
                    explanation.append(" = ").append(initializer.getText());
                }
                explanation.append("\n");
            }
        }
    }

    /**
     * Analyzes an expression statement and adds information to the explanation.
     */
    private void analyzeExpressionStatement(@NotNull PsiExpressionStatement expressionStatement, @NotNull StringBuilder explanation) {
        PsiExpression expression = expressionStatement.getExpression();
        if (expression != null) {
            explanation.append("Expression: ").append(expression.getText()).append("\n");
            explanation.append("Expression Type: ").append(expression.getClass().getSimpleName().replace("Psi", "").replace("Impl", "")).append("\n");
            
            if (expression.getType() != null) {
                explanation.append("Result Type: ").append(expression.getType().getPresentableText()).append("\n");
            }
        }
    }

    /**
     * Analyzes an expression and adds information to the explanation.
     */
    private void analyzeExpression(@NotNull PsiExpression expression, @NotNull StringBuilder explanation) {
        explanation.append("Expression Type: ").append(expression.getClass().getSimpleName().replace("Psi", "").replace("Impl", "")).append("\n");
        
        if (expression.getType() != null) {
            explanation.append("Result Type: ").append(expression.getType().getPresentableText()).append("\n");
        }
        
        // Specific expression types
        if (expression instanceof PsiMethodCallExpression) {
            analyzeMethodCallExpression((PsiMethodCallExpression) expression, explanation);
        } else if (expression instanceof PsiReferenceExpression) {
            analyzeReferenceExpression((PsiReferenceExpression) expression, explanation);
        } else if (expression instanceof PsiBinaryExpression) {
            analyzeBinaryExpression((PsiBinaryExpression) expression, explanation);
        } else if (expression instanceof PsiLiteralExpression) {
            analyzeLiteralExpression((PsiLiteralExpression) expression, explanation);
        }
    }

    /**
     * Analyzes a method call expression and adds information to the explanation.
     */
    private void analyzeMethodCallExpression(@NotNull PsiMethodCallExpression methodCall, @NotNull StringBuilder explanation) {
        PsiReferenceExpression methodExpression = methodCall.getMethodExpression();
        explanation.append("Method Name: ").append(methodExpression.getReferenceName()).append("\n");
        
        PsiExpressionList argumentList = methodCall.getArgumentList();
        PsiExpression[] arguments = argumentList.getExpressions();
        explanation.append("Arguments: ").append(arguments.length).append("\n");
        
        for (int i = 0; i < arguments.length; i++) {
            PsiExpression argument = arguments[i];
            explanation.append("  - Arg ").append(i + 1).append(": ").append(argument.getText())
                      .append(" (").append(argument.getType() != null ? argument.getType().getPresentableText() : "unknown").append(")\n");
        }
        
        PsiMethod resolvedMethod = methodCall.resolveMethod();
        if (resolvedMethod != null) {
            PsiClass containingClass = resolvedMethod.getContainingClass();
            if (containingClass != null) {
                explanation.append("Defined in: ").append(containingClass.getQualifiedName()).append("\n");
            }
        }
    }

    /**
     * Analyzes a reference expression and adds information to the explanation.
     */
    private void analyzeReferenceExpression(@NotNull PsiReferenceExpression reference, @NotNull StringBuilder explanation) {
        explanation.append("Reference Name: ").append(reference.getReferenceName()).append("\n");
        
        PsiElement resolved = reference.resolve();
        if (resolved != null) {
            explanation.append("Resolves to: ").append(resolved.getClass().getSimpleName().replace("Psi", "").replace("Impl", "")).append("\n");
            
            if (resolved instanceof PsiVariable) {
                PsiVariable variable = (PsiVariable) resolved;
                explanation.append("Variable Type: ").append(variable.getType().getPresentableText()).append("\n");
            } else if (resolved instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) resolved;
                explanation.append("Method Return Type: ").append(method.getReturnType() != null ? 
                        method.getReturnType().getPresentableText() : "void").append("\n");
            }
        }
    }

    /**
     * Analyzes a binary expression and adds information to the explanation.
     */
    private void analyzeBinaryExpression(@NotNull PsiBinaryExpression binary, @NotNull StringBuilder explanation) {
        PsiJavaToken operationSign = binary.getOperationSign();
        explanation.append("Operation: ").append(operationSign.getText()).append("\n");
        
        PsiExpression lOperand = binary.getLOperand();
        explanation.append("Left Operand: ").append(lOperand.getText()).append("\n");
        
        PsiExpression rOperand = binary.getROperand();
        if (rOperand != null) {
            explanation.append("Right Operand: ").append(rOperand.getText()).append("\n");
        }
        
        if (binary.getType() != null) {
            explanation.append("Result Type: ").append(binary.getType().getPresentableText()).append("\n");
        }
    }

    /**
     * Analyzes a literal expression and adds information to the explanation.
     */
    private void analyzeLiteralExpression(@NotNull PsiLiteralExpression literal, @NotNull StringBuilder explanation) {
        explanation.append("Literal Value: ").append(literal.getValue()).append("\n");
        
        if (literal.getType() != null) {
            explanation.append("Literal Type: ").append(literal.getType().getPresentableText()).append("\n");
        }
    }
}