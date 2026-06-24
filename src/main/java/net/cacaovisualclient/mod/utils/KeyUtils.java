package net.cacaovisualclient.mod.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

@UtilityClass
public class KeyUtils {

    public boolean isKeyPressed(int key) {
        return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().handle(), key) == GLFW.GLFW_PRESS;
    }
}
