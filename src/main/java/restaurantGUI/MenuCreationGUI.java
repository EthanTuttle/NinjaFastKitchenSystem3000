package src.main.java.restaurantGUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import src.main.java.Backend.Menu;
import src.main.java.Backend.MenuItem;

import java.util.ArrayList;
import java.util.Iterator;

public class MenuCreationGUI extends JPanel {

    private ArrayList<JTextField> itemFields = new ArrayList<>();
    private ArrayList<JButton> itemButtons = new ArrayList<>();
    private boolean loadingMenu = false;

    public MenuCreationGUI(Menu menu){
        setLayout(new BorderLayout());
        JTextField categField = new JTextField(10);
        JButton addCategButton = new JButton("Add Category");
        categPanel = new JPanel();
        enclosingCategPanel = new JPanel();
        scrollPanel = new JScrollPane(enclosingCategPanel);
        
        enclosingCategPanel.setLayout(new BoxLayout(enclosingCategPanel, BoxLayout.X_AXIS));
        categPanel.add(categField);
        categPanel.add(addCategButton);

        addCategButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent event){
                if (categField.getText().equals("")){
                    return;
                }
                JPanel singleCategPanel = new JPanel();
                JPanel enclosedSingleCategPanel = new JPanel(new BorderLayout());
                singleCategPanel.setLayout(new BoxLayout(singleCategPanel,BoxLayout.Y_AXIS));
                FlowLayout fl = new FlowLayout(FlowLayout.CENTER,0,0);
                FlowLayout fl2 = new FlowLayout(FlowLayout.CENTER,10,0);
                JPanel newMenuItemPanel = new JPanel(fl);
                JPanel labelAndExitPanel = new JPanel(fl2);
                System.out.println(fl.getVgap());
                
                JLabel newLabel = new JLabel(categField.getText());
                newLabel.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        handleMousePress(newLabel, "category");
                    }
                });
                JButton addMenuItemButton = new JButton(new ButtonAction("Add Menu Item", singleCategPanel));
                JButton removeComponentButton = new JButton(new RemoveComponentAction("X", enclosingCategPanel, enclosedSingleCategPanel));

                newMenuItemPanel.add(addMenuItemButton);
                
                labelAndExitPanel.add(newLabel);
                labelAndExitPanel.add(removeComponentButton);

                //singleCategPanel.add(newLabel);
                singleCategPanel.add(labelAndExitPanel);
                singleCategPanel.add(newMenuItemPanel);
                enclosedSingleCategPanel.add(singleCategPanel,BorderLayout.NORTH);
                enclosingCategPanel.add(enclosedSingleCategPanel);
                
                revalidate();
                scrollPanel.revalidate();
            }
        });

        add(categPanel,BorderLayout.NORTH);
        add(scrollPanel,BorderLayout.CENTER);

        loadingMenu = true;
        Iterator<String> itr1 = menu.allItems().keySet().iterator();
        while (itr1.hasNext()) {
            String category = itr1.next();
            categField.setText(category);
            addCategButton.getActionListeners()[0].actionPerformed(null);
            Iterator<String> itr2 = menu.allItems().get(category).keySet().iterator();
            while (itr2.hasNext()) {
                String name = itr2.next(); 
                MenuItem item = menu.allItems().get(category).get(name);
                itemFields.get(itemFields.size()-1).setText(item.getName());
                itemButtons.get(itemButtons.size()-1).getActionListeners()[0].actionPerformed(null);
            }
        }
        loadingMenu = false;
    }
    class ButtonAction extends AbstractAction {
        String name;
        JPanel parentPanel;
        public ButtonAction(String name, JPanel pPanel){
            super(name);
            this.name = name;
            this.parentPanel  = pPanel;
        }

        public void actionPerformed(ActionEvent event){
            JTextField menuItem = new JTextField();
            JTextField menuItemETM = new JTextField();
            JComponent[] components = new JComponent[] {
                new JLabel("Enter the name of the item"),
                menuItem,
                new JLabel("Enter the estimated time to make the menu item"),
                menuItemETM,
            };
            int result = JOptionPane.showConfirmDialog(null, components, "Add new menu item", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.OK_OPTION) {
                JLabel itemTimeCombo = new JLabel(menuItem.getText()+"  "+menuItemETM.getText());
                itemTimeCombo.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        handleMousePress(itemTimeCombo, "menu_item");
                    }
                });
                parentPanel.add(itemTimeCombo);
                parentPanel.revalidate();
                scrollPanel.revalidate();
            }
        }
    }
    class RemoveComponentAction extends AbstractAction{
        String name;
        JPanel parentPanel;
        JPanel childPanel;
        public RemoveComponentAction(String name, JPanel parentPanel, JPanel childPanel)
        {
            super(name);
            this.name = name;
            this.parentPanel = parentPanel;
            this.childPanel = childPanel;
        }
        public void actionPerformed(ActionEvent event)
        {
            this.parentPanel.remove(this.childPanel);
            this.parentPanel.revalidate();
            this.parentPanel.repaint();
        }
    }
    public void handleMousePress(JLabel text, String type)
    {
        String updatedString = null;
        JTextArea updatedMenuItem = new JTextArea();
        JTextArea updatedMenuItemETM = new JTextArea();
        if (type.toLowerCase().equals("category"))
        {
		    updatedString = JOptionPane.showInputDialog(this, "What would you like to rename the category to?");
        }
        else if (type.toLowerCase().equals("menu_item"))
        {
            JComponent[] components = new JComponent[] {
                new JLabel("Enter the name of the item"),
                updatedMenuItem,
                new JLabel("Enter the estimated time to make the menu item"),
                updatedMenuItemETM,
            };
            int result = JOptionPane.showConfirmDialog(null, components, "Add new menu item", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.OK_OPTION) {
                // Check if the input is valid
                updatedString = updatedMenuItem.getText()+" "+updatedMenuItemETM.getText();
            }
        }
        else{
            // The type provided is unexpected.
            System.err.println("Received type: "+type+" which was not expected.");
            return;
        }

        if (updatedString != null)
        {
            text.setText(updatedString);
        }
    }
    private JScrollPane scrollPanel;
    private JPanel categPanel;
    private JPanel enclosingCategPanel;
}
