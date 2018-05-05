package cz.cuni.mff.kocur.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import cz.cuni.mff.kocur.base.Utils;
import cz.cuni.mff.kocur.exceptions.LoadingError;
import cz.cuni.mff.kocur.interests.Shop;

/**
 * Class that loads items from items.data inside the working directory. Class
 * loads them and stores them inside ItemsBase.
 * 
 * @author kocur
 *
 */
public class ItemsLoader {
	// private static final Logger logger =
	// LogManager.getLogger(ItemsLoader.class.getName());

	/**
	 * Scanner that we use for reading the input.
	 */
	private Scanner scanner = null;

	/**
	 * Bot components. I will store them here and load them after all the items were
	 * loaded.
	 */
	private HashMap<String, String[]> botComponents = new HashMap<>();

	public ItemsLoader() {

	}

	/**
	 * Loads the file and initializes the scanner that we use for reading the input.
	 * 
	 * @param path
	 *            Path to file that should be loaded.
	 * 
	 * @throws FileNotFoundException
	 *             Thrown when the file is not found on the path supplied in
	 *             constructor.
	 */
	private void loadFile(String path) throws FileNotFoundException {
		if (path == null)
			throw new FileNotFoundException("Null path provided.");
		scanner = new Scanner(new File(path));
	}

	/**
	 * Loads items from file on specified path.
	 * 
	 * @param path
	 *            Path leading to items file.
	 * @throws LoadingError
	 *             Throws LoadingError if there was an error with input (badly
	 *             specified item).
	 * 
	 * @throws FileNotFoundException
	 *             If file was not found.
	 */
	public void loadItems(String path) throws FileNotFoundException, LoadingError {
		// Load scanner to Path
		loadFile(path);

		// Clear itemsBase to be sure it is empty
		ItemsBase.clear();

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			loadItem(line);
		}
		scanner.close();

		// After all the items are loaded, load their components.
		for (Entry<String, String[]> e : botComponents.entrySet()) {
			ItemsBase.loadItem(e.getKey(), e.getValue());
		}

	}

	/**
	 * Loads item from line and stores it inside ItemsBase.
	 * 
	 * @param line
	 *            Line that contains the item and that should be parsed.
	 * @throws LoadingError
	 *             Thrown if input data weren't in correct state.
	 */
	private void loadItem(String line) throws LoadingError {
		String[] fields = line.split("\\s");

		if (fields.length < 4) {
			throw new LoadingError("I have not loaded 4 fields. Some information in item is missing: " + line);
		}

		String name = fields[0];
		String[] components = Utils.parseArrayOfStrings(fields[3].substring(1, fields[3].length() - 1));

		botComponents.put(name, components);

		int price = 0;
		try {
			price = Integer.parseInt(fields[1]);
		} catch (NumberFormatException e) {
			throw new LoadingError("I couldn't parse price from supplied item data: " + fields[2]);
		}

		int shop = Shop.BASE;
		if (fields[2].equals("secret"))
			shop = Shop.SECRET;
		else if (fields[2].equals("sidesecret"))
			shop = Item.SIDESECRET;
		else if (fields[2].equals("baseside"))
			shop = Item.BASESIDE;

		// Preload the item
		ItemsBase.preloadItem(name, price, shop);
	}
}
