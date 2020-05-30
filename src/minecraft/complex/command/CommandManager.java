package complex.command;

import complex.command.impl.IDHistory;
import complex.command.impl.Toggle;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager {
    public static ArrayList<Command> commands = new ArrayList<Command>();

    public void init_Command(){
        //ここにコマンドを追加
        addCommand(new Toggle());
        addCommand(new IDHistory());
    }

    public void addCommand(Command cmd){
        this.commands.add(cmd);
    }
    public ArrayList<Command> getCommands() {
        return commands;
    }
    public boolean execute(String text) {
        if (!text.startsWith("-")) {
            return false;
        }
        text = text.substring(1);
        String[] arguments = text.split(" ");
        for (Command cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(arguments[0])) {
                String[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
                cmd.execute(args);
                return true;
            }
        }
        Command.sendChatMessage("§c Invalid Command!");
        return false;
    }
}
