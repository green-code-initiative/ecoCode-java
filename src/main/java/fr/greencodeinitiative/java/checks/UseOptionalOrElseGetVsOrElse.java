package fr.greencodeinitiative.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Rule(key = "XXX")
@DeprecatedRuleKey(repositoryKey = "greencodeinitiative-java", ruleKey = "XXX")
public class UseOptionalOrElseGetVsOrElse extends IssuableSubscriptionVisitor {

    private static final String MESSAGE_RULE = "Use optional orElseGet instead of orElse.";
    private final UseOptionalOrElseGetVsOrElseVisitor visitorInFile = new UseOptionalOrElseGetVsOrElseVisitor();

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(@Nonnull Tree tree) {
        tree.accept(visitorInFile);
    }

    private class UseOptionalOrElseGetVsOrElseVisitor extends BaseTreeVisitor {
        @Override
        public void visitMethodInvocation(MethodInvocationTree tree) {
            if (tree.methodSelect().is(Tree.Kind.MEMBER_SELECT) &&
                    Objects.requireNonNull(tree.methodSelect().firstToken()).text().equals("Optional")) {
                MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) tree.methodSelect();
                if (memberSelect.identifier().name().equals("orElse")) {
                    reportIssue(memberSelect, MESSAGE_RULE);
                }
            }
        }
    }
}
