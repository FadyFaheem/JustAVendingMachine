package com.fadyfaheem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI extends Main {
    public static JButton buttonSetup(String buttonText, int fontSize, int x, int y, int width, int height, ActionListener listener, boolean enabled){
        int actualScreenResHeight = 1920; // CONSTANT DO NOT CHANGE
        int actualScreenResWidth = 1080; // CONSTANT DO NOT CHANGE
        int screenHeight = 1920 / 3;
        int screenWidth = 1080 / 3;
        JButton button = new JButton();
        button.setText(buttonText); // sets Button Text
        double tmp_x = (double)screenWidth * ((double) x)/((double)actualScreenResWidth);
        double tmp_y = (double) screenHeight * ((double) y / (double) actualScreenResHeight);
        double tmp_w = (double) screenWidth * ((double) width / (double) actualScreenResWidth);
        double tmp_h = (double) screenHeight * ((double) height / (double) actualScreenResHeight);
        double fontSizeCorrection = (double) actualScreenResWidth / (double) screenWidth;
        button.setBounds((int)tmp_x,(int)tmp_y,(int) tmp_w, (int)tmp_h);  // Create bounds for button
        button.setFont(new Font("Myriad", Font.PLAIN, (int)(fontSize / fontSizeCorrection))); // Sets font to Myriad (because it looks better)
        button.setForeground(Color.BLACK); // Sets text font to white
        button.setFocusPainted(false); // removes ugly box around text
        button.setBackground(Color.white);
        button.addActionListener(listener);
        button.setEnabled(enabled);
        button.setFocusPainted(false);
        //button.setBorder(new RoundedBorder(Color.decode("#212426"), 8, 40));
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
        int actualScreenResHeight = 1920; // CONSTANT DO NOT CHANGE
        int actualScreenResWidth = 1080; // CONSTANT DO NOT CHANGE
        int screenHeight = 1920 / 3;
        int screenWidth = 1080 / 3;
        double fontSizeCorrection = (double) actualScreenResWidth / (double) screenWidth;
        JLabel newLabel = new JLabel(text, SwingConstants.CENTER);
        double tmp_x = (double)screenWidth*((double) x)/((double)actualScreenResWidth);
        double tmp_y = (double) screenHeight * ((double) y / (double) actualScreenResHeight);
        double tmp_w = (double) screenWidth * ((double) width / (double) actualScreenResWidth);
        double tmp_h = (double) screenHeight * ((double) height / (double) actualScreenResHeight);
        newLabel.setBounds((int)tmp_x,(int)tmp_y,(int)tmp_w,(int)tmp_h);
        newLabel.setFont(new Font("Myriad", Font.PLAIN, (int)(fontSize / fontSizeCorrection)));
        newLabel.setForeground(Color.white);
        newLabel.setEnabled(enabled);
        return newLabel;
    }
}
