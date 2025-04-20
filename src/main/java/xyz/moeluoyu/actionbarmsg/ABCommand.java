package xyz.moeluoyu.actionbarmsg;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ABCommand implements CommandExecutor {

    private final Main plugin;

    public ABCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean show = !plugin.isShowActionBar(player);
            plugin.setShowActionBar(player, show);
            if (show) {
                player.sendMessage(ChatColor.GREEN + "ActionBar 已开启");
            } else {
                player.sendMessage(ChatColor.RED + "ActionBar 已关闭");
            }
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "只有玩家可以执行此命令");
            return false;
        }
    }
}