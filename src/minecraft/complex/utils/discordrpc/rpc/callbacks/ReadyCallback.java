package complex.utils.discordrpc.rpc.callbacks;

import com.sun.jna.Callback;
import complex.utils.discordrpc.rpc.DiscordUser;

public interface ReadyCallback extends Callback {

	/**
	 * Method called when the connection to Discord has been established.
	 *
	 * @param user Object containing all required information about the user executing the app.
	 * @see DiscordUser
	 **/
	void apply(DiscordUser user);
}

