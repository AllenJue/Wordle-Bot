import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Gui extends JFrame {
	final Color GREEN = new Color(108, 169, 101);
	final Color GRAY = new Color(120, 124, 126);
	final Color YELLOW = new Color(200, 180, 88);
	
	private final int WORD_SIZE = 5;
	private JLabel textArea;
	private Image[] icons;
	private JLabel botImage;
	private JToolBar toolBar;
	private WordleManager functionalWordle;
	private JButton[][] squares;
	private JPanel gridArea;
	private JPanel mainGui;
	private int curRow;
	private int curCol;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
//                	WordleManagerTester tester = new WordleManagerTester();
//                	tester.test(1000);
//                	tester.printData();
                	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                	createAndShowGui();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	/**
	 * Creates and displays gui
	 */
	private static void createAndShowGui() {
    	Gui frame = new Gui("Wordle Bot");
    	frame.setResizable(false);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.addComponents();
    	frame.addKeyListener();
    	frame.pack();
    	frame.setVisible(true); 
	}
	
	/**
	 * Constructor for Gui
	 * @param name of program
	 */
	public Gui(String name) {
		super(name);
		curRow = 0;
		curCol = 0;
		functionalWordle = new WordleManager(WORD_SIZE);
	}
	
	/**
	 * Gets the current configuration from the GUI and tries the guess in functional wordle.
	 */
	private void processInput() {
		char[] input = new char[10];
		for(int i = 0; i < squares[0].length; i++) {
			JButton curButton = squares[curRow][i];
			input[2 * i] = curButton.getText().toLowerCase().charAt(0);
			if(curButton.getBackground() == GRAY) {
				input[2 * i + 1] = 'N';
			} else if(curButton.getBackground() == YELLOW) {
				input[2 * i + 1] = 'Y';
			} else {
				input[2 * i + 1] = 'G';
			}
		}
		functionalWordle.processGuess(new String(input));
		String bestGuess = functionalWordle.getBestGuess();
		setText(bestGuess);
	}
	
	/**
	 * Sets the GUI text to welcome user and display the best guess
	 * @param bestGuess current processed best guess by wordle manager
	 */
	private void setText(String bestGuess) {
		if(functionalWordle.gameOver()) {
			textArea.setText("Game over! Click reset to start over");
		} else if(bestGuess == null) {
			textArea.setText("Sorry, the word you're looking for doesn't exist! Try again");
		} else {
			textArea.setText("<html>Welcome to my Wordle Bot!<br/>Best guess: " 
					+ bestGuess.toUpperCase() + "<html/>");
		}
	}

	/**
	 * Adds buttons and text areas to the gui
	 */
	private void addComponents() {
		((JComponent) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		GridLayout gridLayout = new GridLayout(WORD_SIZE + 1, WORD_SIZE);
		gridLayout.setVgap(5);
		gridLayout.setHgap(5);
		gridArea = new JPanel(gridLayout);
		gridArea.setPreferredSize(new Dimension(500, 600));
		initializeButtons();
		initializeIcons();
		initializeToolBar();
		initializeMainGui();
		
	}
	
	/**
	 * Places the main gui back onto the frame
	 */
	private void initializeMainGui() {
		mainGui = new JPanel();
		mainGui.setBorder(new EmptyBorder(20, 20, 20, 20));
		mainGui.add(gridArea, BorderLayout.CENTER);
		mainGui.setBackground(Color.WHITE);
		getContentPane().add(mainGui);		
	}

	/**
	 * Initializes icons to have images
	 */
	private void initializeIcons() {
		icons = new Image[4];
		try {
			icons[0] = ImageIO.read(getClass().getResource("concerned-bot.png"));
	        icons[1] = ImageIO.read(getClass().getResource("default-bot.png"));
	        icons[2] = ImageIO.read(getClass().getResource("confident-bot.png"));
	        icons[3] = ImageIO.read(getClass().getResource("failed-bot.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Establishes tool bar with reset button and text suggestion
	 */
	private void initializeToolBar() {
		toolBar = new JToolBar();
		Action resetButton = new AbstractAction("Reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
            	reset();
            	getContentPane().requestFocusInWindow();
            }
        };
        botImage = new JLabel();
        toolBar.add(resetButton);
        toolBar.add(botImage);
		Border margin = new EmptyBorder(10,10,10,10);
		textArea = new JLabel("", SwingConstants.CENTER);
		setText(functionalWordle.getBestGuess());
		setImage();
		textArea.setSize(textArea.getPreferredSize());
		textArea.setBorder(margin);
		toolBar.add(textArea);
        getContentPane().add(toolBar, BorderLayout.NORTH);
	}

	private void setImage() {
		double probability = functionalWordle.getProbability();
		boolean likelySolvable = functionalWordle.likelySolvable();
		if(!likelySolvable && probability < 0.5) {
			botImage.setIcon(new ImageIcon(icons[3]));
		} else if(!likelySolvable) {
			botImage.setIcon(new ImageIcon(icons[0]));
		} else if(likelySolvable && probability < 0.5) {
			botImage.setIcon(new ImageIcon(icons[1]));
		} else {
			botImage.setIcon(new ImageIcon(icons[0]));
		}
	}

	/**
	 * Resets the Gui and WordleManager
	 */
	private void reset() {
		curCol = 0;
		curRow = 0;
		functionalWordle.reset();
		setText(functionalWordle.getBestGuess());
		for(JButton[] row : squares) {
			for(JButton b : row) {
				b.setText("");
				b.setBackground(GRAY);
			}
		}
		gridArea.requestFocus();
	}
	
	/**
	 * Adds keylistener to the entire frame. Takes inputs from letters, backspace, and enter keys
	 */
	private void addKeyListener() {
		// Text will always focus on the main gui
    	getContentPane().setFocusable(true);
        getContentPane().requestFocusInWindow();
		getContentPane().addKeyListener(new KeyListener() {
			/**
			 * Appends letter to corresponding button if key value is a letter
			 * and letters are still needed
			 * @param e is the KeyEvent typed
			 */
			@Override
			public void keyTyped(KeyEvent e) {
				if(!functionalWordle.gameOver()) {
					char c = Character.toUpperCase(e.getKeyChar()) ;
					if(Character.isLetter(c) && curCol < 5) {
						JButton curButton = squares[curRow][curCol];
						curCol++;
						curButton.setText(Character.toString(c));
					} 
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// placeholder
			}

			/**
			 * Deletes last letter typed if possible when back_space/delete typed
			 * Processes a word entry if enter is pressed
			 * @param e KeyEvent typed
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				if(!functionalWordle.gameOver()) {
					if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && curCol > 0) {
						curCol--;
						JButton curButton = squares[curRow][curCol];
						curButton.setText("");
					} else if(e.getKeyCode() == KeyEvent.VK_ENTER && curCol == 5) {
						if(curRow < 6) {
							processInput();
							curRow++;
							curCol = 0;
						}
						if(functionalWordle.gameOver()) {
							setText("");
						}
					}
				}
			}
		});
	}
	
	/**
	 * 
	 * @param button
	 * @return
	 */
	private boolean isCorrectRow(JButton button) {
		final int CONVERSION_FACTOR = 100;
		return ((int) button.getLocation().getY()) / CONVERSION_FACTOR == curRow;
	}
	
	/**
	 * Creates a 6x5 (row x col) grid of buttons
	 */
	private void initializeButtons() {
		squares = new JButton[WORD_SIZE + 1][WORD_SIZE];
		for(int i = 0; i < squares.length; i++) {
			for(int j = 0; j < squares[0].length; j++) {
				squares[i][j] = new JButton();
				squares[i][j].setPreferredSize(new Dimension(20, 20));
				squares[i][j].setBackground(GRAY);
				squares[i][j].setForeground(Color.WHITE);
				squares[i][j].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
				squares[i][j].setFont(new Font("Helvetica Neue", Font.BOLD, 45));
				squares[i][j].setFocusable(false);
				squares[i][j].addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
						// Placeholder
						
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						// Placeholder
						
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						// Placeholder
						
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						// Placeholder
					}
					
					/**
					 * Allows for the squares to be toggled between Gray, Yellow, and Green
					 */
					@Override
					public void mouseClicked(MouseEvent e) {
						if(!functionalWordle.gameOver()) {
							JButton button = (JButton) e.getComponent();
							if(isCorrectRow(button)) {
								if(button.getBackground() == GRAY) {
									button.setBackground(YELLOW);
								} else if(button.getBackground() == YELLOW) {
									button.setBackground(GREEN);
								} else {
									button.setBackground(GRAY);
								}
							}
						}
					}
				});
				gridArea.add(squares[i][j]);
			}
		}
	}
}
