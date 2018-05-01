package cz.cuni.mff.kocur.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.exceptions.LoadingError;

/**
 * <p>
 * This class should take file that contains navigation and height map of the
 * game world and load the data into memory and represent them in a way that is
 * usable later.
 * </p>
 * 
 * <p>
 * It needs to load:
 * <ul>
 * <li>navigation map - blocked and traversable positions</li>
 * <li>height map - heights for every grid position</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Grid file looks like this: </br>
 * There are sections that are divided by section names (#heights, #navmap..).
 * Each section should be parsed regarding its name.
 *
 * @author Banzindun
 *
 */
public class GridLoader {
	private Logger logger = LogManager.getLogger(GridLoader.class.getName());

	/**
	 * Path to the file with grid informations.
	 */
	private String path = null;

	/**
	 * Scanner that we use for reading the input.
	 */
	private Scanner s = null;

	/**
	 * Array containing the sections. Last is just filler that should not match
	 * anything (last state).
	 */
	private String[] sections = { "#header", "#navmap", "#heightmap" };

	/**
	 * Index of the section we are expecting.
	 */
	private int nextSectionIndex = 0;

	/**
	 * Navigation grid composed of tiles.
	 */
	private GridBase grid = GridBase.getInstance();
	
	/**
	 * Constructor.
	 * 
	 * @param path
	 *            Path of the file, where all the data should lie.
	 * @throws FileNotFoundException
	 *             Thrown when the file is not found on the supplied path.
	 */
	public GridLoader(String path) throws FileNotFoundException {
		this.path = path;

		loadFile();
	}

	/**
	 * Loads the file and initializes the scanner that we use for reading the input.
	 * 
	 * @throws FileNotFoundException
	 *             Thrown when the file is not found on the path supplied in
	 *             constructor.
	 */
	private void loadFile() throws FileNotFoundException {
		if (path == null)
			throw new FileNotFoundException("You have got me a null path.");
		s = new Scanner(new File(path));
	}

	/**
	 * Parses the data. </br>
	 * Goes line by line and parses every section name if finds.
	 * 
	 * @throws LoadingError
	 *             Thrown when there are some missing data.
	 */
	public void parse() throws LoadingError {
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (line.equals(sections[nextSectionIndex])) {
				parseSectionName();
			}
			// Else we take another line
		}

		// And we close the stream.
		s.close();
	}

	/**
	 * Checks the section name and calls the appropriate function that should handle
	 * the input.
	 * 
	 * @param name
	 *            Name of the section that should be parsed.
	 * @return Returns true if the name was successfully resolved and the
	 *         appropriate function - that parses the section input - called.
	 * @throws LoadingError
	 *             Thrown when there are some missing data.
	 */
	private void parseSectionName() throws LoadingError {
		switch (sections[nextSectionIndex]) {
		case "#header":
			parseHeader();
			break;
		case "#navmap":
			parseNavMap();
			break;
		case "#heightmap":
			parseHeightMap();
			break;
		default:
			break;
		}

		if (nextSectionIndex != sections.length - 1)
			nextSectionIndex++;
	}

	/**
	 * "X:[-1234, 1234]" "Y:[1234, -1234]"
	 */
	private void parseHeader() throws LoadingError {
		int minX = 0;
		int maxX = 0;
		int minY = 0;
		int maxY = 0;

		while (s.hasNextLine()) {
			String line = s.nextLine();

			if (line.equals("#")) {
				break;
			}
			// Else we parse ..

			// Check what the line starts with
			if (line.startsWith("Step:")) {
				grid.setResolution(Integer.parseInt(line.substring(5, line.length())));
			} else if (line.startsWith("X:")) {
				int[] xs = parseInts(line.substring(2, line.length()));
				minX = xs[0];
				maxX = xs[1];
			} else if (line.startsWith("Y:")) {
				int[] ys = parseInts(line.substring(2, line.length()));
				minY = ys[0];
				maxY = ys[1];
			}
		}

		if (minX == 0)
			logger.warn("Minimum of x is 0.");

		if (maxX == 0)
			logger.warn("Maximum of x is 0.");

		if (minY == 0)
			logger.warn("Minimum of y is 0.");

		if (maxY == 0)
			logger.warn("Maximum of y is 0.");

		if (minX >= maxX)
			throw new LoadingError("MinX is greater or equal to maxX. ");

		if (minY >= maxY)
			throw new LoadingError("MinY is greater or equal to maxY. ");

		if (minX > 0)
			throw new LoadingError("MinX is greater than 0. ");

		if (minY > 0)
			throw new LoadingError("MinY is greater than 0. ");

		grid.setSize((int) (1 + (Math.abs(minX) + Math.abs(maxX)) / grid.getResolution()),
				(int) (1 + (Math.abs(minY) + Math.abs(maxY)) / grid.getResolution()));
		grid.setXOrigin(minX);
		grid.setYOrigin(minY);
		
		grid.setMaxX(maxX);
		grid.setMinX(minX);
		grid.setMaxY(maxY);
		grid.setMinY(minY);
	}

	private int[] parseInts(String line) throws LoadingError {
		int[] ints = new int[2];

		// Remove the [ ] brackets
		String l = line.substring(1, line.length() - 1);

		// Split
		String[] fields = l.split(",");

		if (fields.length < 2) {
			throw new LoadingError("Trying to get 2 integers from line that has fewer ints separated by \",\".");
		}

		// Parse
		ints[0] = Integer.parseInt(fields[0].trim());
		ints[1] = Integer.parseInt(fields[1].trim());

		return ints;
	}

	private void parseNavMap() throws LoadingError {
		int y = 0;
		int lineCount = 0;

		while (s.hasNextLine()) {
			String line = s.nextLine();

			if (line.equals("#")) {
				break;
			}
			// Else we parse ..

			lineCount++;

			for (int x = 0; x < line.length(); x++) {

				byte t = Byte.parseByte(line.substring(x, x + 1));

				grid.setTile(x, y, t);
			}
			y++;
		}

		if (lineCount != grid.getHeight()) {
			throw new LoadingError("Height of grid and read lines are unequal.");
		}
	}

	private void parseHeightMap() throws LoadingError {
		int lineCount = 0;
		int y = 0;

		while (s.hasNextLine()) {
			String line = s.nextLine();

			if (line.equals("#")) {
				break;
			}
			// Else we parse ..

			lineCount++;

			String[] heights = line.split("\\s+");
			for (int x = 0; x < heights.length; x++) {
				short height = 1000;
				if (!heights[x].equals("x")) {
					height = Short.parseShort(heights[x]);
				}

				grid.setTileHeight(x, y, height);
			}

			y++;
		}

		if (lineCount != grid.getHeight()) {
			throw new LoadingError("Height of grid and read lines are not unequal.");
		}
	}
}
