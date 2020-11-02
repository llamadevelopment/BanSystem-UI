package net.llamadevelopment.bansystemui.components.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import net.llamadevelopment.bansystem.components.provider.Provider;
import net.llamadevelopment.bansystemui.BanSystemUI;
import net.llamadevelopment.bansystemui.components.forms.custom.CustomForm;
import net.llamadevelopment.bansystemui.components.forms.simple.SimpleForm;
import net.llamadevelopment.bansystemui.components.language.Language;

import java.util.Arrays;

public class FormWindows {

    private final BanSystemUI instance;
    private final Provider provider;

    public FormWindows(BanSystemUI instance, Provider provider) {
        this.instance = instance;
        this.provider = provider;
    }

    public void openManagementMenu(Player player) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("management-menu-title"), Language.getNP("management-menu-content"))
                .addButton(new ElementButton(Language.getNP("management-punishment-tool")), e -> this.openPunishmentMenu(player))
                .addButton(new ElementButton(Language.getNP("management-data-tool")), e -> this.openDataMenu(player))
                .build();
        form.send(player);
    }

    public void openPunishmentMenu(Player player) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("punishment-menu-title"), Language.getNP("punishment-menu-content"))
                .addButton(new ElementButton(Language.getNP("punishment-ban")), e -> this.openBanMenu(player))
                .addButton(new ElementButton(Language.getNP("punishment-mute")), e -> this.openMuteMenu(player))
                .addButton(new ElementButton(Language.getNP("punishment-unban")), e -> this.openUnbanMenu(player))
                .addButton(new ElementButton(Language.getNP("punishment-unmute")), e -> this.openUnmuteMenu(player))
                .addButton(new ElementButton(Language.getNP("punishment-warn")), e -> this.openWarnMenu(player))
                .build();
        form.send(player);
    }

    public void openBanMenu(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("ban-menu-title"))
                .addElement(new ElementInput(Language.getNP("ban-player"), Language.getNP("ban-player-placeholder")))
                .addElement(new ElementDropdown(Language.getNP("ban-unit"), Arrays.asList("HOURS", "DAYS", "PERMANENT"), 1))
                .addElement(new ElementInput(Language.getNP("ban-time"), Language.getNP("ban-time-placeholder")))
                .addElement(new ElementInput(Language.getNP("ban-reason"), Language.getNP("ban-reason-placeholder")))
                .onSubmit((e, r) -> {
                    if (r.getInputResponse(0).isEmpty() || r.getInputResponse(2).isEmpty() || r.getInputResponse(3).isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    String target = r.getInputResponse(0);
                    String unit = r.getDropdownResponse(1).getElementContent();
                    String reason = r.getInputResponse(3);
                    try {
                        int time = Integer.parseInt(r.getInputResponse(2));
                        this.provider.playerIsBanned(target, isBanned -> {
                            if (isBanned) {
                                player.sendMessage(Language.get("player-is-banned", target));
                                return;
                            }
                            int seconds;
                            if (unit.equalsIgnoreCase("DAYS")) seconds = time * 86400;
                            else if (unit.equalsIgnoreCase("HOURS")) seconds = time * 3600;
                            else seconds = -1;
                            this.provider.banPlayer(target, reason, player.getName(), seconds);
                            player.sendMessage(Language.get("player-banned", target));
                        });
                    } catch (NumberFormatException exception) {
                        player.sendMessage(Language.get("invalid-time"));
                    }
                })
                .build();
        form.send(player);
    }

    public void openMuteMenu(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("mute-menu-title"))
                .addElement(new ElementInput(Language.getNP("mute-player"), Language.getNP("mute-player-placeholder")))
                .addElement(new ElementDropdown(Language.getNP("mute-unit"), Arrays.asList("HOURS", "DAYS", "PERMANENT"), 1))
                .addElement(new ElementInput(Language.getNP("mute-time"), Language.getNP("mute-time-placeholder")))
                .addElement(new ElementInput(Language.getNP("mute-reason"), Language.getNP("mute-reason-placeholder")))
                .onSubmit((e, r) -> {
                    if (r.getInputResponse(0).isEmpty() || r.getInputResponse(2).isEmpty() || r.getInputResponse(3).isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    String target = r.getInputResponse(0);
                    String unit = r.getDropdownResponse(1).getElementContent();
                    String reason = r.getInputResponse(3);
                    try {
                        int time = Integer.parseInt(r.getInputResponse(2));
                        this.provider.playerIsMuted(target, isMuted -> {
                            if (isMuted) {
                                player.sendMessage(Language.get("player-is-muted", target));
                                return;
                            }
                            int seconds;
                            if (unit.equalsIgnoreCase("DAYS")) seconds = time * 86400;
                            else if (unit.equalsIgnoreCase("HOURS")) seconds = time * 3600;
                            else seconds = -1;
                            this.provider.mutePlayer(target, reason, player.getName(), seconds);
                            player.sendMessage(Language.get("player-muted", target));
                        });
                    } catch (NumberFormatException exception) {
                        player.sendMessage(Language.get("invalid-time"));
                    }
                })
                .build();
        form.send(player);
    }

    public void openUnbanMenu(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("unban-menu-title"))
                .addElement(new ElementInput(Language.getNP("unban-target"), Language.getNP("unban-target-placeholder")))
                .onSubmit((e, r) -> {
                    String target = r.getInputResponse(0);
                    if (target.isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    this.provider.playerIsBanned(target, isBanned -> {
                        if (isBanned) {
                            this.provider.unbanPlayer(target);
                            player.sendMessage(Language.get("player-unbanned", target));
                        } else player.sendMessage(Language.get("player-not-banned", target));
                    });
                })
                .build();
        form.send(player);
    }

    public void openUnmuteMenu(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("unmute-menu-title"))
                .addElement(new ElementInput(Language.getNP("unmute-target"), Language.getNP("unmute-target-placeholder")))
                .onSubmit((e, r) -> {
                    String target = r.getInputResponse(0);
                    if (target.isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    this.provider.playerIsBanned(target, isBanned -> {
                        if (isBanned) {
                            this.provider.unbanPlayer(target);
                            player.sendMessage(Language.get("player-unmuted", target));
                        } else player.sendMessage(Language.get("player-not-muted", target));
                    });
                })
                .build();
        form.send(player);
    }

    public void openWarnMenu(Player player) {

    }

    public void openDataMenu(Player player) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("data-menu-title"), Language.getNP("data-menu-content"))
                .build();
        form.send(player);
    }

}
