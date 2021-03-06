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
package bammerbom.ultimatecore.sponge.api.command.impl;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.command.Command;
import bammerbom.ultimatecore.sponge.api.command.CommandService;
import bammerbom.ultimatecore.sponge.api.command.event.CommandRegisterEvent;
import bammerbom.ultimatecore.sponge.api.command.event.CommandUnregisterEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.event.cause.Cause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UCCommandService implements CommandService {
    private List<Command> commands = new ArrayList<>();
    //Commands which will be registered later
    private HashMap<Command, Runnable> commandsLater = new HashMap<>();

    /**
     * Get a list of all registered {@link Command}s.
     *
     * @return The list of all commands.
     */
    @Override
    public List<Command> getCommands() {
        return this.commands;
    }

    @Override
    public HashMap<Command, Runnable> getUnregisteredCommands() {
        return this.commandsLater;
    }

    /**
     * Register a new {@link Command}.
     * This also registers it to the sponge command manager.
     *
     * @param command The command to register
     * @return Whether the command was successfully registered
     */
    @Override
    public boolean register(Command command) {
        if (!UltimateCore.get().getCommandsConfig().get().getNode("commands", command.getIdentifier(), "enabled").getBoolean(true)) {
            return false;
        }
        if (Sponge.getEventManager().post(new CommandRegisterEvent(command, Cause.builder().notifier(UltimateCore.getContainer()).build()))) {
            return false;
        }
        this.commands.add(command);
        Sponge.getCommandManager().register(UltimateCore.get(), command.getCallable(), command.getAliases());
        return true;
    }

    @Override
    public boolean registerLater(Command command, Runnable runnable) {
        if (!UltimateCore.get().getCommandsConfig().get().getNode("commands", command.getIdentifier(), "enabled").getBoolean(true)) {
            return false;
        }
        this.commandsLater.put(command, runnable);
        return true;
    }

    /**
     * Unregisters the given {@link Command}.
     * This also unregisters it from the sponge command manager.
     *
     * @param command The {@link Command} to unregister
     * @return Whether the command was found
     */
    @Override
    public boolean unregister(Command command) {
        if (Sponge.getEventManager().post(new CommandUnregisterEvent(command, Cause.builder().notifier(UltimateCore.getContainer()).build()))) {
            return false;
        }
        return this.commands.remove(command);
    }

    /**
     * Unregisters the given {@link Command}.
     * This also unregisters it from the sponge service.
     * <p>
     * This is the same as calling unregister(get(id).get())
     *
     * @param id The {@link Command} to unregister
     * @return Whether the command was found
     */
    @Override
    public boolean unregister(String id) {
        Optional<Command> cmd = get(id);
        if (!cmd.isPresent()) return false;
        return unregister(cmd.get());
    }

    /**
     * Search a command by the provided identifier.
     * This will search for the id first, and then for an alias.
     *
     * @param id The id to search for
     * @return The command, or {@link Optional#empty()} if no results are found
     */
    @Override
    public Optional<Command> get(String id) {
        List<Command> matches = this.commands.stream().filter(cmd -> cmd.getIdentifier().equalsIgnoreCase(id)).collect(Collectors.toList());
        if (matches.isEmpty()) {
            matches = this.commands.stream().filter(cmd -> cmd.getAliases().contains(id)).collect(Collectors.toList());
        }
        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
    }

    @Override
    public void registerLateCommands() {
        CommandManager cm = Sponge.getCommandManager();
        getUnregisteredCommands().forEach((cmd, run) -> {
            for (String alias : cmd.getAliases()) {
                cm.get(alias).ifPresent(cm::removeMapping);
            }
            register(cmd);
            run.run();
        });
        this.commandsLater.clear();
    }
}
