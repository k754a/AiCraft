package net.kallens.aiminecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = "aiminecraft", value = Dist.CLIENT)
public class ClientEvents  {


    @SubscribeEvent
    public static void onTitleScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof TitleScreen)) return;

        GuiGraphics gui = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
String splash = "+ AI";
       // String notice = "--This Mod needs Ollama to run--";


        // vanilla base coords (unscaled)
        float rawX = mc.getWindow().getGuiScaledWidth() / 2f + 90f;
        float rawY = 70f;

        // high-res time cycle [0,1)
        double periodNs = 2_000_000_000.0;
        double t = (System.nanoTime() % periodNs) / periodNs;


        float baseScale = 3;



        float bob = (float)(Math.sin(t * 2 * Math.PI) * 4.0);


        float textWidth = mc.font.width(splash);


       // float noticetextWidth = mc.font.width(notice);

        PoseStack pose = gui.pose();
        pose.pushPose();

        pose.translate(rawX, rawY + bob, 0);
        // scale around that center
        pose.scale(baseScale, baseScale, 1f);

        // compute int coords so drawString resolves
        int xi = Math.round(-textWidth / 2f);
        int yi = 0;


        gui.drawString(
                mc.font,
                Component.literal(splash),
                xi,
                yi,
                0xFFFFFF
        );


//        gui.drawString(
//                mc.font,
//                Component.literal(notice),
//                -20,
//                -20,
//                0xFFFFFF
//        );


        pose.popPose();
    }


    // keep track of which TitleScreen instances we've patched
    private static final WeakHashMap<TitleScreen, Boolean> patchedScreens = new WeakHashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (!(Minecraft.getInstance().screen instanceof TitleScreen screen)) return;

        if (!patchedScreens.containsKey(screen)) {
            try {
                Field splashField = TitleScreen.class.getDeclaredField("splash");
                splashField.setAccessible(true);

                SplashRenderer customSplash = new SplashRenderer("");
                splashField.set(screen, customSplash);

                patchedScreens.put(screen, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
