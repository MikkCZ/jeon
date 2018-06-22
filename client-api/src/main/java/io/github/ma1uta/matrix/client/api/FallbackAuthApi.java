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

package io.github.ma1uta.matrix.client.api;

import static io.github.ma1uta.matrix.client.api.FallbackAuthApi.PATH;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * If a client does not recognize any or all login flows it can use the fallback login API.
 * <p/>
 * <a href="https://matrix.org/docs/spec/client_server/r0.3.0.html#login-fallback">Specification.</a>
 */
@Api(value = PATH, description = "If a client does not recognize any or all login flows it can use the fallback login API.")
@Path(PATH)
@Produces(MediaType.TEXT_HTML)
public interface FallbackAuthApi {

    /**
     * Fallback auth api url.
     */
    String PATH = "/_matrix";

    /**
     * Fallback login.
     *
     * @return This returns an HTML and JavaScript page which can perform the entire login process. The page will attempt to call the
     *     JavaScript function window.onLogin when login has been successfully completed.
     */
    @ApiOperation(value = "Fallback login.", response = String.class)
    @ApiResponses( {
        @ApiResponse(code = 200, message = "Login page for the fallback login.")
    })
    @GET
    @Path("/static/client/login")
    String staticLogin();

    /**
     * Clients cannot be expected to be able to know how to process every single login type. If a client does not know how to
     * handle a given login type, it can direct the user to a web browser with the URL of a fallback page which will allow the
     * user to complete that login step out-of-band in their web browser.
     *
     * @param auth            The type name of the stage it is attempting.
     * @param session         the ID of the session given by the homeserver.
     * @param servletRequest  servlet request.
     * @param servletResponse servlet response.
     * @return an HTML page which can perform this authentication stage. This page must use the following JavaScript when the
     *     authentication has been completed.
     */
    @ApiOperation(value = "Fallback login endpoint",
        notes = "If a client does not know how to handle a given login type, it can direct the user to a web browser with the "
            + "URL of a fallback page which will allow the user to complete that login step out-of-band in their web browser.",
        response = String.class)
    @ApiResponses( {
        @ApiResponse(code = 200, message = "n HTML page which can perform this authentication stage. This page must use the "
            + "following JavaScript when the authentication has been completed.")
    })
    @GET
    @Path("/client/r0/auth/{auth}/fallback/web")
    String auth(
        @ApiParam("The type name of the stage it is attempting.") @PathParam("auth") String auth,
        @ApiParam("the ID of the session given by the homeserver.") @QueryParam("session") String session,
        @Context HttpServletRequest servletRequest, @Context HttpServletResponse servletResponse);
}
