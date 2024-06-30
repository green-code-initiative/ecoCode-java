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
import org.sonar.java.model.ExpressionUtils;
import org.sonar.java.model.JavaTree;
import org.sonar.java.model.ModifiersUtils;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

@Rule(key = "EC82")
public class MakeVariableConstant extends IssuableSubscriptionVisitor {

    protected static final String MESSAGE_RULE = "Make variable constant";

    @Override
    public List<Kind> nodesToVisit() {
        return List.of(Kind.VARIABLE);
    }

    @Override
    public void visitNode(@Nonnull Tree tree) {
        VariableTree variableTree = (VariableTree) tree;
        System.out.println("Variable > " + variableTree.simpleName().name());
        System.out.println("   => isNotFinalAndNotStatic(variableTree) = " + isNotFinalAndNotStatic(variableTree));
        System.out.println("   => hasConstantInitializer(variableTree) = " + hasConstantInitializer(variableTree));
        System.out.println("   => usages = " + variableTree.symbol().usages().size());
        System.out.println("   => isNotReassigned = " + isNotReassigned(variableTree));

        if (isNotFinalAndNotStatic(variableTree) && hasConstantInitializer(variableTree) && isNotReassigned(variableTree)) {
            reportIssue(tree, MESSAGE_RULE);
        } else {
            super.visitNode(tree);
        }
    }

    private static boolean isNotReassigned(VariableTree variableTree) {
        return variableTree.symbol()
                .usages()
                .stream()
                .noneMatch(MakeVariableConstant::parentIsAssignment);
    }

    private static boolean parentIsAssignment(Tree tree) {
        return parentIsKind(tree, Kind.ASSIGNMENT);
    }

    private static boolean parentIsKind(Tree tree, Kind kind) {
        Tree parent = tree.parent();
        return parent != null && parent.is(kind);
    }

    private static boolean isNotFinalAndNotStatic(VariableTree variableTree) {
        return ModifiersUtils.hasNoneOf(variableTree.modifiers(), Modifier.FINAL, Modifier.STATIC);
    }

    // Reprise de https://github.com/SonarSource/sonar-java/blob/master/java-checks/src/main/java/org/sonar/java/checks/ConstantsShouldBeStaticFinalCheck.java
    private static boolean hasConstantInitializer(VariableTree variableTree) {
        ExpressionTree init = variableTree.initializer();
        if (init == null) {
            return false;
        }

        var deparenthesized = ExpressionUtils.skipParentheses(init);

        if (deparenthesized instanceof MethodReferenceTree && isInstanceIdentifier(((MethodReferenceTree) deparenthesized).expression())) {
            return false;
        }
        return !containsChildMatchingPredicate((JavaTree) deparenthesized, MakeVariableConstant::isNonStaticOrFinal);
    }

    private static boolean isNonStaticOrFinal(Tree tree) {
        switch (tree.kind()) {
            case METHOD_INVOCATION:
            case NEW_CLASS:
            case NEW_ARRAY:
            case ARRAY_ACCESS_EXPRESSION:
                return true;
            case IDENTIFIER:
                String name = ((IdentifierTree) tree).name();
                if ("super".equals(name) || "this".equals(name)) {
                    return true;
                } else {
                    var symbol = ((IdentifierTree) tree).symbol();
                    return symbol.isVariableSymbol() && !(symbol.isStatic() && symbol.isFinal());
                }
            default:
                return false;
        }
    }

    private static boolean isInstanceIdentifier(Tree expression) {
        if (!expression.is(Tree.Kind.IDENTIFIER)) {
            return false;
        }
        IdentifierTree identifierTree = (IdentifierTree) expression;
        return identifierTree.symbol().isStatic();
    }

    private static boolean containsChildMatchingPredicate(JavaTree tree, Predicate<Tree> predicate) {
        if (predicate.test(tree)) {
            return true;
        }
        if (!tree.isLeaf()) {
            for (Tree javaTree : tree.getChildren()) {
                if (javaTree != null && containsChildMatchingPredicate((JavaTree) javaTree, predicate)) {
                    return true;
                }
            }
        }
        return false;
    }


}
