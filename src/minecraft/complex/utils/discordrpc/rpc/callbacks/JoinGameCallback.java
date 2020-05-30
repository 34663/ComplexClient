package complex.utils.discordrpc.rpc.callbacks;

import com.sun.jna.Callback;

public interface JoinGameCallback extends Callback {

	/**
	 * Method called when joining a game.
	 *
	 * @param joinSecret Unique String containing information needed to let the player join.
	 */
	void apply(String joinSecret);
}
