package dragon.katana.dragonkatana;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.bukkit.util.Vector;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.PluginBase;


import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class DragonKatana extends JavaPlugin {

    private static Plugin plugin;

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {

        plugin = this;


        getServer().getPluginManager().registerEvents(new MyListener(), this);

        ItemStack dragonkatana = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta dkatanaMeta = dragonkatana.getItemMeta();
        dkatanaMeta.setDisplayName(ChatColor.DARK_PURPLE + "§5§l§kS§r §d§lDragon Katana §5§kS§r");
        dkatanaMeta.setUnbreakable(true);
        dkatanaMeta.setFireResistant(true);
        dkatanaMeta.getPersistentDataContainer().set(new NamespacedKey(this, "Unmodifiable"), PersistentDataType.BYTE, (byte) 1);
        dkatanaMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        dkatanaMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        dkatanaMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        dkatanaMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        dkatanaMeta.setRarity(ItemRarity.EPIC);

        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.DARK_PURPLE + "§rMythical sword forged from the egg of the dragon and the core of a beast.");
        lore.add(" ");
        lore.add(ChatColor.DARK_PURPLE + "Right click: Launch yourself forwards with the power of the dragon while gaining regeneration for 3 seconds.");

        dkatanaMeta.setLore(lore);

        dkatanaMeta.addEnchant(Enchantment.SHARPNESS, 5, true);
        dkatanaMeta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
        dkatanaMeta.addEnchant(Enchantment.LOOTING , 3, true);
        dkatanaMeta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);

        dkatanaMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attackDamage", 10, AttributeModifier.Operation.ADD_NUMBER));


        dkatanaMeta.setCustomModelData(1);


        dragonkatana.setItemMeta(dkatanaMeta);

        ShapedRecipe dkatanaRecipe = new ShapedRecipe(new NamespacedKey(this, "dragon_katana"), dragonkatana);
        //E = dragon egg   S = nether star   B = blaze rod
        dkatanaRecipe.shape("  E", " N ", "S  ");
        dkatanaRecipe.setIngredient('E', Material.DRAGON_EGG);
        dkatanaRecipe.setIngredient('S', Material.NETHER_STAR);
        dkatanaRecipe.setIngredient('N', Material.NETHERITE_SWORD);

        Bukkit.addRecipe(dkatanaRecipe);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }






    public class MyListener implements Listener
    {

        public boolean canDash = true;



        @EventHandler
        private void onPlayerRightClick(PlayerInteractEvent event)
        {



            Player player = event.getPlayer();
            if(event.getAction().isRightClick() && canDash)
            {
                if(event.getHand() == EquipmentSlot.HAND)
                {
                    if(event.getItem() != null)
                    {
                        ItemStack heldItem = player.getInventory().getItemInMainHand();
                        ItemMeta itemMeta = heldItem.getItemMeta();




                        if(itemMeta != null && itemMeta.hasLore())
                        {
                            Vector direction = player.getLocation().getDirection();

                            double dashMagnitude = 3.0;
                            Vector dashVelocity = direction.multiply(dashMagnitude);

                            //PotionEffect slowfall = new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 0);
                            //player.addPotionEffect(slowfall);



                            player.setVelocity(dashVelocity);


                            Location location = player.getLocation();
                            player.spawnParticle(Particle.DRAGON_BREATH, location, 100, 0.5, 0.5, 0.5, 0.1);


                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () ->
                            {
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                            }, 5);

                            canDash = false;


                            Material heldItemType = player.getInventory().getItemInMainHand().getType();
                            int cooldownTicks = 300;

                            player.setCooldown(heldItemType, cooldownTicks);

                            PotionEffect effect = new PotionEffect(PotionEffectType.REGENERATION, 60, 1);
                            player.addPotionEffect(effect);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () ->
                            {
                                canDash = true;
                            }, 300);





                        }



                    }
                }
            }
        }
    }




}
