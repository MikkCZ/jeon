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

package io.github.ma1uta.identity.api;

import io.github.ma1uta.identity.service.impl.AbstractKeyService;
import io.github.ma1uta.identity.service.KeyService;
import io.github.ma1uta.jeon.exception.MatrixException;
import io.github.ma1uta.matrix.ErrorResponse;
import io.github.ma1uta.matrix.identity.api.KeyManagementApi;
import io.github.ma1uta.matrix.identity.model.key.KeyValidationResponse;
import io.github.ma1uta.matrix.identity.model.key.PublicKeyResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;

public class KeyManagement implements KeyManagementApi {

    private final KeyService keyService;

    public KeyManagement(KeyService keyService) {
        this.keyService = keyService;
    }

    public KeyService getKeyService() {
        return keyService;
    }

    @Override
    public PublicKeyResponse get(String keyId) {
        if (StringUtils.isBlank(keyId)) {
            throw new MatrixException(ErrorResponse.Code.M_NOT_FOUND, "Missing key.");
        }
        Pair<String, Certificate> pair = getKeyService().key(keyId)
            .orElseThrow(() -> new MatrixException(ErrorResponse.Code.M_NOT_FOUND, "Key not found", 404));
        PublicKeyResponse response = new PublicKeyResponse();
        response.setPublicKey(new String(pair.getValue().getPublicKey().getEncoded(), StandardCharsets.UTF_8));
        return response;
    }

    @Override
    public KeyValidationResponse valid(String publicKey) {
        return valid(publicKey, true);
    }

    @Override
    public KeyValidationResponse ephemeralValid(String publicKey) {
        return valid(publicKey, false);
    }

    protected KeyValidationResponse valid(String publicKey, boolean longTerm) {
        if (StringUtils.isBlank(publicKey)) {
            throw new MatrixException(AbstractKeyService.M_MISSING_KEY, "Missing key.");
        }
        KeyValidationResponse response = new KeyValidationResponse();
        response.setValid(getKeyService().valid(publicKey, false));
        return response;
    }
}
