package complex.utils.discordrpc.rpc.callbacks;

import com.sun.jna.Callback;

public interface SpectateGameCallback extends Callback {

	/**
	 * Method called when joining a game.
	 *
	 * @param spectateSecret Unique String containing information needed to let the player spectate.
	 */
	void apply(String spectateSecret);
}
