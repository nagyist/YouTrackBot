package youtrackbot.server;

import org.jetbrains.annotations.NonNls;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A class managing our cronjobs.
 *
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
public class YouTrackBotCronServlet extends HttpServlet {
    // The maximum number of hours a robot configuration is kept after the bot is removed from it's wave.
    private final static int maximumIdleHours = 72;
    @NonNls
    private final static String cleanupDatabasePath = "/cleanupdatabase";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        if (path.equals(cleanupDatabasePath)) {
            cleanupDatabase();
        }
    }

    /**
     * Parse the database and remove all old ent
     */
    @SuppressWarnings("unchecked")
    public void cleanupDatabase() {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.HOUR, -maximumIdleHours);
        date = cal.getTime();
        PersistenceManager pm = YouTrackBotPMF.getPmfInstance().getPersistenceManager();
        Query query = pm.newQuery(YouTrackInstance.class);
        query.setFilter("removedFromWaveDate < dateParameter");
        query.declareParameters("Date dateParameter");
        query.declareImports("import java.util.Date");
        try {
            pm.currentTransaction().begin();
            List<YouTrackInstance> rows = (List<YouTrackInstance>) query.execute(date);
            if (rows.iterator().hasNext()) {
                YouTrackBotServlet.log.info("Cronjob is going to delete " + rows.size() + " instances from database.");
                for (YouTrackInstance i : rows) {
                    pm.deletePersistent(pm.getObjectById(YouTrackInstance.class, i.getId()));
                } // for
            }
            pm.currentTransaction().commit();
        } catch (Exception e) {
            pm.currentTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            query.closeAll();
            pm.close();
        }
    }
}
