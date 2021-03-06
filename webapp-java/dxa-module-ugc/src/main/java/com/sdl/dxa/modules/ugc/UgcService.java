package com.sdl.dxa.modules.ugc;

import com.sdl.delivery.ugc.client.comment.UgcCommentApi;
import com.sdl.delivery.ugc.client.comment.impl.SimpleCommentsFilter;
import com.sdl.dxa.modules.ugc.data.Comment;
import com.sdl.dxa.modules.ugc.data.User;
import com.sdl.web.ugc.Status;
import com.sdl.webapp.common.util.TcmUtils;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * <p>Service providing methods to  create and retrieve comments</p>
 */
@Service
@Slf4j
public class UgcService {

    private static final int maximumThreadsDepth = -1;

    @Autowired
    private UgcCommentApi ugcCommentApi;

    //Todo: use UgcVoteCommentApi implementation when it becomes available

    @Autowired
    public UgcService() {
    }

    /**
     * retrieves a list of {@link Comment}  items for a given page
     *
     * @param publicationId Publication Id
     * @param pageId        Page Id
     * @param descending    Order
     * @param statuses      Limit to specific statuses
     * @param top           maximum number of comments to show
     * @param skip          number of comments to skip
     * @return List of {@link Comment}
     */
    public List<Comment> getComments(int publicationId, int pageId, boolean descending, Integer[] statuses, int top, int skip) {
        final List<Status> statusStatuses = new ArrayList<>();
        Arrays.stream(statuses).forEach(status -> statusStatuses.add(Status.getStatusForId(status)));
        final SimpleCommentsFilter filter = new SimpleCommentsFilter()
                .withTop(top)
                .withSkip(skip)
                .withDepth(maximumThreadsDepth)
                .withStatuses(statusStatuses);

        return convert(ugcCommentApi.retrieveThreadedComments(TcmUtils.buildPageTcmUri(publicationId, pageId), filter, descending, true));
    }

    /**
     * Post {@link Comment} for a given page
     *
     * @param publicationId Publication Id
     * @param pageId        Page Id
     * @param username      User name
     * @param email         Email address
     * @param content       Post content
     * @param parentId      parent
     * @param metadata      Meta data
     * @return {@link Comment}
     */
    public Comment postComment(int publicationId, int pageId, String username, String email, String content,
                               int parentId, Map<String, String> metadata) {

        try {
            final ClaimStore claimStore = AmbientDataContext.getCurrentClaimStore();
            if (claimStore != null) {
                claimStore.put(new URI("taf:claim:contentdelivery:webservice:user"), username);
                claimStore.put(new URI("taf:claim:contentdelivery:webservice:post:allowed"), true);
            }
        } catch (URISyntaxException e) {
            log.error("Error while Storing Claims", e);
        }
        return convert(
                ugcCommentApi.postComment(TcmUtils.buildPageTcmUri(publicationId, pageId), username, email, content, parentId, metadata));
    }

    private List<Comment> convert(List<com.sdl.delivery.ugc.client.odata.edm.Comment> comments) {
        final List<Comment> convertedComments = new ArrayList<>();
        comments.forEach(comment -> convertedComments.add(convert(comment)));

        return convertedComments;
    }

    private Comment convert(com.sdl.delivery.ugc.client.odata.edm.Comment comment) {
        if (comment == null) {
            return null;
        }
        final Comment c = new Comment();
        c.setId(comment.getIdLong());
        if (comment.getParent() != null) {
            c.setParentId(comment.getParent().getIdLong());
        }
        c.setItemId(comment.getItemId());
        c.setItemType(comment.getItemType());
        c.setItemPublicationId(comment.getItemPublicationId());
        c.setContent(comment.getContent());
        c.setRating(comment.getScore());
        c.setMetadata(comment.getMetadata());
        if (comment.getUser() != null) {
            c.setUser(convert(comment.getUser()));
        }
        if (comment.getCreationDate() != null) {
            c.setCreationDate(convert(comment.getCreationDate()));
        }
        if (comment.getLastModifiedDate() != null) {
            c.setLastModifiedDate(convert(comment.getLastModifiedDate()));
        }
        c.setChildren(convert(comment.getChildren()));

        return c;
    }

    private DateTime convert(ZonedDateTime zonedDateTime) {
        return  new DateTime(
                zonedDateTime.toInstant().toEpochMilli(),
                DateTimeZone.forTimeZone(TimeZone.getTimeZone(zonedDateTime.getZone())));
    }

    private User convert(com.sdl.delivery.ugc.client.odata.edm.User user) {
        final User u = new User();
        u.setId(user.getId());
        u.setExternalId(user.getExternalId());
        u.setName(user.getName());
        u.setEmailAddress(user.getEmailAddress());
        return u;
    }

}
