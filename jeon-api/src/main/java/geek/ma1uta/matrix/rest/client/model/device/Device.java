package geek.ma1uta.matrix.rest.client.model.device;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement
public class Device {

    private String deviceId;
    private String displayName;
    private String lastSeenIp;
    private Long listSeenTs;
}
