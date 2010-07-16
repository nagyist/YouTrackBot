package youtrackbot;

import org.junit.Test;

/**
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
public class YouTrackBotServletTest extends YouTrackBotServlet {
    @Test
    public void testGetRobotName() throws Exception {
        org.junit.Assert.assertEquals("YouTrack Bot", getRobotName());
    }

    @Test
    public void testOnWaveletSelfAdded() throws Exception {
    }
}
