import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.datatransfer.*;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;

// NOTE: Do not delete any code that is commented out. I commented out a number of features since they weren't completely implemented yet.

public class ScrambleChecker extends JFrame implements ActionListener, MouseListener, KeyListener {

		/* Constants */

	static final int DEFAULT_CUBE_SIZE = 3;

		/* Variables */

	private Canvas expectedScramble, actualScramble;
	private JTextField[] expectedScrambleTextFields = new JTextField[5];
	private JTextField actualScrambleTextField;
	private JLabel expectedScrambleParsed, actualScrambleParsed, scrambleFileLabel;
	private File scrambleFile;
	private JCheckBox includeRotation;
	private JSpinner spinner;
	private JMenuBar menuBar;
	private boolean userHasChangedPuzzleStateByDrawing = false;


	public static void main(String[] args) {

		ScrambleChecker scrambleChecker = new ScrambleChecker();

	}

	public ScrambleChecker() {

			/* Add screen components */
		
		expectedScramble = new Canvas("Expected (official scramble)", new Cube(DEFAULT_CUBE_SIZE, true));
		add(expectedScramble, BorderLayout.WEST); 

		actualScramble = new Canvas("Actual (state obtained by mis-scramble)", new Cube(DEFAULT_CUBE_SIZE, false));
		add(actualScramble, BorderLayout.EAST);
		actualScramble.setFocusable(true);

		add(makeTopContainer(), BorderLayout.NORTH); 
		add(makeBottomContainer(), BorderLayout.SOUTH); 
		
		addMenu();
		
			/* Show the user our wonderful GUI */
		
		pack();
		setGUIproperties();

			/* Add mouse and keyboard listeners */

		addMouseListener(this);
		addKeyListener(this);

	}


	/* Set some default GUI properties */
	private void setGUIproperties() {
		
		// Ensure our application will be closed when the user presses the "X"
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Sets screen location in the center of the screen (only works after calling pack) and prevent resizing
		setResizable(false);
		setLocationRelativeTo(null);
		
		// Update Title
		setTitle("ScrambleChecker v1.0.1 (Tom Rokicki and Micah Stairs)");

		// Show Screen
		setVisible(true);
	}

