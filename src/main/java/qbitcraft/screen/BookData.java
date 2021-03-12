package qbitcraft.screen;

import java.io.IOException;

import qbitcraft.saveload.Load;

public class BookData {
	
	public static final String about = "Modded by David.b and +Dillyg10+ until 1.8, then taken over by " +
			"Chris J until 2.0.4, then by afyber, and then by chrisj42. " +
			"Qbitcraft goal is to expand Minicraft+ and use it as a Java learning sample for aspiring coders.\n" +
			"Minicraft was originally made by Markus Perrson for ludum dare 22 competition.";
	
	public static final String instructions = "With the default controls..." +
			"\n\nMove your character with arrow keys or WSAD. Press C to attack and X to open the inventory, " +
			"and to use items. Pickup furniture and torches with V. Select an item in the inventory to equip it.\n\n" +
			"The Goal: Defeat the air wizard!";
	
	public static final String antVenomBook = loadBook("antidous");
	public static final String storylineGuide = loadBook("story_guide");
	
	private static final String loadBook(String bookTitle) {
		String book;
		try {
			book = String.join("\n", Load.loadFile("/"+bookTitle+".txt"));
			book = book.replaceAll("\\\\0", "\0");
		} catch (IOException ex) {
			ex.printStackTrace();
			book = "";
		}
		
		return book;
	}
}
