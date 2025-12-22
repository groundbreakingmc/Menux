package com.github.groundbreakingmc.menux.menu;

public enum MenuType {
    GENERIC_9x1(9),
    GENERIC_9x2(18),
    GENERIC_9x3(27),
    GENERIC_9x4(36),
    GENERIC_9x5(45),
    GENERIC_9x6(54),
    GENERIC_3x3(9),
    CRAFTER_3x3(9),
    ANVIL(3),
    BEACON(2),
    BLAST_FURNACE(3),
    BREAWING_STAND(5),
    CRAFTING(10),
    ENCHANTMENT(2),
    FURNACE(3),
    GRINDSTONE(3),
    HOPPER(5),
    LECTERN(0),
    LOOM(4),
    MERCHANT(1), // TODO check
    SCHULKER_BOX(27),
    SMITHING(3),
    SMOKER(3),
    CARTOGRAPHY_TABLE(3),
    STONECUTTER(2);

    private final int size;

    MenuType(int size) {
        this.size = size;
    }

    public static MenuType fromString(String value) {
        for (final MenuType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No menu type '" + value + "'.");
    }

    public int size() {
        return this.size;
    }
}
