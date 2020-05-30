package complex.command.impl;

import complex.Complex;
import complex.command.Command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDHistory extends Command {
    public IDHistory() {
        super("IDHistory", null);
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            sendChatMessageInfo(".idhistory <playerName>");
            return;
        }

        String name = args[0];
        try {
            this.IdCheck(name);
        } catch (Exception e) {
            sendChatMessageError("ID NotFound!");
        }
    }

    public void IdCheck(String paramString) {
        new Thread(() -> {
            try {
                URL uRL = new URL("https://api.mojang.com/users/profiles/minecraft/" + paramString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) uRL.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 200) {
                    Matcher matcher = Pattern.compile("\"id\":\"\\w+\"").matcher((new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))).readLine());
                    if (!matcher.find())
                        return;
                    String str = matcher.group().replaceAll("\"", "").replace("id:", "");
                    uRL = new URL("https://api.mojang.com/user/profiles/" + str + "/names");
                    httpURLConnection = (HttpURLConnection) uRL.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();
                    if (httpURLConnection.getResponseCode() == 200) {
                        String str1 = (new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))).readLine();
                        Matcher matcher1 = Pattern.compile("\"name\":\"\\w+\"").matcher(str1);
                        while (matcher1.find())
                            Command.sendChatMessage(matcher1.group().replaceAll("\"name\":\"|\"", ""));
                    }
                }
            } catch (Exception e) {
                sendChatMessageError("ID NotFound!");
            }
        }).start();
    }
}
