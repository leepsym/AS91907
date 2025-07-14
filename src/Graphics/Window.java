package Graphics;

import javax.swing.JFrame;
import java.awt.GridBagLayout;

public class Window extends JFrame {
    public Window(int width, int height) {
        this();
        setSize(width, height);
        setTitle("AS91907 | Virus Simulator Statistics");
    }

    public Window(){
        setTitle("AS91907 | Virus Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());
        setResizable(false);

        setVisible(true);
    }
}