package io.github.ma1uta.matrix.client.model.push;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement
public class PushersResponse {

    private List<Pusher> pushers;
}