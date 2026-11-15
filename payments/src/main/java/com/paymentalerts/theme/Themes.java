package com.paymentalerts.theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Catalog of built-in visual themes for payment alerts.
 * Users can pick a global theme and/or override per effect tier.
 */
public final class Themes {
    private static final Random RANDOM = new Random();
    private static final Map<String, Theme> BY_ID = new LinkedHashMap<>();
    private static final List<Theme> ALL = new ArrayList<>();

    public static final Theme CLASSIC_GOLD = register(new Theme("classic_gold", "Classic Gold", 0xFFD700, 0xFFA500, 0xFFF8DC, 0xFFFFE066, "gold"));

    static {
        register(new Theme("classic_gold", "Classic Gold", 0xFFD700, 0xFFA500, 0xFFF8DC, 0xFFFFE066, "gold"));
        // CycloneSkies signature palette: lilac -> indigo with an ice-white accent and a cyan storm glow.
        register(new Theme("cyclone", "Cyclone", 0xA77BFF, 0x7C9CFF, 0xE6F0FF, 0x5FD8FF, "cyclone"));
        register(new Theme("classic_silver", "Classic Silver", 0xC0C0C0, 0xA8A8A8, 0xFFFFFF, 0xE8E8E8, "silver"));
        register(new Theme("classic_emerald", "Classic Emerald", 0x50C878, 0x2E8B57, 0x98FB98, 0x7CFC00, "green"));
        register(new Theme("classic_ruby", "Classic Ruby", 0xE0115F, 0x9B111E, 0xFF6B6B, 0xFF1493, "red"));
        register(new Theme("classic_sapphire", "Classic Sapphire", 0x0F52BA, 0x082567, 0x4169E1, 0x00BFFF, "blue"));
        register(new Theme("neon_pink", "Neon Pink", 0xFF10F0, 0xFF00AA, 0xFFB6C1, 0xFF69B4, "neon"));
        register(new Theme("neon_cyan", "Neon Cyan", 0x00FFFF, 0x00CED1, 0xE0FFFF, 0x7FFFD4, "neon"));
        register(new Theme("neon_green", "Neon Green", 0x39FF14, 0x00FF7F, 0xADFF2F, 0x7FFF00, "neon"));
        register(new Theme("neon_purple", "Neon Purple", 0xBF00FF, 0x8A2BE2, 0xE6E6FA, 0xDA70D6, "neon"));
        register(new Theme("neon_orange", "Neon Orange", 0xFF5F1F, 0xFF4500, 0xFFDAB9, 0xFFA500, "neon"));
        register(new Theme("neon_blue", "Neon Blue", 0x1F51FF, 0x0047AB, 0x87CEEB, 0x00BFFF, "neon"));
        register(new Theme("neon_yellow", "Neon Yellow", 0xFFFF00, 0xFFD700, 0xFFFACD, 0xFFFF66, "neon"));
        register(new Theme("neon_red", "Neon Red", 0xFF0033, 0xDC143C, 0xFF6B6B, 0xFF1744, "neon"));
        register(new Theme("neon_lime", "Neon Lime", 0xCCFF00, 0xA8E10C, 0xF0FFF0, 0xDFFF00, "neon"));
        register(new Theme("neon_magenta", "Neon Magenta", 0xFF00FF, 0xC71585, 0xFFB6C1, 0xFF66FF, "neon"));
        register(new Theme("cyberpunk", "Cyberpunk", 0xFF00A0, 0x00F0FF, 0x1A0033, 0xFF00FF, "cyber"));
        register(new Theme("synthwave", "Synthwave", 0xFF2A6A, 0x05D9E8, 0x2B1B4D, 0xD16BA5, "cyber"));
        register(new Theme("vaporwave", "Vaporwave", 0xFF71CE, 0x01CDFE, 0xB967FF, 0x05FFA1, "cyber"));
        register(new Theme("matrix_green", "Matrix Green", 0x00FF41, 0x008F11, 0x003B00, 0x00FF41, "cyber"));
        register(new Theme("blade_runner", "Blade Runner", 0xFF6B35, 0x00A8E8, 0x1A1A2E, 0xFF9F1C, "cyber"));
        register(new Theme("tron_blue", "Tron Blue", 0x00D9FF, 0x0077B6, 0x001D3D, 0x48CAE4, "cyber"));
        register(new Theme("holo_pink", "Holo Pink", 0xFF6EC7, 0xC44DFF, 0x2D1B4E, 0xFF9EEE, "cyber"));
        register(new Theme("digital_rain", "Digital Rain", 0x00FF9F, 0x00B8A9, 0x0A1628, 0x00FFCC, "cyber"));
        register(new Theme("neon_tokyo", "Neon Tokyo", 0xFF2E63, 0x08D9D6, 0x252A34, 0xFF2E63, "cyber"));
        register(new Theme("arc_reactor", "Arc Reactor", 0x00E5FF, 0x0096C7, 0x0B132B, 0x48CAE4, "cyber"));
        register(new Theme("ocean_deep", "Ocean Deep", 0x0077B6, 0x023E8A, 0x90E0EF, 0x00B4D8, "nature"));
        register(new Theme("forest_whisper", "Forest Whisper", 0x2D6A4F, 0x1B4332, 0x95D5B2, 0x52B788, "nature"));
        register(new Theme("desert_sand", "Desert Sand", 0xE9C46A, 0xF4A261, 0xE76F51, 0xFFE8A3, "nature"));
        register(new Theme("aurora_borealis", "Aurora Borealis", 0x00F5D4, 0x00BBF9, 0x9B5DE5, 0xF15BB5, "nature"));
        register(new Theme("volcanic", "Volcanic", 0xFF4500, 0x8B0000, 0xFFD700, 0xFF6347, "nature"));
        register(new Theme("arctic_ice", "Arctic Ice", 0xA8DADC, 0x457B9D, 0xF1FAEE, 0xE0FBFC, "nature"));
        register(new Theme("cherry_blossom", "Cherry Blossom", 0xFFB7C5, 0xFF69B4, 0xFFF0F5, 0xFFC0CB, "nature"));
        register(new Theme("autumn_leaves", "Autumn Leaves", 0xD2691E, 0x8B4513, 0xFFD700, 0xFF8C00, "nature"));
        register(new Theme("tropical", "Tropical", 0x00C853, 0xFF6D00, 0x00B0FF, 0xFFD600, "nature"));
        register(new Theme("moss_and_stone", "Moss & Stone", 0x606C38, 0x283618, 0xFEFAE0, 0xDDA15E, "nature"));
        register(new Theme("coral_reef", "Coral Reef", 0xFF6F61, 0x00A8A8, 0xFFD166, 0xEF476F, "nature"));
        register(new Theme("mountain_mist", "Mountain Mist", 0x8D99AE, 0x2B2D42, 0xEDF2F4, 0xA8DADC, "nature"));
        register(new Theme("royal_purple", "Royal Purple", 0x7B2CBF, 0x5A189A, 0xE0AAFF, 0xC77DFF, "luxury"));
        register(new Theme("imperial_gold", "Imperial Gold", 0xFFD700, 0xB8860B, 0xFFF8DC, 0xFFE066, "luxury"));
        register(new Theme("platinum", "Platinum", 0xE5E4E2, 0xA0A0A0, 0xFFFFFF, 0xF5F5F5, "luxury"));
        register(new Theme("rose_gold", "Rose Gold", 0xB76E79, 0xE0BFB8, 0xFFE4E1, 0xF7CAC9, "luxury"));
        register(new Theme("obsidian", "Obsidian", 0x1C1C1C, 0x0D0D0D, 0x4A4A4A, 0x2F2F2F, "luxury"));
        register(new Theme("emerald_royalty", "Emerald Royalty", 0x046307, 0x0B3D0B, 0x50C878, 0x3CB371, "luxury"));
        register(new Theme("sapphire_crown", "Sapphire Crown", 0x0F52BA, 0x061A40, 0x6495ED, 0x4169E1, "luxury"));
        register(new Theme("blood_ruby", "Blood Ruby", 0x9B111E, 0x5C0011, 0xFF4D6D, 0xE63946, "luxury"));
        register(new Theme("pearl_white", "Pearl White", 0xF8F6F0, 0xE8E0D5, 0xFFFFFF, 0xFFFAF0, "luxury"));
        register(new Theme("champagne", "Champagne", 0xF7E7CE, 0xD4AF37, 0xFFF8DC, 0xFFE4B5, "luxury"));
        register(new Theme("blood_moon", "Blood Moon", 0x8B0000, 0x4A0000, 0xFF4444, 0xFF0000, "dark"));
        register(new Theme("void_black", "Void Black", 0x0A0A0A, 0x000000, 0x333333, 0x1A1A1A, "dark"));
        register(new Theme("toxic_waste", "Toxic Waste", 0x7CFC00, 0x228B22, 0xADFF2F, 0x39FF14, "dark"));
        register(new Theme("ghostly", "Ghostly", 0xB0C4DE, 0x708090, 0xF0F8FF, 0xE6E6FA, "dark"));
        register(new Theme("haunted", "Haunted", 0x4B0082, 0x2E0854, 0x9370DB, 0x8A2BE2, "dark"));
        register(new Theme("shadow_realm", "Shadow Realm", 0x2F2F2F, 0x1A1A1A, 0x6A5ACD, 0x483D8B, "dark"));
        register(new Theme("crimson_night", "Crimson Night", 0xDC143C, 0x8B0000, 0xFF6B6B, 0xFF1744, "dark"));
        register(new Theme("abyss", "Abyss", 0x000080, 0x000033, 0x4169E1, 0x1E90FF, "dark"));
        register(new Theme("phantom", "Phantom", 0xDDA0DD, 0x9370DB, 0xE6E6FA, 0xBA55D3, "dark"));
        register(new Theme("necromancer", "Necromancer", 0x2D1B4E, 0x1A0A2E, 0x9B59B6, 0x8E44AD, "dark"));
        register(new Theme("cotton_candy", "Cotton Candy", 0xFFB6C1, 0x87CEEB, 0xFFF0F5, 0xFFC0CB, "candy"));
        register(new Theme("bubblegum", "Bubblegum", 0xFF69B4, 0xFF1493, 0xFFB6C1, 0xFF85C1, "candy"));
        register(new Theme("pastel_rainbow", "Pastel Rainbow", 0xFF9AA2, 0xB5EAD7, 0xC7CEEA, 0xFFDAC1, "candy"));
        register(new Theme("mint_chocolate", "Mint Chocolate", 0x98D8C8, 0x6B4226, 0xE8F8F5, 0xA8E6CF, "candy"));
        register(new Theme("lemon_drop", "Lemon Drop", 0xFFF44F, 0xFFD700, 0xFFFACD, 0xFFFF99, "candy"));
        register(new Theme("lavender_dream", "Lavender Dream", 0xE6E6FA, 0xD8BFD8, 0xFFF0F5, 0xDDA0DD, "candy"));
        register(new Theme("peach_fizz", "Peach Fizz", 0xFFDAB9, 0xFFB347, 0xFFF5EE, 0xFFCC99, "candy"));
        register(new Theme("strawberry_milk", "Strawberry Milk", 0xFFB6C1, 0xFF6B9D, 0xFFF0F5, 0xFFC0CB, "candy"));
        register(new Theme("blueberry_frost", "Blueberry Frost", 0x89CFF0, 0x5B9BD5, 0xE0F7FA, 0xADD8E6, "candy"));
        register(new Theme("candy_corn", "Candy Corn", 0xFFB347, 0xFF6B35, 0xFFF8DC, 0xFFD700, "candy"));
        register(new Theme("christmas", "Christmas", 0xC41E3A, 0x165B33, 0xFFD700, 0xFFFFFF, "seasonal"));
        register(new Theme("halloween", "Halloween", 0xFF6600, 0x2D0A00, 0x9B30FF, 0xFF8C00, "seasonal"));
        register(new Theme("valentine", "Valentine", 0xFF1493, 0xC71585, 0xFFB6C1, 0xFF69B4, "seasonal"));
        register(new Theme("st_patricks", "St Patricks", 0x00A86B, 0x006400, 0xFFD700, 0x32CD32, "seasonal"));
        register(new Theme("independence", "Independence", 0xB22234, 0x3C3B6E, 0xFFFFFF, 0xFFD700, "seasonal"));
        register(new Theme("easter", "Easter", 0xFFB6C1, 0x98FB98, 0xE6E6FA, 0xFFFACD, "seasonal"));
        register(new Theme("summer_solstice", "Summer Solstice", 0xFF8C00, 0xFFD700, 0x87CEEB, 0xFFA500, "seasonal"));
        register(new Theme("winter_wonderland", "Winter Wonderland", 0xE0F7FA, 0x81D4FA, 0xFFFFFF, 0xB3E5FC, "seasonal"));
        register(new Theme("thanksgiving", "Thanksgiving", 0xD2691E, 0x8B4513, 0xFFD700, 0xCD853F, "seasonal"));
        register(new Theme("new_year", "New Year", 0xFFD700, 0xC0C0C0, 0xFFFFFF, 0xFFE066, "seasonal"));
        register(new Theme("diamond", "Diamond", 0x4AEDD9, 0x1ABC9C, 0xA8FFF4, 0x5CE1E6, "minecraft"));
        register(new Theme("netherite", "Netherite", 0x4A4450, 0x2D2A32, 0x7A6F7D, 0x5C5366, "minecraft"));
        register(new Theme("emerald_ore", "Emerald Ore", 0x17DD62, 0x0A8F3C, 0x7CFFB2, 0x2ECC71, "minecraft"));
        register(new Theme("redstone", "Redstone", 0xFF0000, 0x8B0000, 0xFF6B6B, 0xFF3333, "minecraft"));
        register(new Theme("lapis", "Lapis", 0x1E3A8A, 0x1E40AF, 0x60A5FA, 0x3B82F6, "minecraft"));
        register(new Theme("amethyst", "Amethyst", 0x9B59B6, 0x6C3483, 0xD2B4DE, 0xBB8FCE, "minecraft"));
        register(new Theme("gold_block", "Gold Block", 0xF1C40F, 0xD4AC0D, 0xF9E79F, 0xF4D03F, "minecraft"));
        register(new Theme("iron", "Iron", 0xD5D8DC, 0xA6ACAF, 0xF8F9F9, 0xE5E7E9, "minecraft"));
        register(new Theme("copper", "Copper", 0xE67E22, 0xA04000, 0xF5B041, 0xEB984E, "minecraft"));
        register(new Theme("prismarine", "Prismarine", 0x1ABC9C, 0x16A085, 0x76D7C4, 0x48C9B0, "minecraft"));
        register(new Theme("ender", "Ender", 0x1A0A2E, 0x0D0221, 0x9B59B6, 0x8E44AD, "minecraft"));
        register(new Theme("sculk", "Sculk", 0x0D3B3B, 0x052222, 0x1ABC9C, 0x148F77, "minecraft"));
        register(new Theme("dark_academia", "Dark Academia", 0x5C4033, 0x3C2F2F, 0xD4A574, 0x8B7355, "aesthetic"));
        register(new Theme("light_academia", "Light Academia", 0xF5E6D3, 0xE8D5B7, 0xFFFFFF, 0xFAF0E6, "aesthetic"));
        register(new Theme("cottagecore", "Cottagecore", 0xA8C686, 0x6B8E4E, 0xF5E6C8, 0xC9D6A3, "aesthetic"));
        register(new Theme("y2k", "Y2K", 0xFF69B4, 0x00CED1, 0xFFFFFF, 0xFFB6C1, "aesthetic"));
        register(new Theme("grunge", "Grunge", 0x4A4A4A, 0x2C2C2C, 0x8B4513, 0x696969, "aesthetic"));
        register(new Theme("minimal_white", "Minimal White", 0xFFFFFF, 0xF5F5F5, 0xE0E0E0, 0xFAFAFA, "aesthetic"));
        register(new Theme("minimal_black", "Minimal Black", 0x111111, 0x000000, 0x333333, 0x1A1A1A, "aesthetic"));
        register(new Theme("soft_girl", "Soft Girl", 0xFFB6C1, 0xDDA0DD, 0xFFF0F5, 0xFFC0CB, "aesthetic"));
        register(new Theme("e-girl", "E-Girl", 0xFF1493, 0x000000, 0x00FFFF, 0xFF00FF, "aesthetic"));
        register(new Theme("vsco", "VSCO", 0xF4A460, 0x87CEEB, 0xFFF8DC, 0xFFDAB9, "aesthetic"));
        register(new Theme("inferno", "Inferno", 0xFF4500, 0xFF0000, 0xFFD700, 0xFF6347, "energy"));
        register(new Theme("plasma", "Plasma", 0x00FFFF, 0xFF00FF, 0xFFFFFF, 0x7FFFD4, "energy"));
        register(new Theme("solar_flare", "Solar Flare", 0xFF8C00, 0xFF4500, 0xFFFF00, 0xFFA500, "energy"));
        register(new Theme("electric", "Electric", 0x00BFFF, 0x1E90FF, 0xFFFFFF, 0x87CEFA, "energy"));
        register(new Theme("thunder", "Thunder", 0xFFD700, 0x4169E1, 0xFFFFFF, 0xFFFF00, "energy"));
        register(new Theme("magma", "Magma", 0xFF4500, 0x8B0000, 0xFFD700, 0xFF6347, "energy"));
        register(new Theme("frostbite", "Frostbite", 0x00FFFF, 0x00CED1, 0xE0FFFF, 0xAFEEEE, "energy"));
        register(new Theme("starfire", "Starfire", 0xFF1493, 0xFFD700, 0xFFFFFF, 0xFF69B4, "energy"));
        register(new Theme("nova", "Nova", 0xFFFFFF, 0xFFD700, 0xFF8C00, 0xFFE4B5, "energy"));
        register(new Theme("quasar", "Quasar", 0x7B2CBF, 0x00F5D4, 0xFFFFFF, 0xC77DFF, "energy"));
        register(new Theme("rainbow", "Rainbow", 0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, "misc"));
        register(new Theme("monochrome", "Monochrome", 0x808080, 0x404040, 0xFFFFFF, 0xC0C0C0, "misc"));
        register(new Theme("sepia", "Sepia", 0x704214, 0x4A2C0A, 0xD2B48C, 0xC19A6B, "misc"));
        register(new Theme("teal_and_coral", "Teal & Coral", 0x008080, 0xFF7F50, 0xE0FFFF, 0x20B2AA, "misc"));
        register(new Theme("sunset", "Sunset", 0xFF6B35, 0xF7931E, 0xFFD166, 0xEF476F, "misc"));
        register(new Theme("midnight", "Midnight", 0x191970, 0x000033, 0x4169E1, 0x1E90FF, "misc"));
        register(new Theme("honey", "Honey", 0xFFB300, 0xFF8C00, 0xFFE4B5, 0xFFD700, "misc"));
        register(new Theme("jade", "Jade", 0x00A86B, 0x006B3F, 0x98FF98, 0x3CB371, "misc"));
        register(new Theme("turquoise", "Turquoise", 0x40E0D0, 0x00CED1, 0xAFEEEE, 0x48D1CC, "misc"));
        register(new Theme("indigo", "Indigo", 0x4B0082, 0x2E0854, 0x9370DB, 0x6A5ACD, "misc"));
        register(new Theme("maroon", "Maroon", 0x800000, 0x4A0000, 0xCD5C5C, 0xB22222, "misc"));
        register(new Theme("olive", "Olive", 0x808000, 0x556B2F, 0xF5F5DC, 0x9ACD32, "misc"));
        register(new Theme("slate", "Slate", 0x708090, 0x2F4F4F, 0xF0F8FF, 0x778899, "misc"));
        register(new Theme("coral_pink", "Coral Pink", 0xF88379, 0xE9967A, 0xFFE4E1, 0xFFA07A, "misc"));
        register(new Theme("steel_blue", "Steel Blue", 0x4682B4, 0x1E3A5F, 0xB0C4DE, 0x5F9EA0, "misc"));
        register(new Theme("hot_pink", "Hot Pink", 0xFF69B4, 0xFF1493, 0xFFB6C1, 0xFF85C1, "misc"));
        register(new Theme("lime_zest", "Lime Zest", 0x32CD32, 0x228B22, 0xADFF2F, 0x7CFC00, "misc"));
        register(new Theme("sky_blue", "Sky Blue", 0x87CEEB, 0x4682B4, 0xF0F8FF, 0xADD8E6, "misc"));
        register(new Theme("deep_purple", "Deep Purple", 0x6A0DAD, 0x4B0082, 0xD8BFD8, 0x9932CC, "misc"));
        register(new Theme("burnt_orange", "Burnt Orange", 0xCC5500, 0x8B4000, 0xFFDAB9, 0xE97451, "misc"));
        register(new Theme("neon_pulse_1", "Neon Pulse 1", 0xFF0080, 0x00FF80, 0xFFFFFF, 0xFF80FF, "extra"));
        register(new Theme("neon_pulse_2", "Neon Pulse 2", 0x80FF00, 0x0080FF, 0xFFFFFF, 0x80FFFF, "extra"));
        register(new Theme("neon_pulse_3", "Neon Pulse 3", 0xFF8000, 0x8000FF, 0xFFFFFF, 0xFFFF80, "extra"));
        register(new Theme("galaxy_a", "Galaxy A", 0x1B0034, 0x4A0080, 0xE0AAFF, 0xC77DFF, "extra"));
        register(new Theme("galaxy_b", "Galaxy B", 0x0D1B2A, 0x1B263B, 0x778DA9, 0x415A77, "extra"));
        register(new Theme("galaxy_c", "Galaxy C", 0x2D00F7, 0x6A00F4, 0xE0AAFF, 0xF20089, "extra"));
        register(new Theme("retro_80s", "Retro 80s", 0xFF2A6A, 0x05D9E8, 0xFFE66D, 0xFF6B6B, "extra"));
        register(new Theme("retro_90s", "Retro 90s", 0xFF6EC7, 0x7B68EE, 0x00FFAA, 0xFFD166, "extra"));
        register(new Theme("steampunk", "Steampunk", 0xCD7F32, 0x8B4513, 0xD4AF37, 0xB87333, "extra"));
        register(new Theme("dieselpunk", "Dieselpunk", 0x3D3D3D, 0x1A1A1A, 0xFF8C00, 0x696969, "extra"));
        register(new Theme("biopunk", "Biopunk", 0x39FF14, 0x0D3B0D, 0xADFF2F, 0x7CFC00, "extra"));
        register(new Theme("solarpunk", "Solarpunk", 0xF4A261, 0x2A9D8F, 0xE9C46A, 0xE76F51, "extra"));
        register(new Theme("oceanpunk", "Oceanpunk", 0x0077B6, 0x023E8A, 0x90E0EF, 0x00B4D8, "extra"));
        register(new Theme("crystal_ice", "Crystal Ice", 0xE0FBFC, 0x98C1D9, 0xFFFFFF, 0xBEE9E8, "extra"));
        register(new Theme("lava_lamp", "Lava Lamp", 0xFF4500, 0x9400D3, 0xFFD700, 0xFF6347, "extra"));
        register(new Theme("disco", "Disco", 0xFF00FF, 0x00FFFF, 0xFFFF00, 0xFF69B4, "extra"));
        register(new Theme("party_mode", "Party Mode", 0xFF1493, 0x00FF00, 0x00BFFF, 0xFFD700, "extra"));
        register(new Theme("streamer_rgb", "Streamer RGB", 0xFF0000, 0x00FF00, 0x0000FF, 0xFFFFFF, "extra"));
        register(new Theme("donut_pink", "Donut Pink", 0xFF69B4, 0xFFB6C1, 0xFFF0F5, 0xFFC0CB, "extra"));
        register(new Theme("donut_glaze", "Donut Glaze", 0xFFE4E1, 0xFFB6C1, 0xFFFFFF, 0xFFDAB9, "extra"));
    }

