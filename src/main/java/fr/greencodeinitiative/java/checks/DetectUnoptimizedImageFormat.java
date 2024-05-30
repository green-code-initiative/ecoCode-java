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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "EC203")
public class DetectUnoptimizedImageFormat extends IssuableSubscriptionVisitor {

    protected static final String MESSAGERULE = "Detect unoptimized image format";
    protected static final Pattern IMG_EXTENSION = Pattern.compile("\\.(bmp|ico|tiff|webp|png|jpg|jpeg|jfif|pjpeg|pjp|gif|avif|apng)");

    private final DetectUnoptimizedImageFormatVisitor visitor = new DetectUnoptimizedImageFormatVisitor();

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(
                Tree.Kind.STRING_LITERAL
        );
    }

    @Override
    public void visitNode(@Nonnull Tree tree) {
        tree.accept(visitor);
    }

    private class DetectUnoptimizedImageFormatVisitor extends BaseTreeVisitor {

        @Override
        public void visitLiteral(@Nonnull LiteralTree tree) {
            final String strValue = tree.token().text();
            final Matcher matcher = IMG_EXTENSION.matcher(strValue);
            if (matcher.find()) {
                reportIssue(tree, MESSAGERULE);
            } else {
                super.visitLiteral(tree);
            }
        }
    }
}
