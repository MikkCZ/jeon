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

package io.github.ma1uta.matrix.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * Protocol User.
 */
@ApiModel(description = "Protocol User.")
public class ProtocolUser {

    /**
     * A Matrix User ID represting a third party user.
     */
    @ApiModelProperty("A Matrix User ID represting a third party user.")
    private String userid;

    /**
     * The protocol ID that the third party location is a part of.
     */
    @ApiModelProperty("The protocol ID that the third party location is a part of.")
    private String protocol;

    /**
     * Information used to identify this third party location.
     */
    @ApiModelProperty("Information used to identify this third party location.")
    private Map<String, String> fields;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}