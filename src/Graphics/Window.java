package Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.ActionListener;

public class Window extends JFrame {
    private final JMenuBar bar = new JMenuBar();
    private final Container content = getContentPane();
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
    public void buttonQuery(String question, Option[] options) {
        reset();
        label(question, constraints(0,0,options.length,1), null);
        for (int i = 0; i < options.length; i++) {
            Option o = options[i];
            JButton b = new JButton(o.text());
            b.addActionListener(o.action());
            add(b, constraints(i, 1, 1, 1));
        }
        revalidate();
    }

    // Resets the pane to a blank slate
    public void reset() {
        content.removeAll();
    }

    public static GridBagConstraints constraints(int x, int y, int w, int h) {
        return new GridBagConstraints(
                x,y,w,h,
                1,1, GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0, 0, 0, 0),0,0);
    }

    public static LineBorder border(Color colour, int size) {
        return new LineBorder(colour, size);
    }

    public void label(String text, GridBagConstraints constraints, LineBorder border) {
        JLabel label = new JLabel(text);
        if (border != null) label.setBorder(border);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, constraints);
    }

    public void button(String text, ActionListener action, GridBagConstraints constraints) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        add(button, constraints);
    }

    public JTextField textField(String text, GridBagConstraints constraints) {
        JTextField textField = new JTextField(text);
        add(textField, constraints);
        return textField;
    }
}