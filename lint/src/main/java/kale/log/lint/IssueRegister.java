package kale.log.lint;

import java.util.Collections;
import java.util.List;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

/**
 * @author Kale
 * @date 2016/12/26
 */
public class IssueRegister extends IssueRegistry {

    @Override
    public List<Issue> getIssues() {
        return Collections.singletonList(
                LogDetector.ISSUE
        );
    }
}
