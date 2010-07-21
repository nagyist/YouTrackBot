package youtrackbot.server;

import com.google.wave.api.*;
import com.google.wave.api.event.BlipSubmittedEvent;
import com.google.wave.api.event.EventHandler;
import com.google.wave.api.event.FormButtonClickedEvent;
import com.google.wave.api.event.WaveletSelfAddedEvent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;
import java.util.SortedMap;
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
    @NonNls
    private final static String submitButtonName = "youtrackbot-button-submit";
    @NonNls
    private final static String trackerUrlInputFieldName = "youtrackbot-input-trackerurl";
    // This is for logging errors to the appengine logs.
    private static final Logger log = Logger.getLogger(YouTrackBotServlet.class.getName());

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
            String waveId = event.getWavelet().getWaveId().toString();
            YouTrackInstance instance = loadYouTrackInstance(waveId);
            if (instance == null) {
                instance = new YouTrackInstance();
            }
            StringBuilder issueUrl;
            Blip blip = event.getBlip();
            Pattern p = Pattern.compile(issueRegEx, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(blip.serialize().getContent());
            while (m.find()) {
                issueUrl = new StringBuilder(instance.getTrackerUrl());
                blip.first(m.group()).annotate("link/manual", issueUrl.append("/").append(instance.getIssuePath()).append("/").append(m.group(1)).toString());
            } // while
        }
    }

    @Override
    @Capability(contexts = Context.SELF)
    public void onFormButtonClicked(FormButtonClickedEvent event) {
        if (event.getButtonName().equals(submitButtonName)) {
            log.info("Submit button was clicked.");
            String waveId = event.getWavelet().getWaveId().toString();
            YouTrackInstance instance = loadYouTrackInstance(waveId);
            assert instance != null;
            SortedMap<Integer, Element> elements = event.getBlip().getElements();
            log.info("Got " + elements.size() + " elements.");
            for (int key : elements.keySet()) {
                Element e = elements.get(key);
                log.info("Checking element " + e);
                if (e.isFormElement() && e.getType() == ElementType.INPUT && e.getProperty("name").equals(trackerUrlInputFieldName)) {
                    instance.setTrackerUrl(e.getProperty("value"));
                    saveYouTrackInstance(instance);
                    log.info("Value : " + e.getProperty("value"));
                    break; // We don't need to go any further.
                }
            } // for
        }
        else {
            log.warning("Not our button!");
        }
    }

    @Override
    public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
        String waveId = event.getWavelet().getWaveId().toString();
        log.info("Robot was added to wave " + waveId + ".");
        YouTrackInstance instance = loadYouTrackInstance(waveId);
        if (instance != null) {
            log.info("Found saved instance for this wave (" + instance.getTrackerUrl() + ").");
        } else {
            log.info("No saved instance found for this wave.");
            instance = new YouTrackInstance();
            instance.setId(waveId);
            saveYouTrackInstance(instance);
        }
        Blip helloWorld = event.getWavelet().reply("\nHi everybody! Please use #XX-NN to link to existing tickets where XX is the shortcut for your project and NN the ticket number.\n\n");
        helloWorld.appendMarkup("<strong>Tracker URL</strong>");
        Element element = new Element(ElementType.INPUT);
        element.setProperty("name", trackerUrlInputFieldName);
        element.setProperty("type", "text");
        element.setProperty("maxlength", "254");
        element.setProperty("value", instance.getTrackerUrl());
        helloWorld.append(element);
        element = new Element(ElementType.BUTTON);
        element.setProperty("name", submitButtonName);
        element.setProperty("value", "Save");
        helloWorld.append(element);
        helloWorld.append("\n\n");
    }

//    @Override
//    public void onWaveletSelfRemoved(WaveletSelfRemovedEvent event) {
//        String waveId = event.getWavelet().getWaveId().toString();
//        log.info("Robot was removed from wave " + waveId + ".");
//        YouTrackInstance instance = loadYouTrackInstance(waveId);
//        if (instance != null) {
//            log.info("Found saved instance for this wave (" + instance.getTrackerUrl() + ").");
//            deleteYouTrackInstance(instance);
//        }
//    }
//
//    /**
//     * Delete a YouTrack instance from the database.
//     *
//     * @param instance The instance to delete.
//     */
//    private void deleteYouTrackInstance(@NotNull YouTrackInstance instance) {
//        PersistenceManager pm = YouTrackBotPMF.getPmfInstance().getPersistenceManager();
//        try {
//            pm.deletePersistent(pm.getObjectById(instance.getClass(), instance.getId()));
//            log.info("Deleted saved instance from database.");
//        } finally {
//            pm.close();
//        }
//    }

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
