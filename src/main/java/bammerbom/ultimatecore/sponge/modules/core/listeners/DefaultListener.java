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
package bammerbom.ultimatecore.sponge.modules.core.listeners;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.config.defaultconfigs.datafiles.GlobalDataFile;
import bammerbom.ultimatecore.sponge.api.config.defaultconfigs.datafiles.PlayerDataFile;
import bammerbom.ultimatecore.sponge.api.user.UltimateUser;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class DefaultListener {

    @Listener(order = Order.EARLY)
    public void onJoin(ClientConnectionEvent.Join event) {
        Player p = event.getTargetEntity();

        //Player file
        PlayerDataFile config = new PlayerDataFile(event.getTargetEntity().getUniqueId());
        CommentedConfigurationNode node = config.get();
        node.getNode("lastconnect").setValue(System.currentTimeMillis());
        String ip = event.getTargetEntity().getConnection().getAddress().getAddress().toString().replace("/", "");
        node.getNode("lastip").setValue(ip);
        config.save(node);

        //Ipcache file
        GlobalDataFile file = new GlobalDataFile("ipcache");
        CommentedConfigurationNode node2 = file.get();
        node2.getNode(ip, "name").setValue(p.getName());
        node2.getNode(ip, "uuid").setValue(p.getUniqueId().toString());
        file.save(node2);
    }

    @Listener(order = Order.LATE)
    public void onDisconnect(ClientConnectionEvent.Disconnect event) {
        Player p = event.getTargetEntity();
        UltimateUser user = UltimateCore.get().getUserService().getUser(p);
        for (String key : UltimateUser.onlinekeys) {
            user.datas.remove(key);
        }
    }
}
