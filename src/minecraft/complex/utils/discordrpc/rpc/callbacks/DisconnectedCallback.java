package complex.utils.discordrpc.rpc.callbacks;

import com.sun.jna.Callback;

public interface DisconnectedCallback extends Callback {

	/**
	 * Method called when disconnected.
	 *
	 * @param errorCode Error code returned on disconnection.
	 * @param message   Message containing details about the disconnection.
	 */
	void apply(int errorCode, String message);
}
