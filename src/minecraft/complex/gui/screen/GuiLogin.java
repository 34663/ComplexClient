package complex.gui.screen;

import complex.Complex;
import complex.gui.altmanager.PasswordField;
import complex.utils.login.LoginUtils;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import complex.utils.timer.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

public class GuiLogin extends GuiScreen {
    private GuiTextField hwid;
    private String status;

    public GuiLogin() {
        this.status = TextFormatting.GRAY + "Idle...";
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                this.mc.shutdown();
                break;
            case 0:
                String pass = hwid.getText();
                System.out.println(LoginUtils.LoginThread());
                if (pass.length() == 0 || pass == null) {
                    status = TextFormatting.RED+"The information is not entered.";
                    return;
                }
                try {
                    final String hwid = LoginUtils.getHWID();
                    if (pass.equalsIgnoreCase("a")) {
                        Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
                    } else {
                        status = TextFormatting.RED + "LoginFailed.";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    status = TextFormatting.RED + "Error.";
                }
                break;
        }
    }

    public void drawScreen(int i, int j, float f) {
        this.drawDefaultBackground();
        this.hwid.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, "Login", this.width / 2, 20, -1);
        if (this.hwid.getText().isEmpty() && !this.hwid.isFocused()) {
            this.drawString(this.mc.fontRendererObj, "CODE", this.width / 2 - 96, 96, -7829368);
        }

        this.drawCenteredString(this.fontRendererObj, this.status, this.width / 2, 30, -1);
        super.drawScreen(i, j, f);
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 92 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 116 + 12, "Shutdown"));
        this.hwid = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 100, 90, 200, 20);
    }

    protected void keyTyped(char par1, int par2) {
        this.hwid.textboxKeyTyped(par1, par2);
        if (par1 == '\t' && this.hwid.isFocused()) {
            this.hwid.setFocused(!this.hwid.isFocused());
        }

        if (par1 == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    protected void mouseClicked(int par1, int par2, int par3) {
        try {
            super.mouseClicked(par1, par2, par3);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        this.hwid.mouseClicked(par1, par2, par3);
    }
}
