import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image yogyaImg;

    // Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    int yogyaWidth = 34;
    int yogyaHeight = 39;

    class Bird {
        int x = birdX;
        int y = birdY;
        int height = birdHeight;
        int width = birdWidth;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    class Yogya {
        int x = birdX;
        int y = birdY;
        int height = yogyaHeight;
        int width = yogyaWidth;
        Image img;

        Yogya(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }


    // game logic
    Bird bird;
    Yogya yogya;
    int velocityY = 0;
    int gravity = 1;
    int velocityX = -4; // when the pipes move to the left

    ArrayList<Pipe> pipes; // stores the list of pipes in this game
    Random random = new Random();

    Timer gameloop; // Timer allows you to start a timer in the background
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // sets or gets focusable state of the component.
        setFocusable(true);
        // add key listener allows us this class to listen to all the three keyListener functions
        addKeyListener(this);

        setBackground(Color.blue);

        // load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("bottompipe.png")).getImage();
        yogyaImg = new ImageIcon(getClass().getResource("./yogya.png")).getImage();

        // bird
        yogya = new Yogya(yogyaImg);
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();

        // game timer
        // the first variables specifies the amount of time for the window to be drawn,
        //  the 1000 means 1 second, 1000/60 means 60 times per second.
        // the second argument regers to an ActionListener, this refers to the
        gameloop = new Timer(1000/60, this);
        // the timer.start() function starts the timer
        gameloop.start();


    }

    public void placePipes() {
        // (0-1) * pipeHeight /2

        int randomPipeY = (int)(pipeY - pipeHeight / 4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    // the paintComponent function will be called when 'needed',
    // much of the detialed for this function isn;t very visible,
    // and there are a number of factors
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

    }

    public void draw(Graphics g) {
        // background
        // graphiscs.drawImage() takes on 6 components, which are img, x coord, y coord, width, height, observer: null)
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        // g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        g.drawImage(yogya.img, yogya.x, yogya.y, yogya.width, yogya.height, null);
        // pipes
        for (int i = 0; i< pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 35));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

    }

    public void move() {
        // bird
        velocityY += gravity; // velocity will slow down consistently
        bird.y += velocityY;

        // Math.max(variable, threshold) limits the maximum value that the variable
        //    can get to.
        bird.y = Math.max(bird.y, 0);
        bird.y = Math.min(bird.y, boardHeight + birdHeight);


        for(int i =0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // two pipes adding up.
            }

            if(collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y + birdHeight > boardHeight) {
            gameOver = true;
        }
    }

    public void yogyaMove() {
        // bird
        velocityY += gravity; // velocity will slow down consistently
        yogya.y += velocityY;

        // Math.max(variable, threshold) limits the maximum value that the variable
        //    can get to.
        yogya.y = Math.max(yogya.y, 0);
        yogya.y = Math.min(yogya.y, boardHeight + yogyaHeight);


        for(int i =0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && yogya.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // two pipes adding up.
            }


            if(collisionYogya(yogya, pipe)) {
                gameOver = true;
            }
        }

        if (yogya.y + birdHeight > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird bird, Pipe pipe) {
        return bird.x < pipe.x + pipe.width &&
                bird.x + bird.width > pipe.x &&
                bird.y < pipe.y + pipe.height &&
                bird.y + bird.height > pipe.y;
    }

    public boolean collisionYogya(Yogya yogya, Pipe pipe) {
        return yogya.x < pipe.x + pipe.width &&
                yogya.x + yogya.width > pipe.x &&
                yogya.y < pipe.y + pipe.height &&
                yogya.y + yogya.height > pipe.y;
    }

    // the actionPerformed function will be called every specified amount of time
    @Override
    public void actionPerformed(ActionEvent e) {
        //move
        yogyaMove();
        // the repaint function will call the paintCompoenent funtion.
        repaint();

        if(gameOver) {
            placePipeTimer.stop();
            gameloop.stop();
        }
    }


    // EFFECTS: is when you type on a key that has a character (ex. "A")
    @Override
    public void keyTyped(KeyEvent e) {

    }

    // EFFECTS: is when all keys are pressed
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
    }

    // EFFECTS: is when the key is released after it is pressed.
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
