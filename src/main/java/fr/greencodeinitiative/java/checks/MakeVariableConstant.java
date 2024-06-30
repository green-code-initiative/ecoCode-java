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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.*;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@Rule(key = "EC82")
@DeprecatedRuleKey(repositoryKey = "greencodeinitiative-java", ruleKey = "S82")
public class MakeVariableConstant extends IssuableSubscriptionVisitor {
    public static final String ERROR_MESSAGE = "A variable is never reassigned and can be made constant";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return List.of(Tree.Kind.VARIABLE);
    }

    @Override
    public void visitNode(Tree tree) {
        VariableTree variableTree = (VariableTree) tree;
        Symbol symbol = variableTree.symbol();
        if (symbol.isVariableSymbol() && !symbol.isFinal()) {
            boolean reassigned = isReassigned(symbol);
            if (!reassigned) {
                reportIssue(variableTree.simpleName(), ERROR_MESSAGE);
            }
        }
    }

    private boolean isReassigned(Symbol symbol) {
        Tree parent = symbol.declaration().parent();
        if (parent instanceof BlockTree) {
            return isReassignedInBlock(symbol, (BlockTree) parent);
        }
        return false;
    }

    private boolean isReassignedInBlock(Symbol symbol, BlockTree blockTree) {
        List<AssignmentExpressionTree> assignments = new ArrayList<>();
        List<StatementTree> body = blockTree.body();

        for (StatementTree statement : body) {
            if (statement.is(Tree.Kind.EXPRESSION_STATEMENT)) {
                ExpressionTree expression = ((ExpressionStatementTree) statement).expression();
                if (expression.is(Tree.Kind.ASSIGNMENT)) {
                    assignments.add((AssignmentExpressionTree) expression);
                }
            }
        }

        for (AssignmentExpressionTree assignment : assignments) {
            if (((IdentifierTree) assignment.variable()).symbol().equals(symbol)) {
                return true;
            }
        }
        return false;
    }
}
