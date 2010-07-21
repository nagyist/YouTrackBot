package youtrackbot.server;

import com.google.wave.api.*;
import com.google.wave.api.event.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
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
            YouTrackInstance instance;
            try {
                instance = loadYouTrackInstance(waveId);
            } catch (JDOObjectNotFoundException e) {
                instance = new YouTrackInstance();
                instance.setId(waveId);
                saveYouTrackInstance(instance);
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
            String waveId = event.getWavelet().getWaveId().toString();
            YouTrackInstance instance;
            try {
                instance = loadYouTrackInstance(waveId);
            } catch (JDOObjectNotFoundException e) {
                instance = new YouTrackInstance();
                instance.setId(waveId);
                saveYouTrackInstance(instance);
            }
            SortedMap<Integer, Element> elements = event.getBlip().getElements();
            for (int key : elements.keySet()) {
                Element e = elements.get(key);
                if (e.isFormElement() && e.getType() == ElementType.INPUT && e.getProperty("name").equals(trackerUrlInputFieldName)) {
                    instance.setTrackerUrl(e.getProperty("value"));
                    log.info("Value : " + e.getProperty("value") + " Instance : " + instance.getTrackerUrl());
                    break; // We don't need to go any further.
                }
            } // for
        }
    }

    @Override
    public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
        String waveId = event.getWavelet().getWaveId().toString();
        log.info("Robot was added to wave " + waveId + ".");
        YouTrackInstance instance;
        try {
            instance = loadYouTrackInstance(waveId);
        } catch (JDOObjectNotFoundException e) {
            instance = new YouTrackInstance();
            instance.setId(waveId);
            saveYouTrackInstance(instance);
        }
        Blip helloWorld = event.getWavelet().reply("\nHi everybody! Please use #XX-NN to link to existing tickets where XX is the shortcut for your project and NN the ticket number.\n\n");
        helloWorld.appendMarkup("<p><em>To change the url for your YouTrack instance please enter it in the form below and click on the save button.</em></p>");
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
        helloWorld.append("\n");
    }

    @Override
    public void onWaveletSelfRemoved(WaveletSelfRemovedEvent event) {
        String waveId = event.getWavelet().getWaveId().toString();
        log.info("Robot was removed from wave " + waveId + ".");
        YouTrackInstance instance;
        try {
            instance = loadYouTrackInstance(waveId);
            deleteYouTrackInstance(instance);
        } catch (JDOObjectNotFoundException e) {
            log.info("No saved instance found for this wave.");
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
     * @throws javax.jdo.JDOObjectNotFoundException An exception is thrown if the object is not found in the database.
     */
    @Nullable
    private YouTrackInstance loadYouTrackInstance(String waveId) throws JDOObjectNotFoundException {
        PersistenceManager pm = YouTrackBotPMF.getPmfInstance().getPersistenceManager();
        return pm.getObjectById(YouTrackInstance.class, waveId);
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
