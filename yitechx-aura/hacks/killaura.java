import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class EnhancedKillauraMod {
    private static final Minecraft mc = Minecraft.getInstance();
    private boolean isActive = false; // dokunmayın
    private double attackRange = 5.0; // Saldırı mesafesi max: 10.0
    private int maxTargets = 90; // Maksimum hedef sayısı

    @SubscribeEvent
    public void onKeyPress(TickEvent.ClientTickEvent event) {
        // R tuşuna basıldığını kontrol et
        if (mc.gameSettings.keyBindUseItem.isPressed()) {
            isActive = !isActive; // Killaura'yı aç/kapa
            mc.player.sendMessage(new StringTextComponent(isActive ? "Killaura aktif!" : "Killaura pasif!"), mc.player.getUUID());
        }

        if (isActive) {
            attackFilteredEnemies();
        }
    }

    private void attackFilteredEnemies() {
        List<Entity> targets = mc.world.getEntitiesWithinAABB(Entity.class, 
                new AxisAlignedBB(mc.player.getPosition()).grow(attackRange))
                .stream()
                .filter(entity -> entity instanceof PlayerEntity && !entity.equals(mc.player))
                .sorted(Comparator.comparingDouble(entity -> entity.getDistance(mc.player)))
                .limit(maxTargets)
                .collect(Collectors.toList());

        for (Entity target : targets) {
            mc.player.attackTargetEntityWithCurrentItem(target);
        }
    }
}