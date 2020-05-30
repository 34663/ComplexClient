package complex.utils.discordrpc.rpc.callbacks;

import com.sun.jna.Callback;

public interface ErroredCallback extends Callback {

	/**
	 * Method called when a error occurs.
	 *
	 * @param errorCode Error code returned.
	 * @param message   Message containing details about the error.
	 */
	void apply(int errorCode, String message);
}
