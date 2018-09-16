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

import io.github.ma1uta.matrix.Secured;
import io.github.ma1uta.matrix.client.model.admin.AdminResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * Gets information about a particular user.
 */
@Api(
    value = "Admin",
    description = "Gets information about a particular user"
)
@Path("/_matrix/client/r0/admin")
@Produces(MediaType.APPLICATION_JSON)
public interface AdminApi {

    /**
     * This API may be restricted to only be called by the user being looked up, or by a server admin. Server-local administrator
     * privileges are not specified in this document.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link AdminResponse}.
     * <p>Status code 200: The lookup was successful.</p>
     *
     * @param userId          Required. The user to look up.
     * @param servletRequest  Servlet request.
     * @param asyncResponse   Asynchronous response.
     * @param securityContext Security context.
     */
    @ApiOperation(
        value = "This API may be restricted to only be called by the user being looked up, or by a server admin. "
            + "Server-local administrator privileges are not specified in this document.",
        response = AdminResponse.class,
        authorizations = @Authorization("Authorization")
    )
    @ApiResponses( {
        @ApiResponse(code = 200, message = "The lookup was successful.")
    })
    @GET
    @Secured
    @Path("/whois/{userId}")
    void whois(
        @ApiParam(
            value = "The use to look up",
            required = true
        ) @PathParam("userId") String userId,

        @Context HttpServletRequest servletRequest,
        @Suspended AsyncResponse asyncResponse,
        @Context SecurityContext securityContext
    );
}
