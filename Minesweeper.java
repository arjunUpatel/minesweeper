import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Minesweeper extends JFrame implements ActionListener, MouseListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	JToggleButton[][] board;
	JTextField timeField;
	JButton resetButton;
	JPanel boardPanel;
	JMenuBar menu;
	JMenu difficultyOptions;
	JMenuItem[] difficultyOption;
	boolean firstClick = true;
	int numMines, beginnerMines, intermediateMines, expertMines, beginnerRow, beginnerCol, intermediateRow,
			intermediateCol, expertRow, expertCol, boardRow, boardCol, timePassed, buttonSize, count;
	boolean gameOn = true;
	ImageIcon mine, flag, lose, smile, wait, win;
	GraphicsEnvironment ge;
	Font timeFont;
	ImageIcon[] nums;
	String[] difficulties;
	Timer timer;

	public Minesweeper() {
		buttonSize = 30;

		beginnerRow = 9;
		beginnerCol = 9;
		beginnerMines = 10;

		intermediateRow = 16;
		intermediateCol = 16;
		intermediateMines = 40;

		expertRow = 16;
		expertCol = 40;
		expertMines = 99;

		boardRow = beginnerRow;
		boardCol = beginnerCol;

		try {
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			timeFont = Font.createFont(Font.TRUETYPE_FONT, new File("digital-7.ttf"));
			ge.registerFont(timeFont);
		} catch (IOException | FontFormatException e) {
		}

		nums = new ImageIcon[8];
		for (int i = 1; i < 9; i++) {
			nums[i - 1] = new ImageIcon(i + ".png");
			nums[i - 1] = new ImageIcon(
					nums[i - 1].getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));
		}

		mine = new ImageIcon("mine.png");
		mine = new ImageIcon(mine.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));

		flag = new ImageIcon("flag.png");
		flag = new ImageIcon(flag.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));

		smile = new ImageIcon("smile1.png");
		smile = new ImageIcon(smile.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));

		lose = new ImageIcon("lose1.png");
		lose = new ImageIcon(lose.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));

		wait = new ImageIcon("wait1.png");
		wait = new ImageIcon(wait.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));

		win = new ImageIcon("win1.png");
		win = new ImageIcon(win.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));

		menu = new JMenuBar();

		timeField = new JTextField();
		timeField.setFont(timeFont.deriveFont(24f));
		timeField.setForeground(Color.RED);

		menu.setLayout(new GridLayout(1, 3));

		difficultyOptions = new JMenu("Difficulty");
		difficulties = new String[] { "Beginner", "Intermediate", "Expert" };
		difficultyOption = new JMenuItem[difficulties.length];
		difficultyOptions.setLayout(new GridLayout(3, 1));

		resetButton = new JButton();
		resetButton.addActionListener(this);

		menu.add(difficultyOptions);
		menu.add(resetButton);
		menu.add(timeField);

		for (int i = 0; i < difficultyOption.length; i++) {
			difficultyOption[i] = new JMenuItem(difficulties[i]);
			difficultyOption[i].addActionListener(this);
			difficultyOptions.add(difficultyOption[i]);
		}

		numMines = beginnerMines;
		this.setLayout(new BorderLayout());
		this.add(menu, BorderLayout.NORTH);
		createBoard(boardRow, boardCol);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void createBoard(int row, int col) {
		if (boardPanel != null)
			this.remove(boardPanel);
		boardPanel = new JPanel();
		board = new JToggleButton[row][col];
		boardPanel.setLayout(new GridLayout(row, col));
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				board[r][c] = new JToggleButton();
				board[r][c].putClientProperty("row", r);
				board[r][c].putClientProperty("col", c);
				board[r][c].putClientProperty("state", 0);
				board[r][c].setBorder(BorderFactory.createBevelBorder(0));
				board[r][c].setFocusPainted(false);
				board[r][c].addMouseListener(this);
				boardPanel.add(board[r][c]);
			}
		}
		resetButton.setIcon(smile);
		timeField.setText("" + timePassed);
		this.add(boardPanel, BorderLayout.CENTER);
		this.setSize(col * buttonSize, (row * buttonSize) + 60);
		this.revalidate();
	}

	public void setMinesAndCounts(int row, int col) {
		int count = numMines;
		int dimR = board.length;
		int dimC = board[0].length;
		while (count > 0) {
			int randR = (int) (Math.random() * dimR);
			int randC = (int) (Math.random() * dimC);
			int state = (int) (board[randR][randC].getClientProperty("state"));
			if (state == 0 && (Math.abs(row - randR) > 1 || Math.abs(col - randC) > 1)) {
				board[randR][randC].putClientProperty("state", 9);
				count--;
			}
		}
		for (int r = 0; r < dimR; r++) {

			for (int c = 0; c < dimC; c++) {
				count = 0;
				int buttonState = Integer.parseInt("" + board[r][c].getClientProperty("state"));
				if (buttonState != 9) {
					for (int rSmall = r - 1; rSmall <= r + 1; rSmall++) {
						for (int cSmall = c - 1; cSmall <= c + 1; cSmall++) {
							try {
								int state = Integer.parseInt("" + board[rSmall][cSmall].getClientProperty("state"));
								if (state == 9 && !(rSmall == r && cSmall == c))
									count++;
							} catch (ArrayIndexOutOfBoundsException e) {
							}
						}
					}
					board[r][c].putClientProperty("state", count);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (timer != null) {
			timer.cancel();
			timePassed = 0;
			timeField.setText("" + timePassed);
		}
		for (int i = 0; i < difficultyOption.length; i++) {
			if (e.getSource() == difficultyOption[i]) {
				if (difficultyOption[i].getText() == "Beginner") {
					boardRow = beginnerRow;
					boardCol = beginnerCol;
					numMines = beginnerMines;
				}
				if (difficultyOption[i].getText() == "Intermediate") {
					boardRow = intermediateRow;
					boardCol = intermediateCol;
					numMines = intermediateMines;
				}
				if (difficultyOption[i].getText() == "Expert") {
					boardRow = expertRow;
					boardCol = expertCol;
					numMines = expertMines;
				}
				gameOn = true;
				firstClick = true;
				count = 0;
				createBoard(boardRow, boardCol);
			}
		}
		if (e.getSource() == resetButton) {
			gameOn = true;
			firstClick = true;
			count = 0;
			createBoard(boardRow, boardCol);
			timePassed = 0;
			timeField.setText("" + timePassed);
		}
	}

	public void mouseReleased(MouseEvent e) {
		int row = (int) (((JToggleButton) e.getComponent()).getClientProperty("row"));
		int col = (int) (((JToggleButton) e.getComponent()).getClientProperty("col"));
		if (gameOn) {
			if (e.getButton() == MouseEvent.BUTTON1 && board[row][col].isEnabled()) {
				if (firstClick) {
					timer = new Timer();
					timer.schedule(new UpdateTimer(), 0, 1000);
					setMinesAndCounts(row, col);
					firstClick = false;
				}
				int state = (int) ((JToggleButton) e.getComponent()).getClientProperty("state");
				if (state == 9 && board[row][col].isSelected()) {
					board[row][col].setIcon(mine);
					board[row][col].setContentAreaFilled(false);
					board[row][col].setOpaque(true);
					board[row][col].setBackground(Color.RED);
					revealMines();
					gameOn = false;
					timer.cancel();
					resetButton.setIcon(lose);
				} else {
					expand(row, col);
					count++;
					resetButton.setIcon(smile);
					checkWin();
				}
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				if (!board[row][col].isSelected()) {
					board[row][col].setIcon(flag);
					board[row][col].setSelected(true);
				} else if (board[row][col].getIcon() == flag) {
					board[row][col].setIcon(null);
					board[row][col].setSelected(false);
				}
			}
		} else {
			if (board[row][col].isSelected())
				board[row][col].setSelected(true);
			else
				board[row][col].setSelected(false);
		}
	}

	public void revealMines() {
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				int state = (int) board[r][c].getClientProperty("state");
				if (state == 9) {
					board[r][c].setIcon(mine);
					board[r][c].setDisabledIcon(mine);
					board[r][c].setSelected(true);
				}
				if (board[r][c].getIcon() == flag)
					board[r][c].setDisabledIcon(flag);
				board[r][c].setEnabled(false);
			}
		}
	}

	public void checkWin() {
		int dimR = board.length;
		int dimC = board[0].length;
		int totalSpace = dimR * dimC;
		if (numMines == totalSpace - count) {
			resetButton.setIcon(win);
			timer.cancel();
			gameOn = false;
			for (int r = 0; r < dimR; r++) {
				for (int c = 0; c < dimC; c++) {
					board[r][c].setEnabled(false);
					if (board[r][c].getIcon() == flag)
						board[r][c].setDisabledIcon(flag);
				}
			}
		}
	}

	public void writeText(int row, int col, int state) {
		if (state > 0) {
			board[row][col].setIcon(nums[state - 1]);
			board[row][col].setDisabledIcon(nums[state - 1]);
		}
	}

	public void expand(int row, int col) {
		if (!board[row][col].isSelected()) {
			board[row][col].setSelected(true);
			count++;
		}
		int state = (int) board[row][col].getClientProperty("state");
		if (state > 0 && board[row][col].getIcon() != flag)
			writeText(row, col, state);
		else if (board[row][col].getIcon() != flag) {
			for (int rSmall = row - 1; rSmall <= row + 1; rSmall++) {
				for (int cSmall = col - 1; cSmall <= col + 1; cSmall++) {
					if (!(rSmall == row && cSmall == col)) {
						try {
							if (!board[rSmall][cSmall].isSelected())
								expand(rSmall, cSmall);
						} catch (ArrayIndexOutOfBoundsException e) {
						}
					}
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			resetButton.setIcon(wait);
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {

	}

	public class UpdateTimer extends TimerTask {
		public UpdateTimer() {

		}

		@Override
		public void run() {
			if (gameOn)
				timePassed++;
			timeField.setText("" + timePassed);
		}
	}

	public static void main(String[] args) {
		Minesweeper app = new Minesweeper();
	}
}