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

package io.github.ma1uta.matrix.client.model.eventcontext;

import io.github.ma1uta.matrix.client.model.Event;

import java.util.List;

/**
 * Events that happened just before and after the specified event.
 */
public class EventContextResponse {

    /**
     * A token that can be used to paginate backwards with.
     */
    private String start;

    /**
     * A token that can be used to paginate forwards with.
     */
    private String end;

    /**
     * A list of room events that happened just before the requested event, in reverse-chronological order.
     */
    private List<Event> eventsBefore;

    /**
     * Details of the requested event.
     */
    private Event event;

    /**
     * A list of room events that happened just after the requested event, in chronological order.
     */
    private List<Event> eventsAfter;

    /**
     * The state of the room at the last event returned.
     */
    private Event state;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<Event> getEventsBefore() {
        return eventsBefore;
    }

    public void setEventsBefore(List<Event> eventsBefore) {
        this.eventsBefore = eventsBefore;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<Event> getEventsAfter() {
        return eventsAfter;
    }

    public void setEventsAfter(List<Event> eventsAfter) {
        this.eventsAfter = eventsAfter;
    }

    public Event getState() {
        return state;
    }

    public void setState(Event state) {
        this.state = state;
    }
}
