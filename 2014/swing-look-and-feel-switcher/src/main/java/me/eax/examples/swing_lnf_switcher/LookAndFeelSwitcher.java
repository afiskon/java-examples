package me.eax.examples.swing_lnf_switcher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;


public class LookAndFeelSwitcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }

    private static void createGUI() {
        JList<String> list = new JList<>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScrollPane = new JScrollPane(list);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(listScrollPane, BorderLayout.CENTER);

        ActionListener updateButtonActionListener = new UpdateListAction(list);
        updateButtonActionListener.actionPerformed(new ActionEvent(list, ActionEvent.ACTION_PERFORMED, null));

        JButton updateListButton = new JButton("Update list");
        JButton updateLookAndFeelButton = new JButton("Update Look&Feel");

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        buttonsPanel.add(updateListButton);
        buttonsPanel.add(Box.createHorizontalStrut(5));
        buttonsPanel.add(updateLookAndFeelButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(buttonsPanel);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panel.setLayout(new BorderLayout());
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        JFrame frame = new JFrame("Look&Feel Switcher");
        frame.setMinimumSize(new Dimension(300, 200));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        updateListButton.addActionListener(updateButtonActionListener);
        updateLookAndFeelButton.addActionListener(new UpdateLookAndFeelAction(frame, list));
    }

    static class UpdateListAction implements ActionListener {
        private JList<String> list;

        public UpdateListAction(JList<String> list) {
            this.list = list;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            ArrayList<String> lookAndFeelList = new ArrayList<>();
            UIManager.LookAndFeelInfo[] infoArray = UIManager.getInstalledLookAndFeels();
            int lookAndFeelIndex = 0;
            int currentLookAndFeelIndex = 0;
            String currentLookAndFeelClassName = UIManager.getLookAndFeel().getClass().getName();
            for(UIManager.LookAndFeelInfo info : infoArray) {
                if(info.getClassName().equals(currentLookAndFeelClassName)) {
                    currentLookAndFeelIndex = lookAndFeelIndex;
                }
                lookAndFeelList.add(info.getName());
                lookAndFeelIndex++;
            }
            String[] listDataArray = new String[lookAndFeelList.size()];
            final String[] newListData = lookAndFeelList.toArray(listDataArray);
            final int newSelectedIndex = currentLookAndFeelIndex;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    list.setListData(newListData);
                    list.setSelectedIndex(newSelectedIndex);
                }
            });
        }
    }

    static class UpdateLookAndFeelAction implements ActionListener {
        private JList<String> list;
        private JFrame rootFrame;

        public UpdateLookAndFeelAction(JFrame rootFrame, JList<String> list) {
            this.rootFrame = rootFrame;
            this.list = list;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String lookAndFeelName = list.getSelectedValue();
            UIManager.LookAndFeelInfo[] infoArray = UIManager.getInstalledLookAndFeels();
            for(UIManager.LookAndFeelInfo info : infoArray) {
                if(info.getName().equals(lookAndFeelName)) {
                    String message = "Look and feel was changed to " + lookAndFeelName;
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                        SwingUtilities.updateComponentTreeUI(rootFrame);
                    } catch (ClassNotFoundException e1) {
                        message = "Error: class " + info.getClassName() + " not found";
                    } catch (InstantiationException e1) {
                        message = "Error: instantiation exception";
                    } catch (IllegalAccessException e1) {
                        message = "Error: illegal access";
                    } catch (UnsupportedLookAndFeelException e1) {
                        message = "Error: unsupported look and feel";
                    }
                    JOptionPane.showMessageDialog(null, message);
                    break;
                }
            }
        }
    }
}
