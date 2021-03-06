/*
 * This file is part of UltimateCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) Bammerbom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package bammerbom.ultimatecore.sponge.api.module;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.config.defaultconfigs.module.ModuleConfig;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public interface Module {

    /**
     * Returns whether the module is enabled
     */
    default boolean isEnabled() {
        return UltimateCore.get().getModuleService().getModules().contains(this);
    }

    /**
     * This should return name of the module
     * For example: afk, ban, etc
     */
    String getIdentifier();

    /**
     * Returns a (short) description of the module.
     * Should be only one sentence.
     *
     * @return The description
     */
    Text getDescription();

    /**
     * This should return the config this module provides, or Optional.empty() when not available.
     */
    default Optional<? extends ModuleConfig> getConfig() {
        return Optional.empty();
    }

    /**
     * Called when the module is registered, normally during Pre-initialization.
     */
    default void onRegister() {
    }

    /**
     * Called when the module is being initialized.
     * During this phase the module should register all services and commands, and it should be ready to work.
     *
     * @param event The GameInitializationEvent
     */
    default void onInit(GameInitializationEvent event) {

    }

    /**
     * Called when the module is being Post-initialized.
     * During this phase the module should be able to accept api requests.
     *
     * @param event The GamePostInitializationEvent
     */
    default void onPostInit(GamePostInitializationEvent event) {

    }

    /**
     * Called when the game is reloaded
     * The config files will automatically be reloaded before this method is called
     */
    default void onReload(@Nullable GameReloadEvent event) {

    }

    /**
     * Called when the server is shutting down
     *
     * @param event The GameStoppingEvent
     */
    default void onStop(GameStoppingEvent event) {

    }

}
