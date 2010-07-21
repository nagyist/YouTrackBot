package youtrackbot.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.jdo.annotations.*;

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
}
