package com.fadyfaheem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI extends Main {
    public static JButton buttonSetup(String buttonText, int fontSize, int x, int y, int width, int height, ActionListener listener, boolean enabled){
        JButton button = new JButton();
        button.setText(buttonText); // sets Button Text
        button.setBounds(x,y,width,height);  // Create bounds for button
        button.setFont(new Font("Myriad", Font.PLAIN, fontSize)); // Sets font to Myriad (because it looks better)
        button.setForeground(Color.BLACK); // Sets text font to white
        button.setFocusPainted(false); // removes ugly box around text
        button.setBackground(Color.white);
        button.addActionListener(listener);
        button.setEnabled(enabled);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(Color.decode("#212426"), 8, 40));
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                UIManager.put("Button.select", Color.gray);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        return button;
    }


    public static JLabel labelSetup(String text, int fontSize, int x, int y, int width, int height, boolean enabled){
        JLabel newLabel = new JLabel(text, SwingConstants.CENTER);
        newLabel.setBounds(x,y,width,height);
        newLabel.setFont(new Font("Myriad", Font.PLAIN, fontSize));
        newLabel.setForeground(Color.white);
        newLabel.setEnabled(enabled);
        return newLabel;
    }
}
