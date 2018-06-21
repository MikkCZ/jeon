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

package io.github.ma1uta.matrix.client.model.sync;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ma1uta.matrix.Event;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Timeline.
 */
@ApiModel(description = "Timeline.")
public class Timeline {

    /**
     * List of events.
     */
    @ApiModelProperty("List of events.")
    private List<Event> events;

    /**
     * True if the number of events returned was limited by the limit on the filter.
     */
    @ApiModelProperty("True if the number of events returned was limited by the limit on the filter.")
    private Boolean limited;

    /**
     * A token that can be supplied to the from parameter of the rooms/{roomId}/messages endpoint.
     */
    @ApiModelProperty(name = "prev_batch", value = "A token that can be supplied to the from parameter of the "
        + "rooms/{roomId}/messages endpoint.")
    @JsonProperty("prev_batch")
    private String prevBatch;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Boolean getLimited() {
        return limited;
    }

    public void setLimited(Boolean limited) {
        this.limited = limited;
    }

    public String getPrevBatch() {
        return prevBatch;
    }

    public void setPrevBatch(String prevBatch) {
        this.prevBatch = prevBatch;
    }
}
