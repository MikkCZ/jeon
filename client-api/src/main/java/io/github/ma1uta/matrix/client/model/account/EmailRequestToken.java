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

package io.github.ma1uta.matrix.client.model.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.json.bind.annotation.JsonbProperty;

/**
 * Request for the proxies the identity server API validate/email/requestToken.
 */
@Schema(
    description = "Request for the proxies the identity server API validate/email/requestToken."
)
public class EmailRequestToken {

    /**
     * Required. The hostname of the identity server to communicate with. May optionally include a port.
     */
    @Schema(
        description = "The hostname of the identity server to communicate with. May optionally include a port."
    )
    @JsonbProperty("id_server")
    private String idServer;

    /**
     * Required. A unique string generated by the client, and used to identify the validation attempt.
     * It must be a string consisting of the characters [0-9a-zA-Z.=_-]. Its length must not exceed 255 characters and it must not be empty.
     */
    @Schema(
        description = "A unique string generated by the client, and used to identify the validation attempt. It must be a string consisting"
            + " of the characters [0-9a-zA-Z.=_-]. Its length must not exceed 255 characters and it must not be empty."
    )
    @JsonbProperty("client_secret")
    private String clientSecret;

    /**
     * Required. The email address to validate.
     */
    @Schema(
        description = "The email address to validate"
    )
    private String email;

    /**
     * Required. The server will only send an email if the send_attempt is a number greater than the most recent one which it has seen,
     * scoped to that email + client_secret pair. This is to avoid repeatedly sending the same email in the case of request retries
     * between the POSTing user and the identity server. The client should increment this value if they desire a new email (e.g. a reminder)
     * to be sent.
     */
    @Schema(
        description = "The server will only send an email if the send_attempt is a number greater than the most recent one which it has"
            + " seen, scoped to that email + client_secret pair. This is to avoid repeatedly sending the same email in the case of request"
            + " retries between the POSTing user and the identity server.The client should increment this value if they desire a new"
            + " email(e.g.a reminder) to be sent ."
    )
    @JsonbProperty("send_attempt")
    private Long sendAttempt;

    /**
     * Optional. When the validation is completed, the identity server will redirect the user to this URL.
     */
    @Schema(
        description = "When the validation is completed, the identity server will redirect the user to this URL."
    )
    @JsonbProperty("next_link")
    private String nextLink;

    @JsonProperty("id_server")
    public String getIdServer() {
        return idServer;
    }

    public void setIdServer(String idServer) {
        this.idServer = idServer;
    }

    @JsonProperty("client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("send_attempt")
    public Long getSendAttempt() {
        return sendAttempt;
    }

    public void setSendAttempt(Long sendAttempt) {
        this.sendAttempt = sendAttempt;
    }

    @JsonProperty("next_link")
    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }
}
