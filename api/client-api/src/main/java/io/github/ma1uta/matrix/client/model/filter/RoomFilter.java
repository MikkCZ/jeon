package io.github.ma1uta.matrix.client.model.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomFilter {

    private List<String> notRooms;
    private List<String> rooms;
    private RoomEventFilter ephemeral;
    private Boolean includeLeave;
    private RoomEventFilter state;
    private RoomEventFilter timeline;
    private RoomEventFilter accountData;
}