    private Themes() {}

    private static Theme register(Theme t) {
        BY_ID.put(t.id, t);
        ALL.add(t);
        return t;
    }

    public static List<Theme> all() {
        return Collections.unmodifiableList(ALL);
    }

    public static int count() {
        return ALL.size();
    }

    public static Theme byId(String id) {
        if (id == null || id.isEmpty() || "random".equalsIgnoreCase(id)) {
            return null; // caller treats null as random / default
        }
        Theme t = BY_ID.get(id.toLowerCase(Locale.ROOT));
        if (t != null) return t;
        // fuzzy: replace spaces
        return BY_ID.get(id.toLowerCase(Locale.ROOT).replace(' ', '_'));
    }

    public static Theme byIndex(int index) {
        if (ALL.isEmpty()) return CLASSIC_GOLD;
        int i = index % ALL.size();
        if (i < 0) i += ALL.size();
        return ALL.get(i);
    }

    public static Theme random() {
        return ALL.get(RANDOM.nextInt(ALL.size()));
    }

    public static Theme resolve(String configuredId, Theme fallback) {
        if (configuredId == null || configuredId.isEmpty() || "default".equalsIgnoreCase(configuredId)) {
            return fallback != null ? fallback : CLASSIC_GOLD;
        }
        if ("random".equalsIgnoreCase(configuredId)) {
            return random();
        }
        Theme t = byId(configuredId);
        return t != null ? t : (fallback != null ? fallback : CLASSIC_GOLD);
    }

    /** Opaque ARGB from 0xRRGGBB */
    public static int argb(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    public static int argb(int rgb, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
        return (a << 24) | (rgb & 0xFFFFFF);
    }
}
