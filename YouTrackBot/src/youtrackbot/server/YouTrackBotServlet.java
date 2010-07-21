package youtrackbot.server;

import com.google.wave.api.AbstractRobot;
import com.google.wave.api.Blip;
import com.google.wave.api.Context;
import com.google.wave.api.event.BlipSubmittedEvent;
import com.google.wave.api.event.EventHandler;
import com.google.wave.api.event.WaveletSelfAddedEvent;
import com.google.wave.api.event.WaveletSelfRemovedEvent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                blip.first(m.group()).annotate("link/manual", issueUrl.append("/").append(issuePath).append("/").append(m.group(1)).toString());
            } // while
        }
    }

    @Override
    public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
        String waveId = event.getWavelet().getWaveId().toString();
        log.info("Robot was added to wave " + waveId + ".");
        YouTrackInstance instance = loadYouTrackInstance(waveId);
        if (instance != null) {
            log.info("Found saved instance for this wave (" + instance.getTrackerUrl() + ").");
        }
        else {
            log.info("No saved instance found for this wave.");
            instance = new YouTrackInstance();
            instance.setId(waveId);
            saveYouTrackInstance(instance);
        }
        event.getWavelet().reply("\nHi everybody! Please use #XX-NN to link to existing tickets where XX is the shortcut for your project and NN the ticket number. To set the url of your YouTrack instance please use #trackerUrl=http://www.example.com.");
    }

    @Override
    public void onWaveletSelfRemoved(WaveletSelfRemovedEvent event) {
        String waveId = event.getWavelet().getWaveId().toString();
        log.info("Robot was removed from wave " + waveId + ".");
        YouTrackInstance instance = loadYouTrackInstance(waveId);
        if (instance != null) {
            log.info("Found saved instance for this wave (" + instance.getTrackerUrl() + ").");
            deleteYouTrackInstance(instance);
        }
    }

    /**
     * Delete a YouTrack instance from the database.
     *
     * @param instance The instance to delete.
     */
    private void deleteYouTrackInstance(@NotNull YouTrackInstance instance) {
        PersistenceManager pm = YouTrackBotPMF.getPmfInstance().getPersistenceManager();
        try {
            pm.deletePersistent(pm.getObjectById(instance.getClass(), instance.getId()));
            log.info("Deleted saved instance from database.");
        } finally {
            pm.close();
        }
    }

    /**
     * Loads a stored YouTrack instance from the database.
     *
     * @param waveId The wave id which is the id of the YouTrack instance.
     * @return A YouTrack instance or {@code null}.
     */
    @Nullable
    private YouTrackInstance loadYouTrackInstance(String waveId) {
        YouTrackInstance instance = null;
        PersistenceManager pm = YouTrackBotPMF.getPmfInstance().getPersistenceManager();
        Query query = pm.newQuery(YouTrackInstance.class);
        query.setFilter("id == idParameter");
        query.declareParameters("String idParameter");
        List<YouTrackInstance> results = (List<YouTrackInstance>) query.execute(waveId);
        if (results.size() == 1) {
            instance = results.get(0);
        }
        pm.close();
        return instance;
    }

    /**
     * Stores a YouTrack instance into the database.
     *
     * @param instance The instance to store.
     */
    private void saveYouTrackInstance(@NotNull YouTrackInstance instance) {
        PersistenceManager pm = YouTrackBotPMF.getPmfInstance().getPersistenceManager();
        try {
            pm.makePersistent(instance);
        } finally {
            pm.close();
        }
    }
}
