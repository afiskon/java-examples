package me.eax.examples.tray_icon_example;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;

// TODO: сворачивать фрейм в системный трей http://stackoverflow.com/a/8909348
// TODO: предусмотреть уведомления через notify-send в Linux

public class TrayIconExample {

    public static final String APPLICATION_NAME = "TrayIconExample";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }

    private static void createGUI() {
        JFrame frame = new JFrame(APPLICATION_NAME);
        frame.setMinimumSize(new Dimension(300, 200));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        setTrayIcon();
    }

    private static void setTrayIcon() {
        if(! SystemTray.isSupported() ) {
            return;
        }

        PopupMenu trayMenu = new PopupMenu();
        MenuItem item = new MenuItem("Exit");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayMenu.add(item);

        URL imageURL = TrayIconExample.class.getResource("/images/icon32x32.png");

        Image icon = Toolkit.getDefaultToolkit().getImage(imageURL);
        // Image icon = new ImageIcon(imageURL, "Tray icon").getImage();
        TrayIcon trayIcon = new TrayIcon(icon, APPLICATION_NAME, trayMenu);
        trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        trayIcon.displayMessage(APPLICATION_NAME, "Application started!", TrayIcon.MessageType.INFO);
    }
}
