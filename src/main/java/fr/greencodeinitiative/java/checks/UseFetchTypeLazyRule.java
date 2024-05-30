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
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;

@Rule(key = "EC205")
public class UseFetchTypeLazyRule extends BaseTreeVisitor implements JavaFileScanner {
    protected static final String ONE_TO_MANY = "OneToMany";
    protected static final String MESSAGE_RULE = "Avoid Using FetchType.EAGER instead of FetchType.LAZY on collections in JPA Entity";
    protected static final String JPA_ANNOTATION_WITHOUT_FETCH_TYPE_DETECTED = "JPA annotation without FetchType detected";
    protected static final String LAZY = "LAZY";
    protected static final String MANY_TO_MANY = "ManyToMany";
    private JavaFileScannerContext context;

    @Override
    public void scanFile(JavaFileScannerContext javaFileScannerContext) {
        this.context = javaFileScannerContext;
        // The call to the scan method on the root of the tree triggers the visit of the AST by this visitor
        scan(context.getTree());
    }

    @Override
    public void visitAnnotation(AnnotationTree annotationTree) {
        String annotationName = ((IdentifierTree) annotationTree.annotationType()).name();
        if (ONE_TO_MANY.equals(annotationName)
                || MANY_TO_MANY.equals(annotationName)) {
            boolean fetchExist = false;
            ExpressionTree fetchTypeArg = null;

            for (ExpressionTree argument : annotationTree.arguments()) {
                if (argument.is(Tree.Kind.ASSIGNMENT)) {
                    AssignmentExpressionTree assignmentInvocation = (AssignmentExpressionTree) argument;
                    if (assignmentInvocation.variable().toString().equals("fetch")) {
                        fetchExist = true;
                        fetchTypeArg = assignmentInvocation.expression();
                    }
                }
            }

            this.reportFetchTypeIssue(fetchExist,fetchTypeArg,annotationTree);
        }
        super.visitAnnotation(annotationTree);
    }

    private void reportFetchTypeIssue(boolean fetchExist, ExpressionTree fetchTypeArg, AnnotationTree annotationTree){
        if (!fetchExist) {
            context.reportIssue(this, annotationTree, JPA_ANNOTATION_WITHOUT_FETCH_TYPE_DETECTED);
        } else if (fetchTypeArg != null) {
            String fetchType = ((MemberSelectExpressionTree) fetchTypeArg).identifier().name();
            if (!LAZY.equals(fetchType.strip())) {
                context.reportIssue(this, annotationTree, MESSAGE_RULE);
            }
        }
    }
}
