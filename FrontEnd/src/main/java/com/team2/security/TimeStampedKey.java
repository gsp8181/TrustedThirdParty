package com.team2.security;

public class TimeStampedKey {
	private final String signedKey;
	private final long time;

	/**
	 * @param signedKey
	 * @param time
	 */
	public TimeStampedKey(String signedKey, long time) {
		this.signedKey = signedKey;
		this.time = time;
	}

	public String getSignedKey() {
		return signedKey;
	}

	public long getTime() {
		return time;
	}

}
