package com.catlinman.spyfall;

// Random generation utility imports.
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Utilities {
	// Empty constructor to avoid outside initialization.
	private Utilities() {}

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

	// Generic array shuffle.
	public static < T > void shuffle(T[] arr) {
		Random rand = new Random();

		for (int i = arr.length - 1; i > 0; i--) swap(arr, i, rand.nextInt(i + 1));
	}

	// Generic array item swap.
	public static < T > void swap(T[] arr, int i, int j) {
		T tmp = arr[i];

		arr[i] = arr[j];
		arr[j] = tmp;
	}

}
