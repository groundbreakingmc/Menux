package com.github.groundbreakingmc.menux.property;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// TODO: use in DefaultMenuInstance in open func
public interface WindowPropertyPacketFactory {

    @NotNull List<WrapperPlayServerWindowProperty> createPropertyPackets();
}
