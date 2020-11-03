package net.llamadevelopment.bansystemui.components.forms;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.Warn;
import net.llamadevelopment.bansystem.components.provider.Provider;
import net.llamadevelopment.bansystemui.components.forms.custom.CustomForm;
import net.llamadevelopment.bansystemui.components.forms.simple.SimpleForm;
import net.llamadevelopment.bansystemui.components.language.Language;

import java.util.Arrays;
import java.util.Set;

public class FormWindows {

    private final Provider provider;

    public FormWindows(Provider provider) {
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
                .addButton(new ElementButton(Language.getNP("back-button")), e -> this.openManagementMenu(player))
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
                    if (r.getInputResponse(0).isEmpty() || r.getInputResponse(3).isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    String target = r.getInputResponse(0);
                    String unit = r.getDropdownResponse(1).getElementContent();
                    String reason = r.getInputResponse(3);
                    if (!unit.equals("PERMANENT") && r.getInputResponse(2).isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    try {
                        int time;
                        if (r.getInputResponse(2).isEmpty()) time = 1;
                        else time = Integer.parseInt(r.getInputResponse(2));
                        int finalTime = time;
                        this.provider.playerIsBanned(target, isBanned -> {
                            if (isBanned) {
                                player.sendMessage(Language.get("player-is-banned", target));
                                return;
                            }
                            int seconds;
                            if (unit.equalsIgnoreCase("DAYS")) seconds = finalTime * 86400;
                            else if (unit.equalsIgnoreCase("HOURS")) seconds = finalTime * 3600;
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
                    if (r.getInputResponse(0).isEmpty() || r.getInputResponse(3).isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    String target = r.getInputResponse(0);
                    String unit = r.getDropdownResponse(1).getElementContent();
                    String reason = r.getInputResponse(3);
                    if (!unit.equals("PERMANENT") && r.getInputResponse(2).isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    try {
                        int time;
                        if (r.getInputResponse(2).isEmpty()) time = 1;
                        else time = Integer.parseInt(r.getInputResponse(2));
                        int finalTime = time;
                        this.provider.playerIsMuted(target, isMuted -> {
                            if (isMuted) {
                                player.sendMessage(Language.get("player-is-muted", target));
                                return;
                            }
                            int seconds;
                            if (unit.equalsIgnoreCase("DAYS")) seconds = finalTime * 86400;
                            else if (unit.equalsIgnoreCase("HOURS")) seconds = finalTime * 3600;
                            else seconds = -1;
                            this.provider.mutePlayer(target, reason, player.getName(), seconds);
                            SystemSettings settings = BanSystemAPI.getSystemSettings();
                            Player onlinePlayer = Server.getInstance().getPlayer(target);
                            if (onlinePlayer != null) {
                                this.provider.getMute(target, mute -> settings.cachedMute.put(target, mute));
                            }
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
                    this.provider.playerIsMuted(target, isBanned -> {
                        if (isBanned) {
                            this.provider.unmutePlayer(target);
                            Player onlinePlayer = Server.getInstance().getPlayer(target);
                            if (onlinePlayer != null) {
                                SystemSettings settings = BanSystemAPI.getSystemSettings();
                                settings.cachedMute.remove(onlinePlayer.getName());
                            }
                            player.sendMessage(Language.get("player-unmuted", target));
                        } else player.sendMessage(Language.get("player-not-muted", target));
                    });
                })
                .build();
        form.send(player);
    }

    public void openWarnMenu(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("warn-menu-title"))
                .addElement(new ElementInput(Language.getNP("warn-player"), Language.getNP("warn-player-placeholder")))
                .addElement(new ElementInput(Language.getNP("warn-reason"), Language.getNP("warn-reason-placeholder")))
                .onSubmit((e, r) -> {
                    String target = r.getInputResponse(0);
                    String reason = r.getInputResponse(1);
                    if (target.isEmpty() || reason.isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    this.provider.warnPlayer(target, reason, player.getName());
                    player.sendMessage(Language.get("player-warned", target));
                })
                .build();
        form.send(player);
    }

    public void openDataMenu(Player player) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("data-menu-title"), Language.getNP("data-menu-content"))
                .addButton(new ElementButton(Language.getNP("data-banlog")), e -> this.openBanlogSearch(player))
                .addButton(new ElementButton(Language.getNP("data-mutelog")), e -> this.openMutelogSearch(player))
                .addButton(new ElementButton(Language.getNP("data-warnlog")), e -> this.openWarnlogSearch(player))
                .addButton(new ElementButton(Language.getNP("back-button")), e -> this.openManagementMenu(player))
                .build();
        form.send(player);
    }

    public void openBanlogSearch(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("banlog-search-menu-title"))
                .addElement(new ElementInput(Language.getNP("banlog-search-player"), Language.getNP("banlog-search-player-placeholder")))
                .onSubmit((e, r) -> {
                    String target = r.getInputResponse(0);
                    if (target.isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    this.provider.getBanLog(target, bans -> {
                        if (bans.size() == 0) {
                            player.sendMessage(Language.get("no-data-available"));
                            return;
                        }
                        this.openBanlog(player, target, bans);
                    });
                })
                .build();
        form.send(player);
    }

    public void openBanlog(Player player, String target, Set<Ban> banlog) {
        SimpleForm.Builder form = new SimpleForm.Builder(Language.getNP("banlog-menu-title"), Language.getNP("banlog-menu-content", target));
        banlog.forEach(ban -> form.addButton(new ElementButton(Language.getNP("banlog-ban", ban.getReason(), ban.getBanID())), e -> {
            SimpleForm banForm = new SimpleForm.Builder(Language.getNP("banlog-ban-title", ban.getBanID()),
                    Language.getNP("banlog-ban-content", ban.getBanID(), ban.getReason(), ban.getBanner(), ban.getDate()))
                    .addButton(new ElementButton(Language.getNP("back-button")), f -> this.openBanlog(player, target, banlog))
                    .build();
            banForm.send(player);
        }));
        form.addButton(new ElementButton(Language.getNP("back-button")), e -> this.openManagementMenu(player));
        SimpleForm finalForm = form.build();
        finalForm.send(player);
    }

    public void openMutelogSearch(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("mutelog-search-menu-title"))
                .addElement(new ElementInput(Language.getNP("mutelog-search-player"), Language.getNP("mutelog-search-player-placeholder")))
                .onSubmit((e, r) -> {
                    String target = r.getInputResponse(0);
                    if (target.isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    this.provider.getMuteLog(target, mutes -> {
                        if (mutes.size() == 0) {
                            player.sendMessage(Language.get("no-data-available"));
                            return;
                        }
                        this.openMutelog(player, target, mutes);
                    });
                })
                .build();
        form.send(player);
    }

    public void openMutelog(Player player, String target, Set<Mute> mutelog) {
        SimpleForm.Builder form = new SimpleForm.Builder(Language.getNP("mutelog-menu-title"), Language.getNP("mutelog-menu-content", target));
        mutelog.forEach(mute -> form.addButton(new ElementButton(Language.getNP("mutelog-mute", mute.getReason(), mute.getMuteID())), e -> {
            SimpleForm banForm = new SimpleForm.Builder(Language.getNP("mutelog-mute-title", mute.getMuteID()),
                    Language.getNP("mutelog-mute-content", mute.getMuteID(), mute.getReason(), mute.getMuter(), mute.getDate()))
                    .addButton(new ElementButton(Language.getNP("back-button")), f -> this.openMutelog(player, target, mutelog))
                    .build();
            banForm.send(player);
        }));
        form.addButton(new ElementButton(Language.getNP("back-button")), e -> this.openManagementMenu(player));
        SimpleForm finalForm = form.build();
        finalForm.send(player);
    }

    public void openWarnlogSearch(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("warnlog-search-menu-title"))
                .addElement(new ElementInput(Language.getNP("warnlog-search-player"), Language.getNP("warnlog-search-player-placeholder")))
                .onSubmit((e, r) -> {
                    String target = r.getInputResponse(0);
                    if (target.isEmpty()) {
                        player.sendMessage(Language.get("invalid-input"));
                        return;
                    }
                    this.provider.getWarnLog(target, warns -> {
                        if (warns.size() == 0) {
                            player.sendMessage(Language.get("no-data-available"));
                            return;
                        }
                        this.openWarnlog(player, target, warns);
                    });
                })
                .build();
        form.send(player);
    }

    public void openWarnlog(Player player, String target, Set<Warn> warnlog) {
        SimpleForm.Builder form = new SimpleForm.Builder(Language.getNP("warnlog-menu-title"), Language.getNP("warnlog-menu-content", target));
        warnlog.forEach(warn -> form.addButton(new ElementButton(Language.getNP("warnlog-warn", warn.getReason(), warn.getWarnID())), e -> {
            SimpleForm banForm = new SimpleForm.Builder(Language.getNP("warnlog-warn-title", warn.getWarnID()),
                    Language.getNP("warnlog-warn-content", warn.getWarnID(), warn.getReason(), warn.getCreator(), warn.getDate()))
                    .addButton(new ElementButton(Language.getNP("back-button")), f -> this.openWarnlog(player, target, warnlog))
                    .build();
            banForm.send(player);
        }));
        form.addButton(new ElementButton(Language.getNP("back-button")), e -> this.openManagementMenu(player));
        SimpleForm finalForm = form.build();
        finalForm.send(player);
    }

}
