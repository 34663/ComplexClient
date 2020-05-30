package complex.utils.discordrpc;

import net.arikia.dev.drpc.*;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class MainRPC {
    private boolean running = true;
    private long created = 0;

    public void start() {
        this.created = System.currentTimeMillis();

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {

            @Override
            public void apply(DiscordUser user) {
                System.out.println("Websome " + user.username + "#" + user.discriminator + ".");
                update("Booting up...", "");
            }
        }).build();

        DiscordRPC.discordInitialize("696571819305730098", handlers, true);

        new Thread("DiscordRPC Callback") {
            @Override
            public void run() {
                while (running) {
                    DiscordRPC.discordRunCallbacks();
                }
            }
        }.start();
    }

    public void shutdown() {
        running = false;
        DiscordRPC.discordShutdown();
    }

    public void update(String firstline, String secondline) {
        DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondline);
        b.setBigImage("check", "");
        b.setDetails(firstline);
        b.setStartTimestamps(created);
        DiscordRPC.discordUpdatePresence(b.build());
    }
}
