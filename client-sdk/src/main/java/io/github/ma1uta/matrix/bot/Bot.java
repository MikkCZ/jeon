/*
 * Copyright sablintolya@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ma1uta.matrix.bot;

import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.filter.FilterData;
import io.github.ma1uta.matrix.client.model.filter.RoomEventFilter;
import io.github.ma1uta.matrix.client.model.filter.RoomFilter;
import io.github.ma1uta.matrix.client.model.room.RoomId;
import io.github.ma1uta.matrix.client.model.sync.InvitedRoom;
import io.github.ma1uta.matrix.client.model.sync.JoinedRoom;
import io.github.ma1uta.matrix.client.model.sync.LeftRoom;
import io.github.ma1uta.matrix.client.model.sync.Rooms;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;

/**
 * Matrix bot client.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> service.
 * @param <E> extra data.
 */
public class Bot<C extends BotConfig, D extends BotDao<C>, S extends Service<D>, E> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private final Map<String, Command<C, D, S, E>> commands;

    private String help;

    private final BotHolder<C, D, S, E> holder;

    public Bot(Client client, String homeserverUrl, String domain, String asToken, C config, S service,
               List<Class<? extends Command<C, D, S, E>>> commandsClasses) {
        MatrixClient matrixClient = new MatrixClient(homeserverUrl, domain, client, true, true, config.getTxnId());
        matrixClient.setAccessToken(asToken);
        matrixClient.setUserId(config.getUserId());
        matrixClient.setDeviceId(config.getDeviceId());
        this.holder = new BotHolder<>(matrixClient, service);
        this.holder.setConfig(config);
        this.commands = new HashMap<>(commandsClasses.size());
        StringBuilder sb = new StringBuilder("!help - show help.\n");
        commandsClasses.forEach(cl -> {
            try {
                Command<C, D, S, E> command = cl.newInstance();
                this.commands.put(command.name(), command);
                sb.append(command.usage()).append(" - ").append(command.help()).append("\n");
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Cannot create new instance of the command: " + cl.getCanonicalName(), e);
            }
        });
        this.help = sb.toString();
    }

    public Map<String, Command<C, D, S, E>> getCommands() {
        return commands;
    }

    public BotHolder<C, D, S, E> getHolder() {
        return holder;
    }

    public String getHelp() {
        return help;
    }

    @Override
    public void run() {
        try {
            LoopState state = LoopState.RUN;
            while (!LoopState.EXIT.equals(state)) {
                switch (getHolder().getConfig().getState()) {
                    case NEW:
                        state = newState();
                        break;
                    case REGISTERED:
                        state = registeredState();
                        break;
                    case JOINED:
                        state = joinedState();
                        break;
                    case DELETED:
                        state = deletedState();
                        break;
                    default:
                        LOGGER.error("Unknown state: " + getHolder().getConfig().getState());
                }
            }

        } catch (Throwable e) {
            LOGGER.error("Exception:", e);
            throw e;
        }

        getHolder().getShutdownListeners().forEach(ShutdownListener::shutdown);
    }

    /**
     * Save bot's config.
     *
     * @param dao DAO.
     */
    protected void saveData(BotHolder<C, D, S, E> holder, D dao) {
        C oldConfig = holder.getConfig();
        C newConfig = dao.save(oldConfig);
        holder.setConfig(newConfig);
    }

    /**
     * Main loop.
     *
     * @param loopAction state action.
     * @return next loop state.
     */
    protected LoopState loop(Function<SyncResponse, LoopState> loopAction) {
        C config = getHolder().getConfig();
        MatrixClient matrixClient = getHolder().getMatrixClient();
        SyncResponse sync = matrixClient.sync(config.getFilterId(), config.getNextBatch(), false, null, null);
        while (true) {
            LoopState nextState = loopAction.apply(sync);

            String nextBatch = sync.getNextBatch();
            getHolder().runInTransaction((holder, dao) -> {
                holder.getConfig().setNextBatch(nextBatch);
                saveData(holder, dao);
            });

            if (LoopState.NEXT_STATE.equals(nextState)) {
                return LoopState.NEXT_STATE;
            }

            if (Thread.currentThread().isInterrupted()) {
                return LoopState.EXIT;
            }

            sync = matrixClient.sync(config.getFilterId(), nextBatch, false, null, config.getTimeout());
        }
    }

    /**
     * Register a new bot.
     * <p/>
     * After registration setup a filter to receive only message events.
     *
     * @return {@link LoopState#NEXT_STATE} always. Move to the next state.
     */
    public LoopState newState() {
        getHolder().runInTransaction((holder, dao) -> {
            BotConfig config = holder.getConfig();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(Id.localpart(config.getUserId()));
            registerRequest.setInitialDeviceDisplayName(config.getDisplayName());
            registerRequest.setDeviceId(config.getDeviceId());

            MatrixClient matrixClient = holder.getMatrixClient();
            matrixClient.register(registerRequest);
            matrixClient.setDisplayName(config.getDisplayName());

            RoomEventFilter roomEventFilter = new RoomEventFilter();
            roomEventFilter.setTypes(Collections.singletonList(Event.EventType.ROOM_MESSAGE));
            RoomFilter roomFilter = new RoomFilter();
            roomFilter.setTimeline(roomEventFilter);
            FilterData filter = new FilterData();
            filter.setRoom(roomFilter);
            config.setFilterId(matrixClient.filter().uploadFilter(filter).getFilterId());

            config.setState(BotState.REGISTERED);

            saveData(holder, dao);
        });
        return LoopState.NEXT_STATE;
    }

    /**
     * Waiting to join.
     *
     * @return next loop state.
     */
    protected LoopState registeredState() {
        return loop(sync -> {
            Rooms rooms = sync.getRooms();

            if (rooms.getInvite().size() > 1) {
                LOGGER.error("Too many rooms to join");
                getHolder().runInTransaction((holder, dao) -> {
                    holder.getConfig().setState(BotState.DELETED);
                    saveData(holder, dao);
                });
                return LoopState.NEXT_STATE;
            }

            if (!rooms.getInvite().isEmpty()) {
                rooms.getInvite().forEach(this::joinRoom);
                return LoopState.NEXT_STATE;
            }

            return LoopState.RUN;
        });
    }

    /**
     * Logic of the joined state.
     *
     * @return next loop state.
     */
    protected LoopState joinedState() {
        return loop(sync -> {
            Rooms rooms = sync.getRooms();

            C config = getHolder().getConfig();
            JoinedRoom joinedRoom = rooms.getJoin().get(config.getRoomId());

            if (joinedRoom != null) {
                processJoinedRoom(joinedRoom);
            }

            MatrixClient matrixClient = getHolder().getMatrixClient();
            for (Map.Entry<String, LeftRoom> roomEntry : rooms.getLeave().entrySet()) {
                matrixClient.room().leaveRoom(roomEntry.getKey());
            }

            if (matrixClient.room().joinedRooms().isEmpty()) {
                getHolder().runInTransaction((holder, dao) -> {
                    holder.getConfig().setState(BotState.DELETED);
                    saveData(holder, dao);
                });
                return LoopState.NEXT_STATE;
            }

            return LoopState.RUN;
        });
    }

    /**
     * Join to room.
     *
     * @param roomId room id.
     * @param room   room to join.
     */
    public void joinRoom(String roomId, InvitedRoom room) {
        getHolder().runInTransaction((holder, dao) -> {
            RoomId response = holder.getMatrixClient().room().joinRoomByIdOrAlias(roomId);
            C config = holder.getConfig();
            if ((response.getErrcode() == null || response.getErrcode().trim().isEmpty())
                && (response.getError() == null || response.getError().trim().isEmpty())) {
                config.setRoomId(roomId);
                config.setState(BotState.JOINED);

                room.getInviteState().getEvents().stream().filter(e -> {
                    Object membership = e.getContent().get("membership");
                    return membership instanceof String
                        && Event.MembershipState.INVITE.equals(membership)
                        && Event.EventType.ROOM_MEMBER.equals(e.getType());
                }).map(Event::getSender).findFirst().ifPresent(config::setOwner);

                saveData(holder, dao);
            } else {
                throw new RuntimeException(
                    String.format("Failed join to room, errcode: ''%s'', error: ''%s''", response.getErrcode(), response.getError()));
            }
        });
    }

    /**
     * Delete bot.
     *
     * @return stop running.
     */
    public LoopState deletedState() {
        getHolder().runInTransaction((holder, dao) -> {
            holder.getMatrixClient().deactivate();
            dao.delete(holder.getConfig());
        });
        return LoopState.EXIT;
    }

    /**
     * Process commands.
     *
     * @param joinedRoom room's data.
     */
    public void processJoinedRoom(JoinedRoom joinedRoom) {
        String lastEvent = null;
        long lastOriginTs = 0;
        MatrixClient matrixClient = getHolder().getMatrixClient();
        for (Event event : joinedRoom.getTimeline().getEvents()) {
            Map<String, Object> content = event.getContent();
            if (Event.EventType.ROOM_MESSAGE.equals(event.getType())
                && !matrixClient.getUserId().equals(event.getSender())
                && Event.MessageType.TEXT.equals(content.get("msgtype"))
                && permit(event)) {
                String action = (String) content.get("body");
                try {
                    getHolder().runInTransaction((holder, dao) -> {
                        processAction(event, action);
                        getHolder().getConfig().setTxnId(matrixClient.getTxn().get());
                        saveData(holder, dao);
                    });
                } catch (Exception e) {
                    LOGGER.error(String.format("Cannot perform action '%s'", action), e);
                }
            }

            if (event.getOriginServerTs() != null && event.getOriginServerTs() > lastOriginTs) {
                lastOriginTs = event.getOriginServerTs();
                lastEvent = event.getEventId();
            }
        }
        if (lastEvent != null) {
            matrixClient.sendReceipt(getHolder().getConfig().getRoomId(), lastEvent);
        }
    }

    /**
     * Permission check.
     *
     * @param event event.
     * @return {@code true}, if process event, else {@code false}.
     */
    protected boolean permit(Event event) {
        C config = getHolder().getConfig();
        return config.getPolicy() == null
            || AccessPolicy.ALL.equals(config.getPolicy())
            || config.getOwner().equals(event.getSender());
    }

    /**
     * Process action.
     *
     * @param event   event.
     * @param content command.
     */
    protected void processAction(Event event, String content) {
        String[] arguments = content.trim().split("\\s");
        if (arguments[0].startsWith("!")) {
            Command<C, D, S, E> command = getCommands().get(arguments[0].substring(1));
            MatrixClient matrixClient = getHolder().getMatrixClient();
            C config = getHolder().getConfig();
            if (command != null) {
                command.invoke(getHolder(), event, Arrays.stream(arguments).skip(1).collect(Collectors.joining(" ")));
            } else {
                matrixClient.sendNotice(config.getRoomId(), getHelp());
            }
        }
    }
}