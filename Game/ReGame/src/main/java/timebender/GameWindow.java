package timebender;

import javax.swing.*;
import java.awt.*;

/**
 * Class implements the notion of window of the game
 * wndFrame member is a JFrame object that has the utility of a graphic window and also of a continer
 * (all grapfical elements will be contained by window.
 */
public class GameWindow
{
    private JFrame  wndFrame;
    private String  wndTitle;
    private int     wndWidth;
    private int     wndHeight;

    private Canvas  canvas;

    public GameWindow(String title, int width, int height){
        wndTitle = title;
        wndWidth = width;
        wndHeight = height;
        wndFrame = null;
    }

    public void buildGameWindow()
    {
        if(wndFrame != null)
        {
            return;
        }

        wndFrame = new JFrame(wndTitle);
        wndFrame.setSize(wndWidth, wndHeight);
        wndFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wndFrame.setResizable(false);
        wndFrame.setLocationRelativeTo(null);
        wndFrame.setVisible(true);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(wndWidth, wndHeight));
        canvas.setMaximumSize(new Dimension(wndWidth, wndHeight));
        canvas.setMinimumSize(new Dimension(wndWidth, wndHeight));

        wndFrame.add(canvas);
        wndFrame.pack();
    }

    public int getWndWidth()
    {
        return wndWidth;
    }

    public int getWndHeight()
    {
        return wndHeight;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getJFrame() { return wndFrame; }
}
