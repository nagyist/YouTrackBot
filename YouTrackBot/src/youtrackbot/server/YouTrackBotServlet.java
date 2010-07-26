package youtrackbot.server;

import com.google.wave.api.*;
import com.google.wave.api.event.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import youtrackbot.client.YouTrackUser;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    // A debug flag for internal use.
    final static boolean DEBUG = true;
    @NonNls
    private final static String robotId = "youtrackbot@appspot.com";
    @NonNls
    private final static String issueRegEx = "#(\\w+-\\d+)";
    @NonNls
    private final static String submitButtonName = "youtrackbot-button-submit";
    @NonNls
    private final static String trackerUrlInputFieldName = "youtrackbot-input-trackerurl";
    @NonNls
    private final static String trackerUserLoginFieldName = "youtrackbot-input-trackerlogin";
    @NonNls
    private final static String trackerUserPasswordFieldName = "youtrackbot-input-trackerpassword";
    // This is for logging errors to the appengine logs.
    final static Logger log = Logger.getLogger(YouTrackBotServlet.class.getName());

    /**
     * Default constructor.
     */
    public YouTrackBotServlet() {
        super();
    }

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
            YouTrackUser login;
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
                if (e.isFormElement()) {
                    if (e.getType() == ElementType.INPUT) {
                        if (e.getProperty("name").equals(trackerUrlInputFieldName)) {
                            instance.setTrackerUrl(e.getProperty("value"));
                        }
                        if (e.getProperty("name").equals(trackerUserLoginFieldName)) {
                            login = instance.getLogin();
                            login.setLogin(e.getProperty("value"));
                            instance.setLogin(login);
                        }
                    }
                    if (e.getType() == ElementType.PASSWORD && e.getProperty("name").equals(trackerUserPasswordFieldName)) {
                        login = instance.getLogin();
                        login.setPassword(e.getProperty("value"));
                        instance.setLogin(login);
                    }
                }
            } // for
            appEngineSaveBugWorkaround(instance); //TODO Remove when the bug gets fixed.
        }
    }

    @Override
    public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
        String waveId = event.getWavelet().getWaveId().toString();
        if (DEBUG) log.info("Robot was added to wave " + waveId + ".");
        YouTrackInstance instance;
        try {
            instance = loadYouTrackInstance(waveId);
            if (instance.getRemovedFromWaveDate() != null) {
                instance.setRemovedFromWaveDate(null);
                appEngineSaveBugWorkaround(instance); //TODO Remove when the bug gets fixed.
            }
        } catch (JDOObjectNotFoundException e) {
            instance = new YouTrackInstance();
            instance.setId(waveId);
            saveYouTrackInstance(instance);
        }
        Blip helloWorld = event.getWavelet().reply("\nHi everybody! Please use #XX-NN to link to existing tickets where XX is the shortcut for your project and NN the ticket number.\n\n");
        helloWorld.appendMarkup("<em>To change the url for your YouTrack instance please enter it in the form below and click on the save button.</em>\n\n");
        helloWorld.appendMarkup("<strong>Tracker URL</strong>");
        Element element = new Element(ElementType.INPUT);
        element.setProperty("name", trackerUrlInputFieldName);
        element.setProperty("type", "text");
        element.setProperty("maxlength", "254");
        element.setProperty("value", instance.getTrackerUrl());
        helloWorld.append(element);
        YouTrackUser login = instance.getLogin();
        helloWorld.appendMarkup("<strong>Login</strong>");
        element = new Element(ElementType.INPUT);
        element.setProperty("name", trackerUserLoginFieldName);
        element.setProperty("type", "text");
        element.setProperty("maxlength", "50");
        if (login != null) {
            element.setProperty("value", login.getLogin());
        }
        helloWorld.append(element);
        helloWorld.appendMarkup("<strong>Password</strong>");
        element = new Element(ElementType.PASSWORD);
        element.setProperty("name", trackerUserPasswordFieldName);
        element.setProperty("maxlength", "254");
        if (login != null) {
            element.setProperty("value", login.getPassword());
        }
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
        PersistenceManager pm = YouTrackBotPMF.getPmfInstance().getPersistenceManager();
        Date removedAt = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (DEBUG) log.info("Robot was removed from wave " + waveId + " at " + format.format(removedAt) + ".");
        YouTrackInstance instance;
        try {
            instance = loadYouTrackInstance(waveId);
            instance.setRemovedFromWaveDate(removedAt);
            appEngineSaveBugWorkaround(instance); //TODO Remove when the bug gets fixed.
        } catch (JDOObjectNotFoundException e) {
            if (DEBUG) log.info("No saved instance found for this wave!");
        } finally {
            pm.close();
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
            pm.currentTransaction().begin();
            pm.deletePersistent(pm.getObjectById(instance.getClass(), instance.getId()));
            pm.currentTransaction().commit();
            if (DEBUG) log.info("Deleted saved instance from database.");
        } catch (Exception e) {
            pm.currentTransaction().rollback();
        } finally {
            pm.close();
        }
    }

    /**
     * Loads a stored YouTrack instance from the database.
     *
     * @param waveId The wave id which is the id of the YouTrack instance.
     * @return A YouTrack instance or {@code null}.
     * @throws javax.jdo.JDOObjectNotFoundException
     *          An exception is thrown if the object is not found in the database.
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

    /**
     * Due to a bug in the appengine our persistent objects do not get written to the
     * database after they are updated. So the changes are lost after the sessions ends.
     * A workaround is to delete the object and re-create it.
     *
     * @param instance The YouTrack instance to be updated.
     */
    private void appEngineSaveBugWorkaround(@NotNull YouTrackInstance instance) {
        PersistenceManager pm = YouTrackBotPMF.getPmfInstance().getPersistenceManager();
        YouTrackInstance updatedInstance;
        try {
            updatedInstance = pm.getObjectById(YouTrackInstance.class, instance.getId());
        } catch (JDOObjectNotFoundException e) {
            updatedInstance = new YouTrackInstance(instance);
        }
        try {
            pm.currentTransaction().begin();
            updatedInstance.setIssuePath(instance.getIssuePath());
            if (instance.getRemovedFromWaveDate() != null) {
                updatedInstance.setRemovedFromWaveDate(instance.getRemovedFromWaveDate());
            }
            updatedInstance.setTrackerUrl(instance.getTrackerUrl());
            pm.makePersistent(updatedInstance);
            pm.currentTransaction().commit();
        } catch (Exception e) {
            pm.currentTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            pm.close();
        }
        updatedInstance.setLogin(instance.getLogin());
    }
}
