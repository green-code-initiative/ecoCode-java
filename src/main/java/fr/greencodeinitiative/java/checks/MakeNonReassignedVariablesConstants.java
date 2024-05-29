package fr.greencodeinitiative.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * This rule aims to promote the use of constants in Java code by identifying local variables
 * that are never reassigned after their initial assignment and recommending their conversion
 * to constants using the `final` modifier. By making variables constants, developers can
 * not only enhance code readability and maintainability but also improve the energy efficiency
 * of their applications.
 * <br>
 * <br>
 * The rule functions by analyzing local variable declarations within methods. If a variable is
 * declared and assigned a value, but never reassigned afterwards, it is a candidate for being
 * marked as `final`. This practice can help the compiler optimize the code, leading to potential
 * energy savings during execution.
 * <br>
 * <br>
 * Example:
 * <pre>
 * {@code
 * public class Example {
 *     public void exampleMethod() {
 *         int x = 10;
 *         String y = "example"; // Noncompliant, should be final
 *         x = 20; // This reassignment means x should not be final
 *     }
 * }
 * }
 * </pre>
 *
 * In the above example, the variable `y` is never reassigned after its initial assignment,
 * so it should be declared as `final`. The variable `x`, on the other hand, is reassigned,
 * so it should not be declared as `final`.
 * <br>
 * <br>
 *
 * By converting such non-reassigned variables to constants, the following benefits can be achieved:
 * <ul>
 *   <li><b>Enhanced Readability:</b> Declaring variables as `final` makes it clear that their value
 *       will not change after initialization, improving the readability of the code.</li>
 *   <li><b>Improved Maintainability:</b> Constants reduce the likelihood of accidental reassignments,
 *       making the code easier to maintain and less prone to bugs.</li>
 *   <li><b>Optimized Performance:</b> The compiler and JIT (Just-In-Time) compiler can apply more
 *       aggressive optimizations on `final` variables, potentially leading to more efficient bytecode
 *       and better runtime performance.</li>
 *   <li><b>Energy Efficiency:</b> Optimized code typically executes faster and consumes less energy,
 *       contributing to the overall energy efficiency of the application.</li>
 * </ul>
 *
 * This rule helps developers write more efficient, robust, and maintainable code by encouraging
 * the use of constants where appropriate.
 *
 * @author Massil TAGUEMOUT - CGI FRANCE
 */
@Rule(key = "EC82")
public class MakeNonReassignedVariablesConstants extends IssuableSubscriptionVisitor {

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return List.of(Tree.Kind.METHOD, Tree.Kind.CONSTRUCTOR);
    }

    @Override
    public void visitNode(Tree tree) {
        MethodTree methodTree = (MethodTree) tree;
        Set<String> reassignedVariables = new HashSet<>();
        Set<VariableTree> declaredVariables = new HashSet<>();

        // Collect reassigned and declared variables
        methodTree.accept(new BaseTreeVisitor() {
            @Override
            public void visitVariable(VariableTree variableTree) {
                declaredVariables.add(variableTree);
                super.visitVariable(variableTree);
            }

            @Override
            public void visitAssignmentExpression(AssignmentExpressionTree tree) {
                if (tree.variable().is(Tree.Kind.IDENTIFIER)) {
                    reassignedVariables.add(((IdentifierTree) tree.variable()).name());
                }
                super.visitAssignmentExpression(tree);
            }
        });

        // Check for variables that are declared but not reassigned and recommend making them final
        for (VariableTree variableTree : declaredVariables) {
            String variableName = variableTree.simpleName().name();
            if (!reassignedVariables.contains(variableName) && !variableTree.symbol().isFinal()) {
                reportIssue(variableTree.simpleName(), "Consider declaring '" + variableName + "' as a constant by using the 'final' modifier.");
            }
        }
    }

}
