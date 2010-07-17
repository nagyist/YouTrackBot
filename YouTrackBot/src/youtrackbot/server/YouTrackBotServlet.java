package youtrackbot.server;

import com.google.wave.api.AbstractRobot;
import com.google.wave.api.Blip;
import com.google.wave.api.Context;
import com.google.wave.api.Element;
import com.google.wave.api.event.*;
import org.jetbrains.annotations.NonNls;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.logging.Logger;

/**
 * The main handler for our wave robot.
 * 
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
public class YouTrackBotServlet extends AbstractRobot {
    @NonNls
    private final static String robotId = "youtrackbot@appspot.com";
    @NonNls
    private final static String issueRegEx = "#(\\w+-\\d+)";
    private final static Pattern p = Pattern.compile(issueRegEx, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
    // This is for logging errors to the appengine logs.
    private static final Logger log = Logger.getLogger(YouTrackBotServlet.class.getName());
    //TODO Remove these and make them configurable per wave.
    private static String trackerUrl = "https://dev.wegtam.com";
    private final static String issuePath = "issue";

    @Override
    protected String getRobotName() {
        return "YouTrackBot";
    }

    @Override
    protected String getRobotAvatarUrl() {
        return "http://www.jetbrains.com/img/icons/YT_big.gif";
    }

    @Override
    protected String getRobotProfilePageUrl() {
        return "http://youtrackbot.appspot.com/";
    }

    @Override
    @EventHandler.Capability(contexts = {Context.SELF}, filter = issueRegEx)
    public void onBlipSubmitted(BlipSubmittedEvent event) {
        if (!event.getModifiedBy().equalsIgnoreCase(robotId)) {
            StringBuilder issueUrl;
            Blip blip = event.getBlip();
            Pattern p = Pattern.compile(issueRegEx, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(blip.serialize().getContent());
            while (m.find()) {
                issueUrl = new StringBuilder(trackerUrl);
                blip.first(m.group()).replace(issueUrl.append("/").append(issuePath).append("/").append(m.group(1)).toString());
            } // while
        }
    }

    @Override
    public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
        event.getWavelet().reply("\nHi everybody! Please use #XX-NN to link to existing tickets where XX is the shortcut for your project and NN the ticket number.");
    }
}
