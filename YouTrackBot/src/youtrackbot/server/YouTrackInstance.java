package youtrackbot.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import youtrackbot.client.YouTrackUser;

import javax.jdo.annotations.*;
import java.util.Date;

/**
 * A class describing a YouTrack server instance.
 *
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class YouTrackInstance {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Unique
    private String id;
    @Persistent
    private String trackerUrl = "http://youtrack.jetbrains.net";
    @Persistent
    private String issuePath = "issue";
    @Persistent(defaultFetchGroup = "true")
    private YouTrackUser login = null;
    @Persistent
    private Date removedFromWaveDate = null;

    /**
     * Default constructor.
     */
    public YouTrackInstance() {

    }

    /**
     * Overloaded constructor to create an instance from an old one.
     *
     * @param instance The instance providing the settings for the new instance.
     */
    public YouTrackInstance(@NotNull YouTrackInstance instance) {
        if (instance.getId() != null) {
            setId(instance.getId());
        }
        if (instance.getTrackerUrl() != null) {
            setTrackerUrl(instance.getTrackerUrl());
        }
        if (instance.getIssuePath() != null) {
            setIssuePath(instance.getIssuePath());
        }
        if (instance.getLogin() != null) {
            setLogin(instance.getLogin());
        }
        if (instance.getRemovedFromWaveDate() != null) {
            setRemovedFromWaveDate(instance.getRemovedFromWaveDate());
        }
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public String getTrackerUrl() {
        return trackerUrl;
    }

    public void setTrackerUrl(@NotNull String trackerUrl) {
        this.trackerUrl = trackerUrl;
    }

    public String getIssuePath() {
        return issuePath;
    }

    public void setIssuePath(@NotNull String issuePath) {
        this.issuePath = issuePath;
    }

    @Nullable
    public YouTrackUser getLogin() {
        return login;
    }

    public void setLogin(@NotNull YouTrackUser login) {
        this.login = login;
    }

    @Nullable
    public Date getRemovedFromWaveDate() {
        return removedFromWaveDate;
    }

    public void setRemovedFromWaveDate(Date removedFromWaveDate) {
        this.removedFromWaveDate = removedFromWaveDate;
    }
}
