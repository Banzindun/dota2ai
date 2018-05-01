package cz.cuni.mff.kocur.Graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ConsolePane extends JPanel {
    
	public void paintComponent(Graphics g){
        g.setColor(Color.BLACK);
        g.setFont(new Font("Verdana",Font.BOLD,16));
        g.drawString("Hello there", 20, 20);
    }
	
	
}
