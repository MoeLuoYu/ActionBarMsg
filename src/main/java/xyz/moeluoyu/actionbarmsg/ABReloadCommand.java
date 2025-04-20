package xyz.moeluoyu.actionbarmsg;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ABReloadCommand implements CommandExecutor {

    private final Main plugin;

    public ABReloadCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission("actionbarmsg.reload")) {
            plugin.reloadConfigContent();

            sender.sendMessage(ChatColor.GREEN + "ActionBar 配置已重新加载");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "你没有权限执行此命令");
            return false;
        }
    }
}