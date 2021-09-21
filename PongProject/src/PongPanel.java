import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Font;

public class PongPanel extends JPanel implements ActionListener, KeyListener {

	private final static Color BACKGROUND_COLOUR = Color.black;
	private final static int TIMER_DELAY = 5;
	private Ball ball;
	private Paddle playerOnePaddle, playerTwoPaddle;
	private GameState gameState = GameState.Initialising;
	private final static int BALL_MOVEMENT_SPEED = 2;
	private final static int POINTS_TO_WIN = 11;
	private int playerOneScore = 0, playerTwoScore = 0;
	private Player gameWinner;
	private final static int X_PADDING = 100;
	private final static int Y_PADDING = 100;
	private final static int FONT_SIZE = 50;
	private final static Font SCORE_FONT = new Font("Serif", Font.BOLD, FONT_SIZE);
	private final static int WINNER_TEXT_X = 200;
	private final static int WINNER_TEXT_Y = 200;
	private final static int WINNER_FONT_SIZE = 40;
	private final static Font WINNER_FONT = new Font("Serif", Font.BOLD, WINNER_FONT_SIZE);
	private final static String WINNER_TEXT = "WIN!";
	
	public void createObjects() {
		ball = new Ball(getWidth(), getHeight());
		playerOnePaddle = new Paddle(Player.One, getWidth(), getHeight());
		playerTwoPaddle = new Paddle(Player.Two, getWidth(), getHeight());
		addKeyListener(this);
		setFocusable(true);
		
	}
	
	private void update() {
		switch(gameState) {
			case Initialising: {
				createObjects();
				gameState = GameState.Playing;
				ball.setXVelocity(BALL_MOVEMENT_SPEED);
				ball.setYVelocity(BALL_MOVEMENT_SPEED);
				break;
			}
			case Playing: {
				moveObject(playerOnePaddle);
				moveObject(playerTwoPaddle);
				moveObject(ball);
				checkWallBounce();
				checkPaddleBounce();
				checkWin();
				break;
			}
			case GameOver: {
				break;
			}
		}
	}
	
	private void paintSprite(Graphics g, Sprite sprite) {
		g.setColor(sprite.getColour());;
		g.fillRect(sprite.getXPosition(), sprite.getYPosition(), sprite.getWidth(),sprite.getHeight());
	}
	
	private void moveObject(Sprite sprite) {
		
		sprite.setXPosition(sprite.getXPosition() + sprite.getXVelocity(), getWidth());
		sprite.setYPosition(sprite.getYPosition() + sprite.getYVelocity(), getHeight());
	}
	private void addScore(Player player) {
		if (player == player.One) {
			playerOneScore ++;
		}
		else if (player == player.Two) {
			playerTwoScore++;
		}
	}
	
	private void checkWin() {
		if (playerOneScore >= POINTS_TO_WIN) {
			gameWinner = Player.One;
			gameState = GameState.GameOver;
		}
		else if(playerTwoScore >= POINTS_TO_WIN) {
			gameWinner = Player.Two;
			gameState = GameState.GameOver;
		}
	}
	
	private void checkWallBounce() {
		if (ball.getXPosition() <= 0) {
			//hit left side
			ball.setXVelocity(-ball.getXVelocity());
			addScore(Player.Two);
			resetBall();
		}else if (ball.getXPosition() >= getWidth() - ball.getWidth()) {
			//hit right side
			ball.setXVelocity(-ball.getXVelocity());
			addScore(Player.One);
			resetBall();
		}
		if (ball.getYPosition() <= 0 || ball.getYPosition() >= getHeight() - ball.getHeight()){
			//hit top or bottom
			ball.setYVelocity(-ball.getYVelocity());
		}
		
	}
	
	private void checkPaddleBounce() {
		if(ball.getXVelocity() < 0 && ball.getRectangle().intersects(playerOnePaddle.getRectangle())) {
			ball.setXVelocity(BALL_MOVEMENT_SPEED);
		}
		
		if(ball.getXVelocity() > 0 && ball.getRectangle().intersects(playerTwoPaddle.getRectangle())) {
			ball.setXVelocity(-BALL_MOVEMENT_SPEED);
		}
	}
	
	private void resetBall() {
		ball.resetToInitialPosition();
	}
	
	public PongPanel() {
		setBackground(BACKGROUND_COLOUR);
		Timer timer = new Timer(TIMER_DELAY, this);
		timer.start();
	}
	@Override
	public void keyTyped(KeyEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_UP) {
			playerTwoPaddle.setYVelocity(-1);
		} else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
			playerTwoPaddle.setYVelocity(1);
		}
		
		if(event.getKeyCode() == KeyEvent.VK_W) {
			playerOnePaddle.setYVelocity(-1);
		} else if (event.getKeyCode() == KeyEvent.VK_S) {
			playerOnePaddle.setYVelocity(1);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN) {
			playerTwoPaddle.setYVelocity(0);
		}
		if(event.getKeyCode() == KeyEvent.VK_W || event.getKeyCode() == KeyEvent.VK_S) {
			playerOnePaddle.setYVelocity(0);
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		update();
		repaint();
	}
	private void paintScores(Graphics g) {
		
		String leftScore = Integer.toString(playerOneScore);
		String rightScore = Integer.toString(playerTwoScore);
		g.setFont(SCORE_FONT);
		g.drawString(leftScore, X_PADDING, Y_PADDING);
		g.drawString(rightScore, getWidth()-X_PADDING, Y_PADDING);
	}
	
	private void displayWinner(Graphics g) {
		if (gameWinner != null) {
			g.setFont(WINNER_FONT);
			int xPosition = getWidth() /2;
			if(gameWinner == Player.One) {
				xPosition -= WINNER_TEXT_X;
			} else if(gameWinner == Player.Two) {
				xPosition += WINNER_TEXT_X;
			}
			g.drawString(WINNER_TEXT, xPosition,WINNER_TEXT_Y);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintDottedLine(g);
		if(gameState != GameState.Initialising) {
			paintSprite(g, ball);
			paintSprite(g, playerOnePaddle);
			paintSprite(g, playerTwoPaddle);
			paintScores(g);
			displayWinner(g);
		}
	}
	
	private void paintDottedLine(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
			Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {9},0);
			g2d.setStroke(dashed);;
			g2d.setPaint(Color.WHITE);
			g2d.drawLine(getWidth() / 2,  0,  getWidth() / 2,  getHeight());
			g2d.dispose();
	}
}
