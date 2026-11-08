package net.shadersmod.client;

public class ShaderOptionScreen extends ShaderOption {
    public ShaderOptionScreen(String name) {
        super(name, null, null, new String[0], null, null);
    }

    @Override
    public String getNameText() {
        return Shaders.translate("screen." + this.getName(), this.getName());
    }

    @Override
    public String getDescriptionText() {
        return Shaders.translate("screen." + this.getName() + ".comment", null);
    }
}
