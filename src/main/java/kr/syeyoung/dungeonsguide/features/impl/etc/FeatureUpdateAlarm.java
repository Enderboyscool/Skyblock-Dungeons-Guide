/*
 *     Dungeons Guide - The most intelligent Hypixel Skyblock Dungeons Mod
 *     Copyright (C) 2021  cyoung06
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package kr.syeyoung.dungeonsguide.features.impl.etc;

import kr.syeyoung.dungeonsguide.events.StompConnectedEvent;
import kr.syeyoung.dungeonsguide.features.SimpleFeature;
import kr.syeyoung.dungeonsguide.features.listener.StompConnectedListener;
import kr.syeyoung.dungeonsguide.features.listener.TickListener;
import kr.syeyoung.dungeonsguide.stomp.StompInterface;
import kr.syeyoung.dungeonsguide.stomp.StompMessageHandler;
import kr.syeyoung.dungeonsguide.stomp.StompPayload;
import kr.syeyoung.dungeonsguide.stomp.StompSubscription;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class FeatureUpdateAlarm extends SimpleFeature implements StompConnectedListener, StompMessageHandler, TickListener {
    public FeatureUpdateAlarm() {
        super("ETC", "Update Alarm","Show a warning in chat when a version has been released.", "etc.updatealarm", true);
    }

    private StompPayload stompPayload;
    @Override
    public void handle(StompInterface stompInterface, StompPayload stompPayload) {
        this.stompPayload = stompPayload;
    }

    @Override
    public void onTick() {
        if (stompPayload != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(stompPayload.payload()));
            stompPayload = null;
            Minecraft.getMinecraft().thePlayer.playSound("random.successful_hit", 1f,1f);
        }
    }

    @Override
    public void onStompConnected(StompConnectedEvent event) {
        event.getStompInterface().subscribe(StompSubscription.builder()
                .destination("/topic/updates")
                .ackMode(StompSubscription.AckMode.AUTO)
                .stompMessageHandler(this).build());
        event.getStompInterface().subscribe(StompSubscription.builder()
                .destination("/user/queue/messages")
                .ackMode(StompSubscription.AckMode.AUTO)
                .stompMessageHandler(this).build());
    }
}
