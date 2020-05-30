package complex.utils.discordrpc.rpc;

public final class OSUtil {

	public boolean isMac() {
		return getOS().toLowerCase()
				.startsWith("mac");
	}

	public boolean isWindows() {
		return getOS().toLowerCase()
				.startsWith("win");
	}

	public String getOS() {
		return System.getProperty("os.name").toLowerCase();
	}

}