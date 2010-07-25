package youtrackbot.client;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.jdo.annotations.*;

/**
 * A class describing a YouTrack user account.
 *
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class YouTrackUser {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Unique
    private String id;
    @Persistent
    @NonNls
    private String login;
    @Persistent
    @NonNls
    private String fullName;
    @Persistent
    @NonNls
    private String email;
    @Persistent
    @NonNls
    private String jabber;
    @Persistent
    @NonNls
    private String password;

    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public String getJabber() {
        return jabber;
    }

    public void setJabber(String jabber) {
        this.jabber = jabber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
