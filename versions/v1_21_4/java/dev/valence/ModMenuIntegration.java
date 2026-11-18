package dev.valence;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.valence.gui.ValenceScreen;
import dev.valence.license.License;
import dev.valence.license.LicenseScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_437;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> (class_437)(License.active() ? new ValenceScreen(parent) : new LicenseScreen(parent, true));
    }
}
