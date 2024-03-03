import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        // setVisible ensures that the user can see the window
        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        // setLocationRelativeTo(null) is centering the window
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        // adding the pack makes sure that the title is not accounted in calculating the
        // dimensions of width and height.
        frame.pack();
        flappyBird.requestFocus();
        // the frame.setVisibility must be added after the JPanle is added
        frame.setVisible(true);
    }
}