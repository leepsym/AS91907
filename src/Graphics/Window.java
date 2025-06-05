package Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.*;
import java.awt.event.ActionListener;

public class Window extends JFrame {
    private final JMenuBar bar = new JMenuBar();
    private final Container content = getContentPane();
    public Window() {
        setTitle("AS91907 | Virus Simulator");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());
        setVisible(true);
    }
    public void buttonQuery(String question, Option[] options) {
        reset();
        label(question, constraints(0,0,options.length,1));
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

    private void menuItem(String text, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(action);
        bar.add(item);
    }

    public void label(String text, GridBagConstraints constraints) {
        JLabel label = new JLabel(text);
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