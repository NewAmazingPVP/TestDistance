package mc.testdistance;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class TestDistance extends JavaPlugin implements Listener {

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        luckPerms = LuckPermsProvider.get();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("setview")) {
            if (args.length != 2) {
                player.sendMessage("Usage: /setview <render_distance> <simulation_distance>");
                return true;
            }

            if (!player.hasPermission("customview.set")) {
                player.sendMessage("You don't have permission to use this command.");
                return true;
            }

            int renderDistance;
            int simulationDistance;
            try {
                renderDistance = Integer.parseInt(args[0]);
                simulationDistance = Integer.parseInt(args[1]);

                if (renderDistance < 0 || renderDistance > 33 || simulationDistance < 0 || simulationDistance > 33) {
                    player.sendMessage("Render and simulation distances should be integers in the range [1, 32].");
                    return true;
                }
            } catch (Exception e) {
                player.sendMessage("An error occurred while setting the distances. Render and simulation distances should be integers in the range [1, 32]. Please try again.");
                getLogger().log(Level.SEVERE, "Error setting distances for player " + player.getName(), e);
                return true;
            }

            player.setViewDistance(renderDistance);
            player.setSimulationDistance(simulationDistance);
            player.sendMessage("Your render distance has been set to " + ChatColor.AQUA + renderDistance + ChatColor.WHITE + " and simulation distance to " + ChatColor.AQUA + simulationDistance + ChatColor.WHITE + ".");
            getLogger().info("Set render distance to " + renderDistance + " and simulation distance to " + simulationDistance + " for player " + player.getName());

            /*// Remove previous permissions for view and simulation distances
            for (int i = 1; i <= 33; i++) {
                String renderPermission = "customview.render." + i;
                String simulationPermission = "customview.simulation." + i;
                luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> {
                    user.data().remove(Node.builder(renderPermission).build());
                    user.data().remove(Node.builder(simulationPermission).build());
                });
            }

            // Grant permissions for the set distances
            String renderPermission = "customview.render." + renderDistance;
            String simulationPermission = "customview.simulation." + simulationDistance;
            Node renderNode = Node.builder(renderPermission).build();
            Node simulationNode = Node.builder(simulationPermission).build();
            luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> {
                user.data().add(renderNode);
                user.data().add(simulationNode);
            });*/


            return true;
        }
        return false;
    }

    /*@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int defaultRenderDistance = 10; // Default view distance
        int defaultSimulationDistance = 5; // Default simulation distance

        User luckPermsUser = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (luckPermsUser != null) {
            for (int i = 33; i >= 1; i--) {
                String renderPermission = "customview.render." + i;
                if (luckPermsUser.getCachedData().getPermissionData().checkPermission(renderPermission).asBoolean()) {
                    defaultRenderDistance = i;
                    break;
                }
            }

            for (int i = 33; i >= 1; i--) {
                String simulationPermission = "customview.simulation." + i;
                if (luckPermsUser.getCachedData().getPermissionData().checkPermission(simulationPermission).asBoolean()) {
                    defaultSimulationDistance = i;
                    break;
                }
            }
        }

        player.setViewDistance(defaultRenderDistance);
        player.setSimulationDistance(defaultSimulationDistance);
        player.sendMessage("Your server side render distance has been set to " + ChatColor.AQUA + defaultRenderDistance + ChatColor.WHITE + " and simulation distance to " + ChatColor.AQUA + defaultSimulationDistance + ChatColor.WHITE + ". Change it by doing /setview <render_distance> <simulation_distance>");
        getLogger().info("Set render distance to " + defaultRenderDistance + " and simulation distance to " + defaultSimulationDistance + " for player " + player.getName());
    }*/
    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack newArmorPiece = event.getNewItem();

        // Check if the new armor piece is an elytra
        if (newArmorPiece != null && isElytra(newArmorPiece)) {
            player.getInventory().setChestplate(null);
            player.getInventory().addItem(new ItemStack(Material.ELYTRA));
            player.sendMessage(ChatColor.RED + "You cannot equip an elytra!");
        }
    }

    private boolean isElytra(ItemStack item) {
        return item.getType().toString().toLowerCase().contains("elytra");
    }



}
