package youtrackbot.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the youtrack issue class.
 *
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
public class YouTrackIssueTest {
    private YouTrackIssue issue;
    @Before
    public void setUp() throws Exception {
        this.issue = new YouTrackIssue();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetProject() throws Exception {
    }

    @Test
    public void testSetProject() throws Exception {
        issue.setProject("foo");
        org.junit.Assert.assertEquals("foo", issue.getProject());
    }

    @Test
    public void testGetAssignee() throws Exception {
    }

    @Test
    public void testSetAssignee() throws Exception {
    }

    @Test
    public void testGetSummary() throws Exception {
    }

    @Test
    public void testSetSummary() throws Exception {
    }

    @Test
    public void testGetDescription() throws Exception {
    }

    @Test
    public void testSetDescription() throws Exception {
    }

    @Test
    public void testGetPriority() throws Exception {
    }

    @Test
    public void testSetPriority() throws Exception {
    }

    @Test
    public void testGetType() throws Exception {
    }

    @Test
    public void testSetType() throws Exception {
    }

    @Test
    public void testGetSubsystem() throws Exception {
    }

    @Test
    public void testSetSubsystem() throws Exception {
    }

    @Test
    public void testGetState() throws Exception {
    }

    @Test
    public void testSetState() throws Exception {
    }

    @Test
    public void testGetAffectsVersion() throws Exception {
    }

    @Test
    public void testSetAffectsVersion() throws Exception {
    }

    @Test
    public void testGetFixedVersions() throws Exception {
    }

    @Test
    public void testSetFixedVersions() throws Exception {
    }

    @Test
    public void testGetFixedInBuild() throws Exception {
    }

    @Test
    public void testSetFixedInBuild() throws Exception {
    }

    @Test
    public void testGetAttachments() throws Exception {
    }

    @Test
    public void testSetAttachments() throws Exception {
    }
}
