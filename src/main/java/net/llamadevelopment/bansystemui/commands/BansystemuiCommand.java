package net.llamadevelopment.bansystemui.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import net.llamadevelopment.bansystemui.BanSystemUI;
import net.llamadevelopment.bansystemui.components.language.Language;

public class BansystemuiCommand extends PluginCommand<BanSystemUI> {

    public BansystemuiCommand(BanSystemUI owner) {
        super(owner.getConfig().getString("Commands.Bansystemui.Name"), owner);
        this.setDescription(owner.getConfig().getString("Commands.Bansystemui.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Bansystemui.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Bansystemui.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission(this.getPermission())) {
                this.getPlugin().getFormWindows().openManagementMenu((Player) sender);
            } else sender.sendMessage(Language.get("no-permission"));
        }
        return false;
    }

}
