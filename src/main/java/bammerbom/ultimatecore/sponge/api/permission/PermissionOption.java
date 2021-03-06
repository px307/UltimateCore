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
package bammerbom.ultimatecore.sponge.api.permission;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.command.Command;
import bammerbom.ultimatecore.sponge.api.language.utils.Messages;
import bammerbom.ultimatecore.sponge.api.module.Module;
import bammerbom.ultimatecore.sponge.api.variable.utils.ArgumentUtil;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Permission ids should be in the format uc.[Module].[Command].otherstuff
 */
public class PermissionOption {
    private Module module;
    @Nullable
    private String commandid;
    private Optional<Command> command = null;
    private String identifier;
    private Text description;
    private String def;

    private PermissionOption(String id, Module mod, @Nullable String commandid, String def, Text desc) {
        this.module = mod;
        this.commandid = commandid;
        this.identifier = id;
        this.description = desc;
        this.def = def;
    }

    public static PermissionOption create(String identifier, Module module, @Nullable Command command, String def, Text description) {
        PermissionOption perm = new PermissionOption(identifier, module, command != null ? command.getIdentifier() : null, def, description);
        UltimateCore.get().getPermissionService().registerOption(perm);
        return perm;
    }

    public static PermissionOption create(String identifier, String moduleid, @Nullable String commandid, String def, Text description) {
        Optional<Module> module = UltimateCore.get().getModuleService().getModule(moduleid);
        if (!module.isPresent()) {
            Messages.log("Failed to register permissionoption " + identifier + ": Invalid module " + moduleid);
            //TODO ErrorLogger?
            return null;
        }

        PermissionOption perm = new PermissionOption(identifier, module.get(), commandid, def, description);
        UltimateCore.get().getPermissionService().registerOption(perm);
        return perm;
    }

    /**
     * Get the module that registered this permission.
     *
     * @return The module that registered this permission
     */
    public Module getModule() {
        return module;
    }

    /**
     * Get the command this {@link PermissionOption} is associated with, or {@link Optional#empty()} when not associated with a command.
     *
     * @return The command
     */
    public Optional<Command> getCommand() {
        if ((command == null || !command.isPresent()) && commandid != null) {
            command = UltimateCore.get().getCommandService().get(commandid);
            return command;
        }
        return command != null ? command : Optional.empty();
    }

    /**
     * Get the permission's id.
     * For example: uc.kick
     *
     * @return The permission's id
     */
    public String get() {
        return identifier;
    }

    /**
     * Get a description of this permission.
     *
     * @return The description of this permission
     */
    public Text getDescription() {
        return description;
    }

    /**
     * Get the default value of this permission option
     */
    public Optional<String> getDefault() {
        return Optional.ofNullable(def);
    }

    public String getFor(Subject subject) {
        return subject.getOption(get()).orElse(getDefault().orElse(null));
    }

    public Integer getIntFor(Subject subject) {
        String value = getFor(subject);
        if (!ArgumentUtil.isInteger(value)) {
            Messages.log(Messages.getFormatted("core.option.invalidnumber", "%option%", get(), "%value%", value));
            return 0;
        }
        return Integer.parseInt(value);
    }
}
