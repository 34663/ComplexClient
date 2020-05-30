package complex.utils.discordrpc.rpc;

import com.sun.jna.Structure;
import complex.utils.discordrpc.rpc.callbacks.*;
import java.util.Arrays;
import java.util.List;

public class DiscordEventHandlers extends Structure {

	/**
	 * Callback called when Discord-RPC was initialized successfully.
	 */
	public ReadyCallback ready;
	/**
	 * Callback called when the Discord connection was disconnected.
	 */
	public DisconnectedCallback disconnected;
	/**
	 * Callback called when a Discord error occurred.
	 */
	public ErroredCallback errored;
	/**
	 * Callback called when the player joins the game.
	 */
	public JoinGameCallback joinGame;
	/**
	 * Callback called when the player spectates a game.
	 */
	public SpectateGameCallback spectateGame;
	/**
	 * Callback called when a join request is received.
	 */
	public JoinRequestCallback joinRequest;

	@Override
	public List<String> getFieldOrder() {
		return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
	}

	public static class Builder {

		DiscordEventHandlers h;

		public Builder() {
			h = new DiscordEventHandlers();
		}

		public Builder setReadyEventHandler(ReadyCallback r) {
			h.ready = r;
			return this;
		}

		public Builder setDisconnectedEventHandler(DisconnectedCallback d) {
			h.disconnected = d;
			return this;
		}

		public Builder setErroredEventHandler(ErroredCallback e) {
			h.errored = e;
			return this;
		}

		public Builder setJoinGameEventHandler(JoinGameCallback j) {
			h.joinGame = j;
			return this;
		}

		public Builder setSpectateGameEventHandler(SpectateGameCallback s) {
			h.spectateGame = s;
			return this;
		}

		public Builder setJoinRequestEventHandler(JoinRequestCallback j) {
			h.joinRequest = j;
			return this;
		}

		public DiscordEventHandlers build() {
			return h;
		}
	}
}
