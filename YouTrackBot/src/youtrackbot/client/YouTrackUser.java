package youtrackbot.client;

import org.jetbrains.annotations.NonNls;

/**
 * A class describing a YouTrack user account.
 *
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
public class YouTrackUser {
    @NonNls
    private String login;
    @NonNls
    private String fullName;
    @NonNls
    private String email;
    @NonNls
    private String jabber;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJabber() {
        return jabber;
    }

    public void setJabber(String jabber) {
        this.jabber = jabber;
    }
}
