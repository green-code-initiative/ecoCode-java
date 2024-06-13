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

import java.util.Arrays;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

@Rule(key = "EC31")
@DeprecatedRuleKey(repositoryKey = "greencodeinitiative-java", ruleKey = "EC31")
public class PreferLighterImageFormats extends IssuableSubscriptionVisitor {

    private static final Logger LOGGER = Loggers.get(PreferLighterImageFormats.class);
    private static final String MESSAGE = "Consider using lighter image formats like .webp or .avif instead of .jpg, .jpeg or .png";

    private static final List<String> HEAVY_FORMATS = Arrays.asList(".jpg", ".jpeg", ".png");

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.STRING_LITERAL);
    }

    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.STRING_LITERAL)) {
            LiteralTree literalTree = (LiteralTree) tree;
            String value = literalTree.value();

            // Remove the quotes around the literal value
            if (value.length() > 2 && value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }

            for (String format : HEAVY_FORMATS) {
                if (value.endsWith(format)) {
                    reportIssue(literalTree, MESSAGE);
                    break;
                }
            }
        }
    }
}
