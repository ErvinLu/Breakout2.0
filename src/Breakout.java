import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram 
{
	/*DIMENSIONS OF THE APPLET*/
	public static final int APP_WIDTH = 400;
	public static final int APP_HEIGHT = 600;
	
	/*DIMENSIONS OF THE PLAYING FIELD*/
	private static final int FIELD_WIDTH = APP_WIDTH;
	private static final int FIELD_HEIGHT = APP_HEIGHT;
	
	/*DIMENSIONS OF THE PADDLE*/
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;
	
	/*OFFSET PADDLE FROM THE BOTTOM OF THE SCREEN*/
	private static final int PADDLE_OFFSET = 30;
	
	/*NUMBER OF BRICKS PER ROW*/
	private static final int BRICKS_PER_ROW = 10;
	
	/*NUMBER OF ROWS*/
	private static final int BRICKS_ROW = 10;
	
	/*SEPARATION BETWEEN BRICK*/
	private static final int BRICKS_SEP = 4;
	
	/*BRICK WIDTH*/
	private static final int BRICK_WIDTH = (FIELD_WIDTH - (BRICKS_PER_ROW - 1) * BRICKS_SEP) / BRICKS_PER_ROW;
	
	/*BRICK HEIGHT*/
	private static final int BRICK_HEIGHT = 20;
	
	/*BALL RADIUS*/
	private static final int BALL_RADIUS = 10;
	
	/*OFFSET BRICKS FROM THE TOP OF THE SCREEN*/
	private static final int BRICKS_OFFSET = 70;
	
	/*NUMBER OF TRIES*/
	private static final int LIVES = 3;
	
	/*SPEED INCREMENT*/
	private static final double SPEED = 0.1;
	
	//Brick
	GRect bricks[][];
	
	//Paddle
	GRect paddle;
	
	//Ball
	GOval ball;
	private double vx, vy;	//BALL VELOCITY
	private int TRY = 0;	//SET NUMBER OF TRIES TO 0
	private int BRICKS_LEFT = BRICKS_PER_ROW * BRICKS_ROW;	//FOR SCORING
	
	private RandomGenerator rgen = RandomGenerator.getInstance();	//RANDOM GENERATOR FOR vx   
    private static final int DELAY = 10;	//FOR THE DELAY
    
    //Score
    GLabel score;
    
    //Life
    GLabel life;
    
    //Audio
    AudioClip BOUNCE_SOUND;
    AudioClip BACKGROUND_MUSIC;
    
    //Credits
    GLabel name1 = new GLabel ("BREAKOUT");
    GLabel name2 = new GLabel ("by Ervin Lester Lu");
    
    /*BREAKOUT GAME*/
    public void init()
    {
    	TRY = 0;
    	BRICKS_LEFT = BRICKS_PER_ROW * BRICKS_ROW;
    	
    	//BACKGROUND_MUSIC = MediaTools.loadAudioClip("BGMusic.au");
    	//BACKGROUND_MUSIC.loop();
    	//BACKGROUND_MUSIC.play();
    	
    	BOUNCE_SOUND = MediaTools.loadAudioClip("C:\\Users\\Lu Family\\Documents\\Java\\bounce.au");
    	
    	
    	setSize(APP_WIDTH, APP_HEIGHT);
    	
    	score = new GLabel ("Bricks Remaining: " + BRICKS_LEFT);
    	score.setColor(Color.CYAN);
    	add(score, 50, 500); //SCORE LOCATION
    	
    }
    
    public void run()
    {
    	setBackground(Color.BLACK);
    	SetupBricks();
    	SetupPaddle();
    	SetupBall();
    }
    
    public void SetupBall()	//SETUP BALL
    {
    	TRY++;
    	
    	//life = new GLabel("NUMBER OF ATTEMPTS: " + (TRY - 1));
    	//life.setColor(Color.GREEN);
    	//add(life, 130, 15);	//LIFE LOCATION
    	
    	if((TRY <= LIVES) && (BRICKS_LEFT > 0))
    	{
    		int diameter = 2 * BALL_RADIUS;
    		ball = new GOval(diameter, diameter);
    		ball.setFillColor(Color.WHITE);
    		ball.setFilled(true);
    		
    		int x = (getWidth() - diameter) / 2;
    		int y = (getHeight() - diameter) / 2;
    		add(ball, x, y);
    		
    		//BALL SPEED
    		vx = rgen.nextDouble(1.0, 0.3);
    		//vy = vx;
    		vy = 4.0;
    		
    		if(rgen.nextBoolean(0.5))
    		{
    			vx = -vx;
    		}
    		
    		if(rgen.nextBoolean(0.5))
    		{
    			vy = -vy;
    		}
    		
    		waitForClick();	//WAIT FOR USER INPUT (CLICK)
    		
    		movingBall();
    		
    		remove(ball);
    		//remove(life);
			SetupBall();
    	}
    	
    	else
    	{
    		//BACKGROUND_MUSIC.stop();	//STOPS MUSIC
    		GLabel message;
    		
    		if(TRY >= LIVES)
    		{
    			message = new GLabel ("GAME OVER");
    			message.setColor(Color.RED);
    			add(message, 50, 525);
    			
    			//AudioClip gameover = MediaTools.loadAudioClip("lost.au");
    			//gameover.play();
    			
    			Credits();	//CREDITS
    			
    			int creds = 0;
    			while (creds < 100)
    			{
    				name1.move(0, -3);
    				name2.move(0, -3);
    				pause(DELAY);
    				creds++;
    			}
    			
    			waitForClick();
    			//gameover.stop();
    			removeAll();
    			init();
    			run();
    		}
    		
    		else
    		{
    			message = new GLabel ("WINNER");
    			message.setColor(Color.BLUE);
    			add(message, 50, 525);
    			
    			//AudioClip winner = MediaTools.loadAudioClip("won.wav");
    			//winner.play();
    			
    			Credits();
    			
    			waitForClick();
    			
    			//winner.stop();
    			removeAll();
    			init();
    			run();
    		}
    	}
    }	//END SETUP BALL
    
    public void movingBall()	//MOVING BALL
    {
    	boolean hitRightWall = false;
		boolean hitLeftWall = false;
		boolean hitTopWall = false;
		boolean hitBottomWall = false;
		
		int diameter = 2 * BALL_RADIUS;
		
		GObject collider = null;
		
		int colorChangeCount = 0;
		
		while(true)
		{
			if(colorChangeCount++ % 20 ==0 )
			{
				//ball.setFillColor(rgen.nextColor());
				//paddle.setFillColor(rgen.nextColor());
				
				for(int brickRow=0; brickRow<BRICKS_ROW; brickRow++)
				{
					bricks[brickRow][rgen.nextInt(0, BRICKS_PER_ROW-1)].setFillColor(rgen.nextColor());
				}
			}
			
			ball.move(vx, vy);
			pause(DELAY);
			
			hitRightWall = (ball.getX() + diameter) >= getWidth();
			hitLeftWall = (ball.getX()) <= 0;
			hitTopWall = (ball.getY()) <= 0;
			hitBottomWall = (ball.getY() + diameter) >= getHeight();
			
			if(hitRightWall || hitLeftWall)
			{
				vx = -(vx);
				BOUNCE_SOUND.play();
			}
			if(hitTopWall)
			{
				vy = -(vy);
				BOUNCE_SOUND.play();
			}
			if(hitBottomWall)
				break;
			
			collider = getCollidingObject();
			
			if((collider == null) || (collider == score))
			{
				continue;
			}
			
			if(collider == paddle)
			{
				vy = -(vy + SPEED);
				
				//Divide the paddle into 10 parts 
				int parts = 10;
				int paddleFract = PADDLE_WIDTH / parts;
				
				//Points of the Rectangle enclosing the ball object
				GPoint topLeft = new GPoint(ball.getX(), ball.getY());
				GPoint topRight = new GPoint(topLeft.getX() + diameter, topLeft.getY());
				GPoint bottomLeft = new GPoint(topLeft.getX(), topLeft.getY() + diameter);
				GPoint bottomRight = new GPoint(topLeft.getX() + diameter, topLeft.getY() + diameter);
				
				//If the ball moves rightwards (slightly) and hits the left fraction of the paddle
				if(bottomRight.getX() >= paddle.getX() && bottomRight.getX() <= paddle.getX() + paddleFract * parts && vx>=0)		
					vx = -vx;
				
				//If the ball moves leftwards (slightly) and hits the right fraction of the paddle
				if(bottomLeft.getX() >= paddle.getX() + paddleFract * parts - 1 && bottomLeft.getX() <= paddle.getX() + paddleFract * parts && vx<=0)
					vx = -vx;
				
				BOUNCE_SOUND.play();
			}
			
			else
			{
				BRICKS_LEFT--;
				score.setLabel("Bricks remaining: " + BRICKS_LEFT);
				remove(collider);
				vy = -(vy);
				
				BOUNCE_SOUND.play();
				
				if(BRICKS_LEFT == 0)
				{
					break;
				}
			}
			
		}
    }	//END MOVING BALL
    
    private GObject getCollidingObject()
    {
    	int diameter = 2 * BALL_RADIUS;
    	
    	//Points of the Rectangle enclosing the ball object
    	GPoint topLeft = new GPoint(ball.getX(), ball.getY());
    	GPoint topRight = new GPoint(topLeft.getX() + diameter, topLeft.getY());
    	GPoint bottomLeft = new GPoint(topLeft.getX(), topLeft.getY() + diameter);
    	GPoint bottomRight = new GPoint(topLeft.getX() + diameter, topLeft.getY() + diameter);
    			
    	GObject element;
    			
    	element=getElementAt(topLeft);
    	if(element!=null)
    		return element;
    			
    	element=getElementAt(topRight);
    	if(element!=null)
    		return element;
    			
    	element=getElementAt(bottomLeft);
    	if(element!=null)
    		return element;
    			
    	element=getElementAt(bottomRight);
    	if(element!=null)
    		return element;
    			
    	return null;
    }	//END COLLIDING OBJECT
    
    public void SetupPaddle()
    {
    	int x = getWidth()/2 - PADDLE_WIDTH/2;
		int y = (getHeight()- PADDLE_OFFSET);
		
		paddle = new GRect(x,y,PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFillColor(Color.WHITE);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
    }	//END SETUP PADDLE
    
    public void mouseMoved(MouseEvent e)
	{
		int x = e.getX();
		if((x < 0) || (x > getWidth()))
			return;
		
		if((x >= 0) && (x < getWidth() - PADDLE_WIDTH))
		{
			int dx = (int) paddle.getX() - x;
			paddle.move(-dx, 0);
			add(paddle);
		}
	}	//END MOUSE MOVEMENT
    
    public void SetupBricks()
	{
		//Define Bricks
		bricks = new GRect[BRICKS_ROW][BRICKS_PER_ROW];
		
		//Starting Position
		int X = (getWidth() - APP_WIDTH)/2;
		int Y = BRICKS_OFFSET;
		
		//Temporary x and y
		int x = X;
		int y = Y;
		
		//Layout
		for(int brickRow = 0; brickRow < BRICKS_ROW; brickRow++)
		{
			for(int brickNum = 0; brickNum < BRICKS_PER_ROW; brickNum++)
			{
				Color color = setupBricksColorChooser(brickRow+1);
				
				setupBricksOfColor(brickRow, brickNum, x, y, color);
				add(bricks[brickRow][brickNum]);
				
				x = x + BRICK_WIDTH + BRICKS_SEP;
			}
			x = X;
			y += BRICK_HEIGHT;
		}
	}	//END SETUP BRICKS
    
    private void setupBricksOfColor(int row, int column, int x, int y, Color color)
	{
		bricks[row][column] = new GRect(x,y,BRICK_WIDTH, BRICK_HEIGHT);
		bricks[row][column].setFillColor(color);
		bricks[row][column].setFilled(true);
	}	//END BRICKS COLOR
    
    private Color setupBricksColorChooser(int n)
	{
		switch(n)
		{
		case 1: 
		case 2: return Color.RED;
		case 3:
		case 4: return Color.ORANGE;
		case 5:
		case 6: return Color.YELLOW;
		case 7:
		case 8: return Color.GREEN;
		case 9: 
		case 10: return Color.CYAN;
		
		default: return Color.BLACK;
		}
	}	//END BRICKS COLOR CHOOSER
    
    public void Credits()
	{				
		name1.setColor(Color.CYAN);
		name2.setColor(Color.ORANGE);
		
		int y = (int) name2.getDescent();	//To calculate y offset
		add(name1,(getWidth() - name1.getWidth())/2, getHeight());
		add(name2,(getWidth() - name2.getWidth())/2, getHeight() + 4 * y);
				
	}	//END CREDITS
}
