package youtrackbot.client;

import org.jetbrains.annotations.NonNls;

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
    private String state = "submitted";
    @NonNls
    private String affectsVersion;
    @NonNls
    private String fixedVersions;
    @NonNls
    private String fixedInBuild;
    private List<Object> attachments;
}
