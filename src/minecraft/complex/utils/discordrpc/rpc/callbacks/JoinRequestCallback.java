package complex.utils.discordrpc.rpc.callbacks;

import com.sun.jna.Callback;
import complex.utils.discordrpc.rpc.DiscordUser;

public interface JoinRequestCallback extends Callback {

	/**
	 * Method called when another player requests to join a game.
	 *
	 * @param request Object containing all required information about the user requesting to join.
	 * @see DiscordUser
	 */
	void apply(DiscordUser request);
}
