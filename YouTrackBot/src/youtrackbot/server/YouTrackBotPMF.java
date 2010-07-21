package youtrackbot.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * A singleton for the persistence manager factory.
 *
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
public enum YouTrackBotPMF {
    INSTANCE;
    private final static PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");

    public static PersistenceManagerFactory getPmfInstance() {
        return pmfInstance;
    }
}
