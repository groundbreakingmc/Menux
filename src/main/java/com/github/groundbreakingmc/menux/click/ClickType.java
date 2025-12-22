package com.github.groundbreakingmc.menux.click;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;

public enum ClickType {
    LEFT_CLICK,
    RIGHT_CLICK,
    MIDDLE_CLICK,
    SHIFT_LEFT,
    SHIFT_RIGHT,
    ACTIONBAR_1,
    ACTIONBAR_2,
    ACTIONBAR_3,
    ACTIONBAR_4,
    ACTIONBAR_5,
    ACTIONBAR_6,
    ACTIONBAR_7,
    ACTIONBAR_8,
    ACTIONBAR_9,
    OFFHAND,
    DROP,
    CONTROL_DROP,
    QUICK_CRAFT,
    DOUBLE_CLICK,
    UNKNOWN;

    public static ClickType detect(WrapperPlayClientClickWindow packet) {
        return switch (packet.getWindowClickType()) {
            case PICKUP -> packet.getButton() == 0 ? LEFT_CLICK : RIGHT_CLICK;
            case QUICK_MOVE -> packet.getButton() == 0 ? SHIFT_LEFT : SHIFT_RIGHT;
            case SWAP -> packet.getButton() == 40 ? OFFHAND : values()[ACTIONBAR_1.ordinal() + packet.getButton()];
            case CLONE -> MIDDLE_CLICK;
            case THROW -> {
                if (packet.getSlot() >= 0) {
                    yield packet.getButton() == 0 ? DROP : CONTROL_DROP;
                } else {
                    yield packet.getButton() == 0 ? LEFT_CLICK : RIGHT_CLICK;
                }
            }
            case QUICK_CRAFT -> QUICK_CRAFT;
            case PICKUP_ALL -> DOUBLE_CLICK;
            case UNKNOWN -> UNKNOWN;
        };
    }
}
