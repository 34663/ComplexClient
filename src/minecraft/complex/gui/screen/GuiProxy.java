package complex.gui.screen;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiProxy extends GuiScreen {
    private GuiScreen guiScreen;
    private GuiTextField proxyField;
    private String status;

    public GuiProxy(final GuiScreen screen) {
        this.status = "";
        this.guiScreen = screen;
    }

    @Override
    public void updateScreen() {
        this.proxyField.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, "Cancel"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 72 + 12, "Connect"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 96 + 12, "Reset"));
        (this.proxyField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20)).setFocused(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(final GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == 0) {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
            } else if (guiButton.id == 1) {
                if (!this.proxyField.getText().contains(":") || this.proxyField.getText().split(":").length != 2) {
                    this.status = "Not a proxy!";
                    return;
                }
                final String[] split = this.proxyField.getText().split(":");
                if (Integer.parseInt(split[1]) > 65536 || Integer.parseInt(split[1]) < 0) {
                    this.status = "Invalid port!";
                    return;
                }
                try {
                    System.setProperty("socksProxyHost", split[0]);
                    System.setProperty("socksProxyPort", split[1]);
                } catch (Exception ex) {
                    this.status = ex.toString();
                }
            } else if (guiButton.id == 2) {
                System.setProperty("socksProxyHost", "");
                System.setProperty("socksProxyPort", "");
            }
        }
    }

    @Override
    protected void keyTyped(final char p_146201_1_, final int p_146201_2_) {
        this.proxyField.textboxKeyTyped(p_146201_1_, p_146201_2_);
    }

    @Override
    protected void mouseClicked(final int n, final int n2, final int n3) throws IOException {
        super.mouseClicked(n, n2, n3);
        this.proxyField.mouseClicked(n, n2, n3);
        if (this.proxyField.isFocused()) {
            this.status = "";
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Use Proxy", this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, "IP:Port (must be a SOCKS proxy)", this.width / 2 - 100, 47, 10526880);
        this.drawCenteredString(this.fontRendererObj, this.status, this.width / 2, 87, 16711680);
        String string = String.valueOf(System.getProperty("socksProxyHost")) + ":" + System.getProperty("socksProxyPort");
        if (string.equals(":") || string.equals("null:null")) {
            string = "none";
        }
        this.drawString(this.fontRendererObj, "Current proxy: " + string, this.width / 2 - 100, 97, 10526880);
        this.proxyField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
