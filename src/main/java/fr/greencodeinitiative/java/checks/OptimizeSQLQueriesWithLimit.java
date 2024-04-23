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
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@Rule(key = "EC24")
public class OptimizeSQLQueriesWithLimit extends IssuableSubscriptionVisitor {

    public static final String MESSAGE_RULE = "Optimize Database SQL Queries (Clause LIMIT / WHERE)";
    private static final Predicate<String> LIMIT_REGEXP =
            compile("limit", CASE_INSENSITIVE).asPredicate();
    private static final Predicate<String> SELECT_REGEXP =
            compile("select", CASE_INSENSITIVE).asPredicate();
    private static final Predicate<String> FROM_REGEXP =
            compile("from", CASE_INSENSITIVE).asPredicate();
    private static final Predicate<String> WHERE_REGEXP =
            compile("where", CASE_INSENSITIVE).asPredicate();

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return singletonList(Tree.Kind.STRING_LITERAL);
    }

    @Override
    public void visitNode(Tree tree) {
        String value = ((LiteralTree) tree).value();
        if (SELECT_REGEXP.test(value)
            && FROM_REGEXP.test(value)
            && !LIMIT_REGEXP.test(value)
            && !WHERE_REGEXP.test(value)
            ) {
                reportIssue(tree, MESSAGE_RULE);
        }
    }

}
