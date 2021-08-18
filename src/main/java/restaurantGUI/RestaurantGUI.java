package src.main.java.restaurantGUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import src.main.java.Backend.*;
import java.net.Socket;

public class RestaurantGUI extends JFrame{

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new RestaurantGUI();
            }
        });
    }

    private ActiveOrdersDisplay activeOrdersDisplay;
    private src.main.java.Backend.Menu menu;
    private ServerSocket server;

    public RestaurantGUI() {
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(new GUIMenu(this));
        setTitle("Restaurant View");
        try {
            server = new ServerSocket(55555);
        } catch (IOException e) {
            System.out.println(e);
            return;
        } 
        
        menu = MenuLoader.loadMenu();
        activeOrdersDisplay = new ActiveOrdersDisplay(new ActiveOrders(), menu);
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Socket client = server.accept();
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        new Thread(new Runnable() {
                            public void run() {
                                Map<String,HashMap<String,src.main.java.Backend.MenuItem>> allItems = menu.allItems();
                                Iterator<String> itr = allItems.keySet().iterator();
                                while(itr.hasNext()) {
                                    Map<String, src.main.java.Backend.MenuItem> cat = menu.getMenuItems(itr.next());
                                    Iterator<String> itr2 = cat.keySet().iterator();
                                    while(itr2.hasNext()) {
                                        out.println(itr2.next());
                                    }
                                }
                                out.println("End of Menu");
                                while(client.isConnected()) { //while connected, look for communication
                                    try {
                                        activeOrdersDisplay.addOrder(in.readLine());
                                    } catch (IOException e) {
                                        break;
                                    }
                                    
                                }
                            }
                        }).start(); //run thread to send all menu items
                    } catch (IOException e) {
                        //TODO
                        System.out.println(e);
                    }
                }
            }
        }).start();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));
        mainPanel.add(new JLabel("Choose where to go to start", SwingConstants.CENTER));
        JPanel buttonPanel = new JPanel();
        JButton menuButton = new JButton("To Menu Configuration");
        //TODO: create menubutton action listener
        JButton displayButton = new JButton("To Order Display");
        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                remove(mainPanel);
                add(activeOrdersDisplay);
                revalidate();
            }
        });
        buttonPanel.add(menuButton);
        buttonPanel.add(displayButton);
        mainPanel.add(buttonPanel);
        add(mainPanel);

        setVisible(true);
        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
    }

    public src.main.java.Backend.Menu menu() {
        return menu;
    }

}
