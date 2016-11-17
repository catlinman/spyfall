package com.catlinman.spyfall;

public final class StringUtilities {
	// Empty constructor to avoid outside initialization.
	private StringUtilities() {}

	/**
	 * Converts a string to have each word capitalized. Also removes leading and trailing spaces.
	 * @return A new string converted from the input String.
	 */
	public static String capitalize(String in) {
		String[] arr    = in.trim().split(" ");
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < arr.length; i++)
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");

		return sb.toString().trim();
	}

}
