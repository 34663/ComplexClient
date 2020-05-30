package complex.gui.altmanager.althening;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import complex.Complex;
import complex.gui.altmanager.*;
import complex.gui.altmanager.althening.althening.AltService;
import complex.gui.altmanager.althening.althening.api.TheAltening;
import complex.utils.MiscUtils;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import java.io.IOException;
import java.util.Arrays;

import static com.mojang.authlib.Agent.MINECRAFT;
import static java.net.Proxy.NO_PROXY;

public class GuiAlthening extends GuiScreen {
    private final GuiAltManager manager;
    public String apikey = "";
    private GuiButton login;
    private GuiButton generate;
    private GuiButton cancell;
    private AltLoginThread thread;

    private PasswordField apikeyField;
    private GuiTextField tokenField;

    public String status = "";

    public GuiAlthening(GuiAltManager manager) {
        this.manager = manager;
    }

    public void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled)
            return;

        switch (button.id) {
            case 0:
                mc.displayGuiScreen(manager);
                break;
            case 1:
//                login.enabled = false;
//                generate.enabled = false;
//                apikey = apikeyField.text;
//                TheAltening altening = new TheAltening(apikey);
//
//                try {
//                    GuiAltManager.altService.switchService(AltService.EnumAltService.THEALTENING);
//                    status = "§cLogging in...";
//
//                    YggdrasilUserAuthentication yggdrasilUserAuthentication = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(NO_PROXY, ""), MINECRAFT);
//                    yggdrasilUserAuthentication.setUsername(altening.getAccountData().getToken());
//                    yggdrasilUserAuthentication.setPassword("Password");
//
//                    try {
//                        yggdrasilUserAuthentication.logIn();
//                        mc.session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "mojang");
//
//                        System.out.println(altening.getAccountData().getToken());
//                        System.out.println(altening.getAccountData().getInfo().getHypixelLevel());
//                        System.out.println(altening.getAccountData().getInfo().getHypixelRank());
//                        manager.status = "§fYour name is now §a" + yggdrasilUserAuthentication.getSelectedProfile().getName() + "§f.";
//                        mc.displayGuiScreen(manager);
//                    } catch (AuthenticationException e) {
//                        GuiAltManager.altService.switchService(AltService.EnumAltService.MOJANG);
//                        status = "§cFailed to login.";
//                        login.enabled = true;
//                        generate.enabled = true;
//                    }
//                } catch (Throwable throwable) {
//                    status = "§cFailed to login. Unknown error.";
//                    login.enabled = true;
//                    generate.enabled = true;
//                }

                login.enabled = false;
                generate.enabled = false;
                apikey = apikeyField.text;

                TheAltening altening = new TheAltening(apikey);
                TheAltening alt = null;
                TheAltening.Asynchronous asynchronous = new TheAltening.Asynchronous(altening);
                status = "§cGenerating account...";

                asynchronous.getAccountData().thenAccept(account -> {
                    status = "§aGenerated account: " + account.getUsername();

                    try {
                        status = "§cSwitching Alt Service...";

                        // Change Alt Service
                        GuiAltManager.altService.switchService(AltService.EnumAltService.THEALTENING);
                        status = "§cLogging in...";

                        // Set token as username
                        YggdrasilUserAuthentication yggdrasilUserAuthentication = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(NO_PROXY, ""), MINECRAFT);
                        yggdrasilUserAuthentication.setUsername(account.getToken());
                        yggdrasilUserAuthentication.setPassword("Password");

                        try {
                            yggdrasilUserAuthentication.logIn();
                            mc.session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "mojang");

                            System.out.println(altening.getAccountData().getToken());
                            System.out.println(altening.getAccountData().getInfo().getHypixelLevel());
                            System.out.println(altening.getAccountData().getInfo().getHypixelRank());
                            manager.status = "§fYour name is now §a" + yggdrasilUserAuthentication.getSelectedProfile().getName() + "§f.";
                            mc.displayGuiScreen(manager);
                        } catch (AuthenticationException e) {
                            GuiAltManager.altService.switchService(AltService.EnumAltService.MOJANG);

                            Complex.getLogger().error("Failed to login.", e);
                        }
                    } catch (Throwable throwable) {
                        status = "§cFailed to login. Unknown error.";
                        Complex.getLogger().error("Failed to login.", throwable);
                    }

                    login.enabled = true;
                    generate.enabled = true;
                });
                break;
            case 2:
                login.enabled = false;
                generate.enabled = false;

                try {
                    status = "§cSwitching Alt Service...";

                    // Change Alt Service
                    GuiAltManager.altService.switchService(AltService.EnumAltService.THEALTENING);
                    status = "§cLogging in...";

                    // Set token as username
                    YggdrasilUserAuthentication yggdrasilUserAuthentication = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(NO_PROXY, ""), MINECRAFT);
                    yggdrasilUserAuthentication.setUsername(tokenField.text);
                    yggdrasilUserAuthentication.setPassword("Password");

                    try {
                        yggdrasilUserAuthentication.logIn();
                        mc.session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "mojang");

                        manager.status = "§fYour name is now §a" + yggdrasilUserAuthentication.getSelectedProfile().getName() + "f.";
                        mc.displayGuiScreen(manager);
                    } catch (AuthenticationException e) {
                        GuiAltManager.altService.switchService(AltService.EnumAltService.MOJANG);
                        status = "§cFailed to login.";
                        login.enabled = true;
                        generate.enabled = true;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    status = "§cThat Token cannot be used.";
                    login.enabled = true;
                    generate.enabled = true;
                }
                break;
            case 3:
                MiscUtils.showURL("https://thealtening.com/");
        }
        super.actionPerformed(button);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer font = mc.fontRendererObj;
        drawBackground(0);
        RenderingUtils.drawBorderedRect(30, 30, width - 30, height - 30, 1.0F, Colors.getColor(225, 50), Colors.getColor(160, 150));

        drawCenteredString(font, "TheAltening", width / 2, 6, 0xffffff);
        drawCenteredString(font, status, width / 2, 18, 0xffffff);

        apikeyField.drawTextBox();
        tokenField.drawTextBox();
        if (this.apikeyField.getText().isEmpty() && !this.apikeyField.isFocused()) {
            this.drawString(this.mc.fontRendererObj, "Api-Key", this.width / 2 - 96, 156, -7829368);
        }
        if (this.tokenField.getText().isEmpty() && !this.tokenField.isFocused()) {
            this.drawString(this.mc.fontRendererObj, "Token", this.width / 2 - 96, 86, -7829368);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        FontRenderer font = mc.fontRendererObj;
        Keyboard.enableRepeatEvents(true);

        login = new GuiButton(2, width / 2 - 100, 105, "Login");
        buttonList.add(login);
        generate = new GuiButton(1, width / 2 - 100, 175, "Generate");
        buttonList.add(generate);
        buttonList.add(new GuiButton(3, width / 2 - 100, height - 83, "Buy"));
        buttonList.add(new GuiButton(0, width / 2 - 100, height - 60, "Back"));
        tokenField = new GuiTextField(666, font, width / 2 - 100, 80, 200, 20);
//        tokenField.isFocused = true;
        tokenField.maxStringLength = Integer.MAX_VALUE;
        apikeyField = new PasswordField(font, width / 2 - 100, 150, 200, 20);
        apikeyField.maxStringLength = 18;
        apikeyField.text = apikey;

        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // Check if user want to escape from screen
        if (Keyboard.KEY_ESCAPE == keyCode) {
            // Send back to prev screen
            mc.displayGuiScreen(manager);
            return;
        }

        // Check if field is focused, then call key typed
        if (apikeyField.isFocused) apikeyField.textboxKeyTyped(typedChar, keyCode);
        if (tokenField.isFocused) tokenField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Call mouse clicked to field
        apikeyField.mouseClicked(mouseX, mouseY, mouseButton);
        tokenField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        apikeyField.updateCursorCounter();
        tokenField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        // Disable keyboard repeat events
        Keyboard.enableRepeatEvents(false);

        // Set API key
        apikey = apikeyField.text;
        super.onGuiClosed();
    }

}
