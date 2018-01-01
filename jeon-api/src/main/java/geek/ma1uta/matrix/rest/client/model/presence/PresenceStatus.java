package geek.ma1uta.matrix.rest.client.model.presence;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement
public class PresenceStatus {

    interface PresenceType {
        String ONLINE = "online";
        String OFFLINE = "offline";
        String UNAVAILABLE = "unavailable";
    }

    private String presence;
    private String statusMsg;
    private Long lastActiveAgo;
    private Boolean currentlyActive;
}
