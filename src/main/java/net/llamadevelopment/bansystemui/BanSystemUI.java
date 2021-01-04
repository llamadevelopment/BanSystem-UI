package net.llamadevelopment.bansystemui;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystemui.commands.BansystemuiCommand;
import net.llamadevelopment.bansystemui.components.forms.FormListener;
import net.llamadevelopment.bansystemui.components.forms.FormWindows;
import net.llamadevelopment.bansystemui.components.language.Language;

public class BanSystemUI extends PluginBase {

    @Getter
    public FormWindows formWindows;

    @Override
    public void onEnable() {
        try {
            this.saveDefaultConfig();
            Language.init(this);
            this.formWindows = new FormWindows(BanSystem.getApi().getProvider());
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
