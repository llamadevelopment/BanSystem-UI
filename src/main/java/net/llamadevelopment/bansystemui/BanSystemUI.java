package net.llamadevelopment.bansystemui;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystemui.commands.BansystemuiCommand;
import net.llamadevelopment.bansystemui.components.forms.FormListener;
import net.llamadevelopment.bansystemui.components.forms.FormWindows;
import net.llamadevelopment.bansystemui.components.language.Language;

public class BanSystemUI extends PluginBase {

    @Getter
    private static BanSystemUI instance;

    @Getter
    public FormWindows formWindows;

    @Override
    public void onEnable() {
        instance = this;
        try {
            this.saveDefaultConfig();
            Language.init();
            this.formWindows = new FormWindows(BanSystemAPI.getProvider());
            this.loadPlugin();
            this.getLogger().info("§aBanSystem-UI successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().error("§4Failed to load BanSystem-UI.");
        }
    }

    private void loadPlugin() {
        this.getServer().getCommandMap().register("bansystemui", new BansystemuiCommand(this));
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
    }

}
