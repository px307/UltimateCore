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
package bammerbom.ultimatecore.sponge.modules.teleport.commands;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.command.LowCommand;
import bammerbom.ultimatecore.sponge.api.language.utils.Messages;
import bammerbom.ultimatecore.sponge.api.module.Module;
import bammerbom.ultimatecore.sponge.api.module.Modules;
import bammerbom.ultimatecore.sponge.api.permission.Permission;
import bammerbom.ultimatecore.sponge.api.teleport.Teleportation;
import bammerbom.ultimatecore.sponge.api.teleport.utils.LocationUtil;
import bammerbom.ultimatecore.sponge.api.variable.utils.ArgumentUtil;
import bammerbom.ultimatecore.sponge.api.variable.utils.Selector;
import bammerbom.ultimatecore.sponge.api.variable.utils.VariableUtil;
import bammerbom.ultimatecore.sponge.modules.teleport.api.TeleportPermissions;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.List;

public class TeleportCommand implements LowCommand {
    @Override
    public Module getModule() {
        return Modules.TELEPORT.get();
    }

    @Override
    public String getIdentifier() {
        return "teleport";
    }

    @Override
    public Permission getPermission() {
        return TeleportPermissions.UC_TELEPORT_TELEPORT_BASE;
    }

    @Override
    public List<Permission> getPermissions() {
        return Arrays.asList(TeleportPermissions.UC_TELEPORT_TELEPORT_BASE, TeleportPermissions.UC_TELEPORT_TELEPORT_OTHERS);
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("teleport", "tp");
    }