	/* Create a container which allows the user to select the search settings, and a button to start the search */
	private Container makeTopContainer() {

			/* Setup */
		
		Container topContainer = new Container();
		topContainer.setLayout(new FlowLayout());

			/* Number of errors */
		
		topContainer.add(new JLabel(" Maximum number of errors to look for: "));
		SpinnerModel model =
        new SpinnerNumberModel(5, // initial value
                               1, // min
                               20, // max
                               1); // step
        spinner = new JSpinner(model);
        topContainer.add(spinner);

        	/* Cube rotation */

        includeRotation = new JCheckBox("Look for possible cube rotation during scramble (<4 errors is recommended): ");
        topContainer.add(includeRotation);

        	/* Search button */

        JButton button = new JButton("Search for Solution");
        button.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { generateSolution(); }});      
        topContainer.add(button);

        return topContainer;

	}

	/* Create a container which enables the user to type in scrambles to analyze */
	private Container makeBottomContainer() {

			/* Setup */
		
		Container bottomContainer = new Container();
		bottomContainer.setLayout(new GridLayout(0,2));
		bottomContainer.add(expectedScrambleParsed = new JLabel(" Interpretation: "));
		bottomContainer.add(actualScrambleParsed = new JLabel(" Interpretation: "));

			/* Textfield for official scramble */

		Container bottomLeftContainer = new Container();
        bottomLeftContainer.setLayout(new FlowLayout());
        bottomLeftContainer.add(new JLabel("Expected: "));
        expectedScrambleTextFields[0] = new JTextField(35);
		expectedScrambleTextFields[0].getDocument().addDocumentListener(new DocumentListener() {
		  public void changedUpdate(DocumentEvent e) { changed(); }
		  public void removeUpdate(DocumentEvent e) { changed(); }
		  public void insertUpdate(DocumentEvent e) { changed(); }
		  public void changed() {
		    expectedScrambleParsed.setText(" Interpretation: " + expectedScramble.cube.setState(expectedScrambleTextFields[0].getText()));
		    expectedScramble.repaint();
		  }
		});
        bottomLeftContainer.add(expectedScrambleTextFields[0]);
        bottomContainer.add(bottomLeftContainer);

        	/* Textfield for state obtained by mis-scramble */

        Container bottomRightContainer = new Container();
        bottomRightContainer.setLayout(new FlowLayout());
        bottomRightContainer.add(new JLabel("Actual: "));
        actualScrambleTextField = new JTextField(35);
		actualScrambleTextField.getDocument().addDocumentListener(new DocumentListener() {
		  public void changedUpdate(DocumentEvent e) { changed(); }
		  public void removeUpdate(DocumentEvent e) { changed(); }
		  public void insertUpdate(DocumentEvent e) { changed(); }
		  public void changed() {
		  	if (!userHasChangedPuzzleStateByDrawing) {
			    actualScrambleParsed.setText(" Interpretation: " + actualScramble.cube.setState(actualScrambleTextField.getText()));
			    actualScramble.repaint();
			} else 
				userHasChangedPuzzleStateByDrawing = false;
		  }
		});
        bottomRightContainer.add(actualScrambleTextField);
        bottomContainer.add(bottomRightContainer);	

        	/* Extra official scrambles */

		bottomContainer.add(new JLabel(" Other scrambles (optional):"));
		bottomContainer.add(new JLabel());
		for (int i = 1; i < 5; i++) {
			expectedScrambleTextFields[i] = new JTextField(35);
			bottomContainer.add(expectedScrambleTextFields[i]);
		}
		JButton button = new JButton("Get scrambles from file (1 per line)");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrambleFile = selectFile("Select Scrambles");
				if (scrambleFile == null)
					scrambleFileLabel.setText(" No file is selected");
				else
					scrambleFileLabel.setText(" Selected file: " + scrambleFile);
			}
		});      
        bottomContainer.add(button);
        scrambleFileLabel = new JLabel(" No file is selected");
        bottomContainer.add(scrambleFileLabel);
        

		return bottomContainer;
	}

	/* Adds the menubar to application */
	private void addMenu () {

			/* Setup */

		menuBar = new JMenuBar();
		JMenuItem menuItem;
		JMenu menu;

			/* File Menu */

		menu = new JMenu("File");
		menu.getAccessibleContext().setAccessibleDescription("File");
		menuBar.add(menu);

		menuItem = new JMenuItem("Quit");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Quit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		
			/* Puzzle Menu */

		/**
		menu = new JMenu("Puzzle");
		menu.getAccessibleContext().setAccessibleDescription("Puzzle");
		menuBar.add(menu);

		menuItem = new JMenuItem("2x2x2");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("3x3x3");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("4x4x4");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("5x5x5");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		**/

		this.setJMenuBar(menuBar);

	}

	/**
	* This method handles all of the actions triggered when the user interacts
	* with the main menu, or presses keyboard shortcuts to trigger those events.
	* @param event - the triggered event
	*/
	public void actionPerformed(ActionEvent event) {

	 	switch (event.getActionCommand()) {

	 		case "Quit":

	 			System.exit(0);
	 			break;

	 	/**
	 		case "2x2x2": case "3x3x3": case "4x4x4": case "5x5x5":

	 			int n = Integer.valueOf(event.getActionCommand().substring(0, 1));

	 			expectedScramble.cube = new Cube(n, true);
	 			actualScramble.cube = new Cube(n, false);

	 			expectedScramble.reset();
	 			actualScramble.reset();

	 			break;
		**/
	 	}

	 }

	public void mousePressed(MouseEvent e) { 

		// Prevents the scramble textfield from changing when the user begins to use keyboard to edit the puzzle
		this.requestFocusInWindow();

		int x = e.getX() - actualScramble.getX();
    	int y = e.getY() - actualScramble.getY() - getInsets().top - menuBar.getHeight();

    	actualScramble.select(x, y);

	}
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }

    public void keyPressed(KeyEvent e) { 

    	switch (e.getKeyCode()) {

    		case KeyEvent.VK_LEFT:
    			actualScramble.moveSelection(-1, 0);
    			break;

    		case KeyEvent.VK_RIGHT:
    			actualScramble.moveSelection(+1, 0);
    			break;

    		case KeyEvent.VK_UP:
    			actualScramble.moveSelection(0, -1);
    			break;

    		case KeyEvent.VK_DOWN:
    			actualScramble.moveSelection(0, +1);
    			break;

    		case KeyEvent.VK_B:
    			actualScramble.setSticker(Sticker.BLUE);
    			break;

    		case KeyEvent.VK_G:
    			actualScramble.setSticker(Sticker.GREEN);
    			break;

    		case KeyEvent.VK_O:
    			actualScramble.setSticker(Sticker.ORANGE);
    			break;

    		case KeyEvent.VK_R:
    			actualScramble.setSticker(Sticker.RED);
    			break;

    		case KeyEvent.VK_W:
    			actualScramble.setSticker(Sticker.WHITE);
    			break;

    		case KeyEvent.VK_Y:
    			actualScramble.setSticker(Sticker.YELLOW);
    			break;

    		case KeyEvent.VK_DELETE: case KeyEvent.VK_BACK_SPACE: case KeyEvent.VK_SPACE:
    			actualScramble.setSticker(Sticker.BLANK);
    			break;

    	}

    }

    public void keyReleased(KeyEvent e) { }
    public void keyTyped(KeyEvent e) { }

    /* Generate solution by using Tom Rokicki's program */
    private void generateSolution() {

    	int nErrors = (Integer) spinner.getValue();

    	try {

    		boolean solutionFound = false;

    			/* Check all 5 scrambles */
    		
    		for (int i = 0; i < 5; i++) {

    			// Get scramble
    			String scramble;
    			if (i == 0)
    				scramble = expectedScrambleParsed.getText().replace("Interpretation:", "");
    			else
    				scramble = expectedScrambleTextFields[i].getText();

    			// Skip empty scrambles
    			if (scramble.length() == 0)
    				continue;

    			// Run Tom's program with the proper flags set
				Process process;
				if (includeRotation.isSelected())
					 process = new ProcessBuilder(
						System.getProperty("user.dir") + "/movedelta",
						"-e" + nErrors,
						"-p" + actualScramble.cube.getState(),
						"-s" + scramble,
						"-m"
					).start();
				else
					 process = new ProcessBuilder(
						System.getProperty("user.dir") + "/movedelta",
						"-e" + nErrors,
						"-p" + actualScramble.cube.getState(),
						"-s" + scramble
					).start();

				// Setup to read output
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				String reoriented = "";

				// Parse and respond to output
				while ((line = br.readLine()) != null) {

					// Check to see if the cube was reoriented
					if (line.indexOf("Reoriented") > -1) {
						reoriented = "\nNOTE: Cube was reoriented in some way during scramble!";

					// Display solution
					} else if (line.indexOf("Trying") == -1 && line.indexOf("solution") == -1) {
						
						String solution = Cube.beautify(line);
						int buttonPressed = 0;

						if (reoriented.equals("")) {
							Object[] options = { "Dismiss", "Copy Text to Clipboard" };
							buttonPressed = JOptionPane.showOptionDialog(null, Cube.beautify(line), "Solution Found (Scramble #" + (i + 1) + ")",
								JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
								null, options, options[0]);
						} else
							JOptionPane.showMessageDialog(null, Cube.beautify(line) + reoriented, "Solution Found (Scramble #" + (i + 1) + ")", JOptionPane.INFORMATION_MESSAGE);
						
						reoriented = "";
						solutionFound = true;

						// Copy text to the clipboard if requested
						if (buttonPressed == 1) {
							StringSelection stringSelection = new StringSelection(solution);
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard ();
							clipboard.setContents(stringSelection, null);
						}

					}
				}

			}

				/* Check file with scrambles */
    		
    		if (scrambleFile != null) {

    			String fileName = scrambleFile.toString();

    			// Run Tom's program with the proper flags set
				Process process;
				if (includeRotation.isSelected())
					 process = new ProcessBuilder(
						System.getProperty("user.dir") + "/movedelta",
						"-e" + nErrors,
						"-p" + actualScramble.cube.getState(),
						"-f" + fileName,
						"-m"
					).start();
				else
					 process = new ProcessBuilder(
						System.getProperty("user.dir") + "/movedelta",
						"-e" + nErrors,
						"-p" + actualScramble.cube.getState(),
						"-f" + fileName
					).start();

				// Setup to read output
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				String reoriented = "";
				int lineNumber = -1;

				// Parse and respond to output
				while ((line = br.readLine()) != null) {

					// Get line number of scramble which matched
					if (line.indexOf("against") > -1) {
						String[] splitLine = line.split(" +");
						lineNumber = Integer.valueOf(splitLine[splitLine.length - 1]);
					}

					// Check to see if the cube was reoriented
					if (line.indexOf("Reoriented") > -1) {
						reoriented = "\nNOTE: Cube was reoriented in some way during scramble!";

					// Display solution
					} else if (line.indexOf("Trying") == -1 && line.indexOf("solution") == -1 && line.indexOf("scrambles") == -1) {

						String solution = Cube.beautify(line);
						int buttonPressed = 0;

						if (reoriented.equals("")) {
							Object[] options = { "Dismiss", "Copy Text to Clipboard" };
							buttonPressed = JOptionPane.showOptionDialog(null, Cube.beautify(line), "Solution Found (Line #" + (lineNumber + 1) + ")",
								JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
								null, options, options[0]);
						} else
							JOptionPane.showMessageDialog(null, Cube.beautify(line) + reoriented, "Solution Found (Line #" + (lineNumber + 1) + ")", JOptionPane.INFORMATION_MESSAGE);
						
						reoriented = "";
						solutionFound = true;

						// Copy text to the clipboard if requested
						if (buttonPressed == 1) {
							StringSelection stringSelection = new StringSelection(solution);
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard ();
							clipboard.setContents(stringSelection, null);
						}
					}

				}

			}

			// Report to the user if no solutions were found
			if (!solutionFound)
				JOptionPane.showMessageDialog(null, "There were no solutions found within " + nErrors + " errors.", "No Solution Found", JOptionPane.WARNING_MESSAGE);
			

		// Display error message if 'movedelta' could not be run
		} catch (IOException exception) {
			System.err.println("Failed to run Tom's program...");
			JOptionPane.showMessageDialog(null, "Unable to launch 'movedelta' program. Please run command 'make' in the terminal.", "Error", JOptionPane.ERROR_MESSAGE);
			exception.printStackTrace();
			System.exit(-1);
		}

    }

    /** 
	 * Opens up a JFileChooser for the user to choose a file from their file system.
	 * @return - a file that the user selected on their computer, or null if they didn't choose anything
	 */
	private File selectFile (String title) {

			/* Set up the file chooser */

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(title);

			/* Begin at current directory */

		// fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

			/* Prompt user to select a file, returning it */

		fileChooser.showOpenDialog(null);

		return fileChooser.getSelectedFile();
		
	} 

  	/* Private class used to display a cube on the screen */
	private class Canvas extends JPanel {

			/* Constants */
		
		private final int 	DEFAULT_WIDTH   	= 300,
							DEFAULT_HEIGHT 		= (DEFAULT_WIDTH*3)/4,
							HORIZONTAL_PADDING 	= 75,
							VERTICAL_PADDING 	= 75,
							CUBE_LENGTH 		= DEFAULT_WIDTH/5,
							CUBE_PADDING 		= 4;

			/* Variables */

		public boolean initialized;
		private String title = "";
		private Cube cube = null;
		private Point pointSelected;
		private int selectedFace, selectedY, selectedX;

		/* Constructor */
	 	public Canvas(String title, Cube cube) {

	 		this.title = title;
	 		this.cube = cube;

	 		setVisible(true);

	 		reset();

	 	}

	 	/* Reset canvas when cube size is changed */
	 	public void reset() {

	 		initialized = false;
	 		selectedFace = selectedY = selectedX = -1;
	 		pointSelected = null;

	 		repaint();

	 	}

	 	/**
	 	* Returns the dimensions that the canvas should be
	 	* @return preferred dimension
	 	*/
	 	@Override public Dimension getPreferredSize() {

	 		return new Dimension(DEFAULT_WIDTH + HORIZONTAL_PADDING*2, DEFAULT_HEIGHT + VERTICAL_PADDING*2);
	 	
	 	}

	 	/**
	 	* Updates the canvas
	 	* @param g - Graphics object (which is passed through by Java)
	 	*/
	 	@Override protected void paintComponent(Graphics g) {

	 		super.paintComponent(g);

	 			/* Only calculate sticker positions if we have not done so before */

	 		if (!initialized)
	 			initializePositions();
	 		
	 			/* Draw background */

	 		g.setColor(new Color(215, 215, 215, 255));
		 	g.fillRect(HORIZONTAL_PADDING, VERTICAL_PADDING, DEFAULT_WIDTH, DEFAULT_HEIGHT);

		 		/* Draw titles above canvas */
	 		
	 		g.setColor(Color.BLACK);
	 		g.setFont(new Font("Verdana", Font.BOLD, 14));
			Rectangle2D frame = g.getFontMetrics().getStringBounds(title, g);
			g.drawString(title, (int) ((getWidth() - frame.getWidth())/2), (int) ((VERTICAL_PADDING - frame.getHeight()/2)));
			
			 	/* Draw stickers and outline of each side of the cube */

			int frontFaceOriginX = getWidth()/2 - CUBE_LENGTH - CUBE_PADDING/2,
				frontFaceOriginY = (getHeight() - CUBE_LENGTH - CUBE_PADDING)/2,
				stickerLength = CUBE_LENGTH/cube.n - 1;

			for (int i = 0; i < cube.NUM_SIDES; i++) {
				
				// Draw stickers
				for (int y = 0; y < cube.n; y++)
				 	for (int x = 0; x < cube.n; x++) {
				 		g.setColor(Color.DARK_GRAY);
						g.drawRect(cube.stickersOriginOnCanvas[i][y][x].x, cube.stickersOriginOnCanvas[i][y][x].y, stickerLength, stickerLength);
				 		g.setColor(cube.stickers[i][y][x].color);
					 	g.fillRect(cube.stickersOriginOnCanvas[i][y][x].x, cube.stickersOriginOnCanvas[i][y][x].y, stickerLength, stickerLength);
				 	}

				// Draw outline of the side of the cube
				g.setColor(Color.BLACK);
				g.drawRect(cube.stickersOriginOnCanvas[i][0][0].x, cube.stickersOriginOnCanvas[i][0][0].y, CUBE_LENGTH, CUBE_LENGTH);

			}

				/* Draw box around selected sticker */
			
			if (pointSelected != null) {

				int thickness = 4;

				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(Color.DARK_GRAY);
				g2.setStroke(new BasicStroke(thickness));
				g.drawRect(pointSelected.x - thickness/2 + 1, pointSelected.y - thickness/2 + 1, stickerLength + thickness/2, stickerLength + thickness/2);
				 		
			}

	 	}

	 	public void select(int xPos, int yPos) {

	 		for (int i = 0; i < cube.NUM_SIDES; i++)
				for (int y = 0; y < cube.n; y++)
				 	for (int x = 0; x < cube.n; x++) {

				 		int xStart = cube.stickersOriginOnCanvas[i][y][x].x;
				 		int xEnd = xStart + CUBE_LENGTH/cube.n;
				 		int yStart = cube.stickersOriginOnCanvas[i][y][x].y;
				 		int yEnd = yStart + CUBE_LENGTH/cube.n;
				 	
				 		// Check to see if this sticker was clicked on
				 		if (xPos >= xStart && xPos <= xEnd && yPos >= yStart && yPos <= yEnd) {
				 			selectedFace = i;
				 			selectedY = y;
				 			selectedX = x;
				 			pointSelected = cube.stickersOriginOnCanvas[i][y][x];
				 			repaint();
				 			return;
				 		}
				 	}

	 	}

	 	public void moveSelection(int dx, int dy) {

	 		selectedX += dx;
	 		selectedY += dy;

	 		if (selectedX < 0) {
	 			switch (selectedFace) {

	 				case Cube.U: case Cube.L: case Cube.D:
	 					selectedX = 0;
	 					break;

	 				case Cube.F:
	 					selectedFace = Cube.L;
	 					selectedX = cube.n - 1;
	 					break;

	 				case Cube.R:
	 					selectedFace = Cube.F;
	 					selectedX = cube.n - 1;
	 					break;

	 				case Cube.B:
	 					selectedFace = Cube.R;
	 					selectedX = cube.n - 1;
	 					break;

	 			}
	 		} else if (selectedX >= cube.n) {
	 			switch (selectedFace) {

	 				case Cube.U: case Cube.B: case Cube.D:
	 					selectedX = cube.n - 1;
	 					break;

	 				case Cube.L:
	 					selectedFace = Cube.F;
	 					selectedX = 0;
	 					break;

	 				case Cube.F:
	 					selectedFace = Cube.R;
	 					selectedX = 0;
	 					break;

	 				case Cube.R:
	 					selectedFace = Cube.B;
	 					selectedX = 0;
	 					break;

	 			}
	 		} else if (selectedY < 0) {
	 			switch (selectedFace) {

	 				case Cube.L: case Cube.R: case Cube.B: case Cube.U:
	 					selectedY = 0;
	 					break;

	 				case Cube.D:
	 					selectedFace = Cube.F;
	 					selectedY = cube.n - 1;
	 					break;

	 				case Cube.F:
	 					selectedFace = Cube.U;
	 					selectedY = cube.n - 1;
	 					break;

	 			}
	 		} else if (selectedY >= cube.n) {
	 			switch (selectedFace) {

	 				case Cube.L: case Cube.R: case Cube.B: case Cube.D:
	 					selectedY = cube.n - 1;
	 					break;

	 				case Cube.U:
	 					selectedFace = Cube.F;
	 					selectedY = 0;
	 					break;

	 				case Cube.F:
	 					selectedFace = Cube.D;
	 					selectedY = 0;
	 					break;

	 			}
	 		}

	 		pointSelected = cube.stickersOriginOnCanvas[selectedFace][selectedY][selectedX];
	 		repaint();
	 	}

	 	/* Update the selected sticker */
	 	public void setSticker(Sticker sticker) {

	 		// Don't update, if the new color is the same as the old one
	 		if (cube.stickers[selectedFace][selectedY][selectedX] != sticker) {
		 		
		 		cube.stickers[selectedFace][selectedY][selectedX] = sticker;
		 		repaint();

		 		// State of the cube has changed (so it won't match the scramble anymore)
		 		userHasChangedPuzzleStateByDrawing = true; // This needs to be set, or the cube will reset to a solved position
		 		actualScrambleTextField.setText("");
		 		actualScrambleParsed.setText(" Interpretation: ");

		 	}

	 	}

	 	/* Calculate positions of all of the stickers on the cube */
	 	public void initializePositions() {

	 		initialized = true;

			int frontFaceOriginX = getWidth()/2 - CUBE_LENGTH - CUBE_PADDING/2,
				frontFaceOriginY = (getHeight() - CUBE_LENGTH - CUBE_PADDING)/2,
				stickerLength = CUBE_LENGTH/cube.n - 1;

			for (int i = 0; i < cube.NUM_SIDES; i++) {

				int startX, startY;

				switch (i) {

					case 0:
						startX = frontFaceOriginX;
						startY = frontFaceOriginY - CUBE_LENGTH - CUBE_PADDING;
						break;

					case 1:
						startX = frontFaceOriginX - CUBE_LENGTH - CUBE_PADDING;
						startY = frontFaceOriginY;
						break;

					case 2:
						startX = frontFaceOriginX;
						startY = frontFaceOriginY;
						break;

					case 3:
						startX = frontFaceOriginX + CUBE_LENGTH + CUBE_PADDING;
						startY = frontFaceOriginY;
						break;

					case 4:
						startX = frontFaceOriginX + 2 * (CUBE_LENGTH + CUBE_PADDING);
						startY = frontFaceOriginY;
						break;

					case 5: default:
						startX = frontFaceOriginX;
						startY = frontFaceOriginY + CUBE_LENGTH + CUBE_PADDING;
						break;

				}
				
				// Calculate and store positions
				for (int y = 0; y < cube.n; y++)
				 	for (int x = 0; x < cube.n; x++)
				 		cube.stickersOriginOnCanvas[i][y][x] = new Point(startX + x*stickerLength + x + 1, startY + y*stickerLength + y + 1);
				 		
			}

	 	}

	 } // Canvas Class

}