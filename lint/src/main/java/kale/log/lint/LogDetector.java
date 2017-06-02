package kale.log.lint;

import java.util.Collections;
import java.util.List;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import lombok.ast.AstVisitor;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.MethodInvocation;
import lombok.ast.Node;

/**
 * @author Kale
 * @date 2016/12/26
 */
public class LogDetector extends Detector implements Detector.JavaScanner {

    private static final String ISSUE_DESCRIPTION = "You should use our {AppLog}";

    public static final Issue ISSUE = Issue.create(
            "LogUseError",
            "避免使用Log/System.out.println",
            "使用{AppLog}，防止在正式包打印log",
            Category.SECURITY,
            5,
            Severity.ERROR,
            new Implementation(LogDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(MethodInvocation.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        return new ForwardingAstVisitor() {
            @Override
            public boolean visitMethodInvocation(MethodInvocation node) {

                if (node.toString().startsWith("System.out.println")) {
                    context.report(ISSUE, node, context.getLocation(node), ISSUE_DESCRIPTION);
                    return true;
                }

                JavaParser.ResolvedNode resolve = context.resolve(node);
                if (resolve instanceof JavaParser.ResolvedMethod) {
                    JavaParser.ResolvedMethod method = (JavaParser.ResolvedMethod) resolve;
                    JavaParser.ResolvedClass containingClass = method.getContainingClass();
                    if (containingClass.matches("android.util.Log")) {
                        context.report(ISSUE, node, context.getLocation(node), ISSUE_DESCRIPTION);
                        return true;
                    }
                }
                return super.visitMethodInvocation(node);
            }
        };
    }
}
