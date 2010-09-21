package youtrackbot.client;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * A class describing a YouTrack project.
 *
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
public class YouTrackProject {
    @NonNls
    private String projectId;
    @NonNls
    private String projectName;
    private int startingNumber = 1;
    @NonNls
    private String projectLeadLogin;
    @NonNls
    private String description;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(@NotNull String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(@NotNull String projectName) {
        this.projectName = projectName;
    }

    public int getStartingNumber() {
        return startingNumber;
    }

    public void setStartingNumber(int startingNumber) {
        this.startingNumber = startingNumber;
    }

    public String getProjectLeadLogin() {
        return projectLeadLogin;
    }

    public void setProjectLeadLogin(@NotNull String projectLeadLogin) {
        this.projectLeadLogin = projectLeadLogin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
