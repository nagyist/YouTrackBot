package youtrackbot.server;

import com.google.wave.api.*;
import com.google.wave.api.event.*;

/**
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
public class YouTrackBotServlet extends AbstractRobot {
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
  public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
    Blip blip = event.getWavelet().reply("\nHi everybody!");
  }

  @Override
  public void onWaveletParticipantsChanged(WaveletParticipantsChangedEvent event) {
    for (String newParticipant: event.getParticipantsAdded()) {
      Blip blip = event.getWavelet().reply("\nHi : " + newParticipant);
    }
  }
}
