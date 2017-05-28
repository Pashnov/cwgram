package com.apashnov.cwgram.cw;

import javax.swing.*;

public class ViewInputCode {

    static JFrame f= new JFrame();
    static JTextField text = new JTextField();
    static JTextField code = new JTextField();

    static {
        text.setBounds(10, 10, 280, 20);
        code.setBounds(10, 30, 100, 20);
        f.add(text);
        f.add(code);
        f.setSize(320, 100);
        f.setLayout(null);

    }

    public static void show (){
        f.setVisible(true);
    }

    public static void hide(){
        f.setVisible(false);
    }

    public static JTextField getText() {
        return text;
    }

    public static JTextField getCode() {
        return code;
    }
}
