package youtrackbot;

import com.google.wave.api.*;
import com.google.wave.api.event.*;

/**
 * @author jens
 */
public class YouTrackBotServlet extends AbstractRobot {
    @Override
    protected String getRobotName() {
        return "YouTrackBot";
    }

    @Override
    protected String getRobotProfilePageUrl() {
        return "http://youtrackbot.appspot.com/profile.html";
    }

    @Override
    protected String getRobotAvatarUrl() {
        return "http://www.jetbrains.com/img/icons/YT_big.gif";
    }

    @Override
    public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
        Blip blip = event.getWavelet().reply("\nHi everybody!");
    }
}
