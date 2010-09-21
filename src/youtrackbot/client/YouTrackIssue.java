package youtrackbot.client;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A class describing a YouTrack issue.
 *
 * @author Jens Jahnke <jan0sch@gmx.net>
 * @version $Id$
 */
public class YouTrackIssue {
    @NonNls
    private String project;
    @NonNls
    private String assignee;
    @NonNls
    private String summary;
    @NonNls
    private String description;
    @NonNls
    private String priority;
    @NonNls
    private String type;
    @NonNls
    private String subsystem;
    @NonNls
    private String state;
    @NonNls
    private String affectsVersion;
    @NonNls
    private String fixedVersions;
    @NonNls
    private String fixedInBuild;
    private List<Object> attachments;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAffectsVersion() {
        return affectsVersion;
    }

    public void setAffectsVersion(String affectsVersion) {
        this.affectsVersion = affectsVersion;
    }

    public String getFixedVersions() {
        return fixedVersions;
    }

    public void setFixedVersions(String fixedVersions) {
        this.fixedVersions = fixedVersions;
    }

    public String getFixedInBuild() {
        return fixedInBuild;
    }

    public void setFixedInBuild(String fixedInBuild) {
        this.fixedInBuild = fixedInBuild;
    }

    public List<Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Object> attachments) {
        this.attachments = attachments;
    }
}
