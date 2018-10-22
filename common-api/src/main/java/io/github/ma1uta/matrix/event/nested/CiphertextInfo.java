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

package io.github.ma1uta.matrix.event.nested;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * Cipher text info.
 */
@Schema(
    description = "Cipher text info."
)
public class CiphertextInfo {

    /**
     * The encrypted payload.
     */
    @Schema(
        description = "The encrypted payload."
    )
    private String body;

    /**
     * The Olm message type.
     */
    @Schema(
        description = "The Olm message type."
    )
    private Long type;

    public CiphertextInfo() {
    }

    public CiphertextInfo(Map props) {
        this.body = (String) props.get("body");
        this.type = (Long) props.get("type");
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }
}