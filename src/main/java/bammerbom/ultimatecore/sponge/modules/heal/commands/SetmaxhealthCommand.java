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
package bammerbom.ultimatecore.sponge.modules.heal.commands;

import bammerbom.ultimatecore.sponge.api.command.HighCommand;
import bammerbom.ultimatecore.sponge.api.command.annotations.CommandInfo;
import bammerbom.ultimatecore.sponge.api.command.argument.Arguments;
import bammerbom.ultimatecore.sponge.api.command.argument.arguments.BoundedDoubleArgument;
import bammerbom.ultimatecore.sponge.api.command.argument.arguments.PlayerArgument;
import bammerbom.ultimatecore.sponge.api.language.utils.Messages;
import bammerbom.ultimatecore.sponge.api.permission.Permission;
import bammerbom.ultimatecore.sponge.modules.heal.HealModule;
import bammerbom.ultimatecore.sponge.modules.heal.api.HealPermissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;

@CommandInfo(module = HealModule.class, aliases = {"setmaxhealth", "setmaxlives", "maxhealth", "maxlives"})
public class SetmaxhealthCommand implements HighCommand {

    @Override
    public Permission getPermission() {
        return HealPermissions.UC_HEAL_SETMAXHEALTH_BASE;

    }

    @Override
    public List<Permission> getPermissions() {
        return Arrays.asList(HealPermissions.UC_HEAL_SETMAXHEALTH_BASE, HealPermissions.UC_HEAL_SETMAXHEALTH_OTHERS);
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                Arguments.builder(new BoundedDoubleArgument(Text.of("health"), 0.0, null)).onlyOne().build(), Arguments.builder(new PlayerArgument(Text.of("player"))).optional().onlyOne().build(),
        };
    }

    @Override
    public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {
        checkPermission(sender, HealPermissions.UC_HEAL_SETMAXHEALTH_BASE);
        if (!args.hasAny("player")) {
            checkIfPlayer(sender);
            Player p = (Player) sender;

            Double health = args.<Double>getOne("health").get();
            p.offer(Keys.MAX_HEALTH, health);

            Messages.send(sender, "heal.command.setmaxhealth.success", "%health%", health);
            return CommandResult.success();
        } else {
            checkPermission(sender, HealPermissions.UC_HEAL_SETMAXHEALTH_OTHERS);
            Player t = args.<Player>getOne("player").get();

            Double health = args.<Double>getOne("health").get();
            t.offer(Keys.MAX_HEALTH, health);

            Messages.send(sender, "heal.command.setmaxhealth.success.self", "%target%", t, "%health%", health);
            Messages.send(t, "heal.command.setmaxhealth.success.others", "%player%", sender, "%health%", health);
            return CommandResult.success();
        }
    }
}
