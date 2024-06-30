/*
 * ecoCode - Java language - Provides rules to reduce the environmental footprint of your Java programs
 * Copyright © 2023 Green Code Initiative (https://www.ecocode.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.greencodeinitiative.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;

import java.lang.reflect.Field;
import java.util.List;


/**
 * This rule enforces the use of `FetchType.LAZY` for JPA associations to minimize energy consumption
 * and improve application performance. The rule targets the following JPA annotations:
 * <ul>
 *   <li>{@code @OneToMany}</li>
 *   <li>{@code @ManyToMany}</li>
 *   <li>{@code @OneToOne}</li>
 *   <li>{@code @ManyToOne}</li>
 * </ul>
 *
 * JPA provides two types of fetch strategies for associations: `FetchType.EAGER` and `FetchType.LAZY`.
 * - `FetchType.EAGER` loads the related entities immediately, which can result in significant performance
 *   overhead and increased energy consumption, especially when loading large collections or deeply nested
 *   entity graphs.
 * - `FetchType.LAZY` defers the loading of the related entities until they are accessed, reducing the initial
 *   load time, memory usage, and energy consumption.
 * <br>
 * <br>
 *
 * This rule scans for the above-mentioned JPA annotations and checks if the `fetch` attribute is set to
 * `FetchType.LAZY`. If the `fetch` attribute is either not defined or not set to `FetchType.LAZY`, the rule
 * issues a warning.
 * <br>
 * <br>
 *
 * Example:
 * <pre>
 * {@code
 * @Entity
 * public class Order {
 *     @OneToMany(fetch = FetchType.LAZY) // Correct usage
 *     private List<OrderItem> orderItems;
 *
 *     @ManyToOne // Noncompliant, should specify fetch = FetchType.LAZY
 *     private Customer customer;
 * }
 * }
 * </pre>
 *
 * Benefits of enforcing `FetchType.LAZY`:
 * <ul>
 *   <li>Reduces initial data load, decreasing memory usage and startup time.</li>
 *   <li>Prevents unnecessary data retrieval, lowering CPU and IO usage.</li>
 *   <li>Improves overall application performance and responsiveness.</li>
 *   <li>Minimizes energy consumption by avoiding the overhead of loading large amounts of data eagerly.</li>
 * </ul>
 *
 * By ensuring that JPA associations use `FetchType.LAZY`, developers can create more efficient and
 * scalable applications that are better suited for high-performance environments with lower energy footprints.
 *
 * @author Massil TAGUEMOUT - CGI FRANCE
 */
@Rule(key = "EC205")
public class ForceLazyFetchTypeForJPA extends IssuableSubscriptionVisitor {

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return List.of(Tree.Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        ClassTree classTree = (ClassTree) tree;
        for (Tree member : classTree.members()) {
            if (member.is(Tree.Kind.VARIABLE)) {
                VariableTree variableTree = (VariableTree) member;
                checkFetchTypeLazy(variableTree);
            }
        }
    }

    private void checkFetchTypeLazy(VariableTree variableTree) {
        List<AnnotationTree> annotations = variableTree.modifiers().annotations();
        for (AnnotationTree annotation : annotations) {
            String annotationName = annotation.annotationType().toString();
            if (isJpaAssociationAnnotation(annotationName)) {
                checkLazyFetchType(annotation, variableTree);
            }
        }
    }

    private boolean isJpaAssociationAnnotation(String annotationName) {
        return annotationName.equals("OneToMany") ||
                annotationName.equals("ManyToMany") ||
                annotationName.equals("OneToOne") ||
                annotationName.equals("ManyToOne");
    }

    private void checkLazyFetchType(AnnotationTree annotation, VariableTree variableTree) {
        boolean fetchTypeDefined = false;
        boolean fetchTypeLazy = false;

        for (ExpressionTree argument : annotation.arguments()) {
            if (argument.is(Tree.Kind.ASSIGNMENT)) {
                AssignmentExpressionTree assignment = (AssignmentExpressionTree) argument;
                if (assignment.variable().toString().equals("fetch")) {
                    fetchTypeDefined = true;
                    ExpressionTree expression = assignment.expression();
                    if (expression.is(Tree.Kind.MEMBER_SELECT)) {
                        MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) expression;
                        String expressionText = "";
                        try {
                            Field expressionField = memberSelect.getClass().getDeclaredField("expression");
                            expressionField.setAccessible(true);
                            expressionText = expressionField.get(memberSelect).toString();
                        } catch (NoSuchFieldException | IllegalAccessException ignored) {}

                        fetchTypeLazy = memberSelect.identifier().name().equals("LAZY") && expressionText.equals("FetchType");
                    }
                }
            }
        }

        if (!fetchTypeDefined || !fetchTypeLazy) {
            reportIssue(variableTree.simpleName(), "FetchType should be explicitly set to LAZY for JPA associations.");
        }
    }
}
