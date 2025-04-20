package xyz.moeluoyu.actionbarmsg;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import me.clip.placeholderapi.PlaceholderAPI;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private File configFile;
    private File playerDataFile;
    private FileConfiguration playerData;
    private final Map<Player, Boolean> showActionBarMap = new HashMap<>();
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("ActionBarMsg 正在载入中...");
        getLogger().info("定制插件找落雨，买插件上速德优，速德优（北京）网络科技有限公司出品，落雨QQ：1498640871");
        config = getConfig();
        configFile = new File(getDataFolder(), "config.yml");

        playerDataFile = new File(getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("无法创建玩家数据文件" + e.getMessage());
            }
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        loadPlayerData();

        protocolManager = ProtocolLibrary.getProtocolManager();

        Objects.requireNonNull(getCommand("ab")).setExecutor(new ABCommand(this));
        Objects.requireNonNull(getCommand("abreload")).setExecutor(new ABReloadCommand(this));

        Bukkit.getPluginManager().registerEvents(this, this);

        new ActionBarTask().runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        savePlayerData();
        getLogger().info("ActionBarMsg 已禁用");
    }

    private void loadPlayerData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean show = playerData.getBoolean(player.getUniqueId().toString(), true);
            showActionBarMap.put(player, show);
        }
    }

    private void savePlayerData() {
        for (Map.Entry<Player, Boolean> entry : showActionBarMap.entrySet()) {
            playerData.set(entry.getKey().getUniqueId().toString(), entry.getValue());
        }
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("无法保存玩家数据" + e.getMessage());
        }
    }

    public void setShowActionBar(Player player, boolean show) {
        showActionBarMap.put(player, show);
        savePlayerData();
    }

    public boolean isShowActionBar(Player player) {
        return showActionBarMap.getOrDefault(player, true);
    }

    public void reloadConfigContent() {
        reloadConfig();
        config = getConfig();
        try {
            config.save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("无法保存配置文件" + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean show = playerData.getBoolean(player.getUniqueId().toString(), true);
        showActionBarMap.put(player, show);
    }

    private class ActionBarTask extends BukkitRunnable {
        @Override
        public void run() {
            String actionBarMessage = config.getString("actionbar-message", "Default Message");
            new Thread(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isShowActionBar(player)) {
                        String parsedMessage = actionBarMessage;
                        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            parsedMessage = PlaceholderAPI.setPlaceholders(player, actionBarMessage);
                        }
                        sendActionBarMessage(player, parsedMessage);
                    }
                }
            }).start();
        }
    }

    private void sendActionBarMessage(Player player, String message) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', message)));
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            Bukkit.getLogger().severe("无法发送 ActionBar 消息给玩家 " + player.getName() + ": " + e.getMessage());
        }
    }
}