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

package io.github.ma1uta.matrix.bot.command;

import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotConfig;
import io.github.ma1uta.matrix.bot.BotDao;
import io.github.ma1uta.matrix.bot.BotHolder;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.matrix.bot.PersistentService;
import io.github.ma1uta.matrix.client.MatrixClient;

/**
 * Set new name.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> bot service.
 * @param <E> extra data.
 */
public class NewName<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> implements Command<C, D, S, E> {
    @Override
    public String name() {
        return "name";
    }

    @Override
    public void invoke(BotHolder<C, D, S, E> holder, Event event, String arguments) {
        MatrixClient matrixClient = holder.getMatrixClient();
        C config = holder.getConfig();
        if (arguments == null || arguments.trim().isEmpty()) {
            matrixClient.event().sendNotice(config.getRoomId(), "Usage: " + usage());
        } else {
            matrixClient.profile().setDisplayName(arguments);
            config.setDisplayName(arguments);
        }
    }

    @Override
    public String help() {
        return "set new name.";
    }

    @Override
    public String usage() {
        return "!name <new name>";
    }
}
