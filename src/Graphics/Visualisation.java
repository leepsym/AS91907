package Graphics;

import Main.Simulation;
import Main.Subject;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class Visualisation {
    private final Window window = new Window();
    public static ArrayList<Pixel> pixelStack = new ArrayList<>();

    static Container contentPane = new Container() {
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(Color.BLACK);
            int width = getWidth() - 1;
            int height = getHeight() - 1;

            for (int i = 0; i <= Simulation.size[0]; i++) {
                g2.draw(new Line2D.Float(left(i), 0, left(i), height));
            }

            for (int i = 0; i <= Simulation.size[1]; i++) {
                g2.draw(new Line2D.Float(0, top(i), width, top(i)));
            }
        }
    };

    public Visualisation() {
        window.setContentPane(contentPane);
        contentPane.setPreferredSize(new Dimension(Simulation.size[0] * 10 + 1, Simulation.size[1] * 10 + 1));
        window.setSize(window.getPreferredSize());
    }


    public static void visualiseRound() {
        int[][][] count = new int[Simulation.size[0]][Simulation.size[1]][3];

        for (int i = 0; i < Simulation.size[0]; i++) {
            for (int j = 0; j < Simulation.size[1]; j++) {
                for (Subject subject : Simulation.board[i][j]) {
                    if (subject.infected){
                        count[i][j][0]++;
                    } else if (subject.infectable) {
                        count[i][j][1]++;
                    } else {
                        count[i][j][2]++;
                    }
                }
                int k = 0;
                for (; k < count[i][j][0]; k ++) {
                    Visualisation.pixelStack.add(new Pixel(i + top(k % 9), j + left((k / 9) % 9), Color.red));
                }
                for (; k < count[i][j][0] + count[i][j][1]; k ++) {
                    Visualisation.pixelStack.add(new Pixel(i + top(k % 9), j + left((k / 9) % 9), Color.green));
                }
                for (; k < count[i][j][0] + count[i][j][1] + count[i][j][2]; k ++) {
                    Visualisation.pixelStack.add(new Pixel(i + top(k % 9), j + left((k / 9) % 9), Color.gray));
                }
            }
        }
    }

    public static int top(int y) {return y * 10;}
    public static int bottom(int y) {return top(y + 1);}
    public static int left(int x) {return x * 10;}
    public static int right(int x) {return left(x + 1);}

    private record Pixel(int x, int y, Color colour) {}

}