    @Override
    public CommandResult run(CommandSource sender, String[] args) {
        if (!sender.hasPermission(TeleportPermissions.UC_TELEPORT_TELEPORT_BASE.get())) {
            Messages.send(sender, "core.nopermissions");
            return CommandResult.empty();
        }

        if (args.length == 0) {
            sender.sendMessage(getUsage(sender));
            return CommandResult.empty();
        } else if (args.length == 1) {
            //tp user
            if (!(sender instanceof Player)) {
                Messages.send(sender, "core.noplayer");
                return CommandResult.empty();
            }
            Player p = (Player) sender;
            Entity t = Selector.oneEntity(sender, args[0]).orElse(null);
            if (t == null) {
                Messages.send(sender, "core.entitynotfound", "%entity%", args[0]);
                return CommandResult.empty();
            }

            //Teleport
            Teleportation request = UltimateCore.get().getTeleportService().createTeleportation(sender, Arrays.asList(p), t::getTransform, teleportRequest -> {
                //Complete
                Messages.send(p, "teleport.command.teleport.self", "%target%", VariableUtil.getNameEntity(t));
            }, (teleportRequest, reason) -> {
            }, true, false);
            request.start();
            return CommandResult.success();
        } else if (args.length == 2 && ArgumentUtil.isDouble(args[0]) && ArgumentUtil.isDouble(args[1])) {
            //tp x z
            if (!(sender instanceof Player)) {
                Messages.send(sender, "core.noplayer");
                return CommandResult.empty();
            }
            Player p = (Player) sender;
            if (!sender.hasPermission(TeleportPermissions.UC_TELEPORT_TELEPORT_COORDINATES.get())) {
                Messages.send(sender, "core.nopermissions");
                return CommandResult.empty();
            }

            Double x = Double.parseDouble(args[0]);
            Double z = Double.parseDouble(args[1]);
            Double y = Double.parseDouble(LocationUtil.getHighestY(p.getWorld(), x, z).orElse(-1) + "") + 1;
            if (y == 0) {
                Messages.send(sender, "teleport.command.teleport.noy");
                return CommandResult.empty();
            }

            Location<World> target = new Location<>(p.getWorld(), x, y, z);
            Teleportation request = UltimateCore.get().getTeleportService().createTeleportation(sender, Arrays.asList(p), new Transform<>(target, p.getRotation(), p.getScale()), teleportRequest -> {
                //Complete
                Messages.send(p, "teleport.command.teleport.coords.self", "%x%", x.intValue(), "%y%", y.intValue(), "%z%", z.intValue());
            }, (teleportRequest, reason) -> {
            }, false, false);
            request.start();
            return CommandResult.success();
        } else if (args.length == 2) {
            //tp user user
            if (!sender.hasPermission(TeleportPermissions.UC_TELEPORT_TELEPORT_OTHERS.get())) {
                Messages.send(sender, "core.nopermissions");
                return CommandResult.empty();
            }
            List<Entity> e = Selector.multipleEntities(sender, args[0]);
            if (e.isEmpty()) {
                Messages.send(sender, "core.entitynotfound", "%entity%", args[0]);
                return CommandResult.empty();
            }
            Entity t = Selector.oneEntity(sender, args[1]).orElse(null);
            if (t == null) {
                Messages.send(sender, "core.entitynotfound", "%entity%", args[1]);
                return CommandResult.empty();
            }

            //Teleport
            Teleportation request = UltimateCore.get().getTeleportService().createTeleportation(sender, e, t::getTransform, teleportRequest -> {
                //Complete
                Messages.send(sender, "teleport.command.teleport.others", "%target1%", VariableUtil.getNamesEntity(e), "%target2%", VariableUtil.getNameEntity(t));
            }, (teleportRequest, reason) -> {
            }, true, false);
            request.start();
            return CommandResult.successCount(e.size());
        } else if (args.length == 3 && !ArgumentUtil.isDouble(args[0])) {
            // tp user x z
            if (!sender.hasPermission(TeleportPermissions.UC_TELEPORT_TELEPORT_COORDINATES_OTHERS.get())) {
                Messages.send(sender, "core.nopermissions");
                return CommandResult.empty();
            }
            List<Entity> e = Selector.multipleEntities(sender, args[0]);
            if (e.isEmpty()) {
                Messages.send(sender, "core.entitynotfound", "%entity%", args[0]);
                return CommandResult.empty();
            }
            World w = sender instanceof Player ? ((Player) sender).getWorld() : e.get(0).getWorld();
            Double x = Double.parseDouble(args[0]);
            Double z = Double.parseDouble(args[1]);
            Double y = Double.parseDouble(LocationUtil.getHighestY(w, x, z).orElse(-1) + "") + 1;
            if (y == 0) {
                Messages.send(sender, "teleport.command.teleport.noy");
                return CommandResult.empty();
            }

            Location<World> target = new Location<>(w, x, y, z);
            Teleportation request = UltimateCore.get().getTeleportService().createTeleportation(sender, e, new Transform<>(target), teleportRequest -> {
                //Complete
                Messages.send(sender, "teleport.command.teleport.coords.self", "%x%", x.intValue(), "%y%", y.intValue(), "%z%", z.intValue());
            }, (teleportRequest, reason) -> {
            }, false, false);
            request.start();
            return CommandResult.successCount(e.size());
        } else if (args.length == 3) {
            //tp x y z
            if (!(sender instanceof Player)) {
                Messages.send(sender, "core.noplayer");
                return CommandResult.empty();
            }
            Player p = (Player) sender;
            if (!sender.hasPermission(TeleportPermissions.UC_TELEPORT_TELEPORT_COORDINATES.get())) {
                Messages.send(sender, "core.nopermissions");
                return CommandResult.empty();
            }

            Double x = Double.parseDouble(args[0]);
            Double y = Double.parseDouble(args[1]);
            Double z = Double.parseDouble(args[2]);

            Location<World> target = new Location<>(p.getWorld(), x, y, z);
            Teleportation request = UltimateCore.get().getTeleportService().createTeleportation(sender, Arrays.asList(p), new Transform<>(target, p.getRotation(), p.getScale()), teleportRequest -> {
                //Complete
                Messages.send(p, "teleport.command.teleport.coords.self", "%x%", x.intValue(), "%y%", y.intValue(), "%z%", z.intValue());
            }, (teleportRequest, reason) -> {
            }, false, false);
            request.start();
            return CommandResult.success();
        } else if (args.length == 4) {
            //tp user x y z
            if (!sender.hasPermission(TeleportPermissions.UC_TELEPORT_TELEPORT_COORDINATES_OTHERS.get())) {
                Messages.send(sender, "core.nopermissions");
                return CommandResult.empty();
            }
            List<Entity> e = Selector.multipleEntities(sender, args[0]);
            if (e.isEmpty()) {
                Messages.send(sender, "core.entitynotfound", "%entity%", args[0]);
                return CommandResult.empty();
            }

            World w = sender instanceof Player ? ((Player) sender).getWorld() : e.get(0).getWorld();
            Double x = Double.parseDouble(args[1]);
            Double y = Double.parseDouble(args[2]);
            Double z = Double.parseDouble(args[3]);

            Location<World> target = new Location<>(w, x, y, z);
            Teleportation request = UltimateCore.get().getTeleportService().createTeleportation(sender, e, new Transform<>(target), teleportRequest -> {
                //Complete
                Messages.send(sender, "teleport.command.teleport.coords.self", "%x%", x.intValue(), "%y%", y.intValue(), "%z%", z.intValue());
            }, (teleportRequest, reason) -> {
            }, false, false);
            request.start();
            return CommandResult.successCount(e.size());
        }
        sender.sendMessage(getUsage(sender));
        return CommandResult.empty();
    }

    @Override
    public List<String> onTabComplete(CommandSource sender, String[] args, String curs, Integer curn) {
        return null;
    }
}
