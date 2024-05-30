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

import java.util.Collections;
import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import com.google.re2j.Pattern;

@Rule(key = "EC24")
public class LimitDbQueryResults extends IssuableSubscriptionVisitor {

    protected static final String MESSAGERULE = "Try and limit the number of data returned for a single query (by using the LIMIT keyword for example)";

    private static final Pattern PATTERN = Pattern.compile("(LIMIT|TOP|ROW_NUMBER|FETCH FIRST|WHERE)", Pattern.CASE_INSENSITIVE);

    @Override
    public List<Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.STRING_LITERAL);
    }

    @Override
    public void visitNode(Tree tree) {
        String value = ((LiteralTree) tree).value().toUpperCase();
        if (value.contains("SELECT") && value.contains("FROM") && !PATTERN.matcher(value).find()) {
            reportIssue(tree, MESSAGERULE);
        }
    }

    
}
