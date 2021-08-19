package src.main.java.restaurantGUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import src.main.java.Backend.Menu;
import src.main.java.Backend.MenuItem;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * MenuCreationGUI Class is the interface for a <b>Customer</b> 
 * to place a <b>Menu</b>
 */
public class MenuCreationGUI extends JPanel {
    
    /**
     * JTextFields in this Menu Creation GUI
     */
    private ArrayList<JTextField> itemFields = new ArrayList<>();
    /**
     * Item Buttons used in the Menu Creation GUI
     */
    private ArrayList<JButton> itemButtons = new ArrayList<>();
    /**
     * Index used to keep track of which add menu item btn is being clicked
     */
    private int itemBtnIndex = 0;
    /**
     * Boolean to check if a menu is loading in
     */
    private boolean loadingMenu = false;
    /**
     * Boolean to check if a menu has not been loaded yet
     */
    private boolean firstLoad = false;
    /**
     * Menu to populate with new categories and menu items
     */
    private Menu menu;
    /**
     * Scroll panel to scroll through different categories
     */
    private JScrollPane scrollPanel;
    /**
     * Panel to add new categories
     */
    private JPanel categPanel;
    /**
     * Panel that holds the categories and their menu items
     */
    private JPanel enclosingCategPanel;
    
    /**
     * Instantiates Menu CreationGUI by loading in the menu
     * @param menu Menu to populate
     */
    public MenuCreationGUI(Menu menu){
        setLayout(new BorderLayout());
        this.menu = menu;
        JTextField categField = new JTextField(10);
        JButton addCategButton = new JButton("Add Category");
        categPanel = new JPanel();
        enclosingCategPanel = new JPanel();
        scrollPanel = new JScrollPane(enclosingCategPanel);
        
        enclosingCategPanel.setLayout(new BoxLayout(enclosingCategPanel, BoxLayout.X_AXIS));
        categPanel.add(categField);
        categPanel.add(addCategButton);

        addCategButton.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent event){
                if (loadingMenu == false && !(checkValidValue(categField.getText(),"category"))){
                    return;
                }
                JPanel singleCategPanel = new JPanel();
                JPanel enclosedSingleCategPanel = new JPanel(new BorderLayout());
                singleCategPanel.setLayout(new BoxLayout(singleCategPanel,BoxLayout.Y_AXIS));
                FlowLayout fl = new FlowLayout(FlowLayout.CENTER,0,0);
                FlowLayout fl2 = new FlowLayout(FlowLayout.CENTER,10,0);
                JPanel newMenuItemPanel = new JPanel(fl);
                JPanel labelAndExitPanel = new JPanel(fl2);
                JLabel categLabel = new JLabel(categField.getText().strip());
                categLabel.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        handleMousePress(categLabel, "category", enclosedSingleCategPanel);
                    }
                });
                JButton addMenuItemButton = new JButton(new ButtonAction("Add Menu Item", singleCategPanel));
                JButton removeComponentButton = new JButton(new RemoveComponentAction("X", enclosingCategPanel, enclosedSingleCategPanel));
                removeComponentButton.setBackground(Color.red);
                itemButtons.add(addMenuItemButton);
                singleCategPanel.setName("category="+categLabel.getText()+";menu_item= ");
                enclosedSingleCategPanel.setName("category="+categLabel.getText()+";menu_item= ");
                newMenuItemPanel.add(addMenuItemButton);
                labelAndExitPanel.add(categLabel);
                labelAndExitPanel.add(removeComponentButton);

                singleCategPanel.add(labelAndExitPanel);
                singleCategPanel.add(newMenuItemPanel);
                enclosedSingleCategPanel.add(singleCategPanel,BorderLayout.NORTH);
                enclosingCategPanel.add(enclosedSingleCategPanel);
                // If it's the first load we do want to populate the menu otherwise only populate it when we're not loading a menu
                if (firstLoad == true || loadingMenu == false){
                    menu.addCategory(categLabel.getText());
                }
                revalidate();
                scrollPanel.revalidate();
            }
        });

        add(categPanel,BorderLayout.NORTH);
        add(scrollPanel,BorderLayout.CENTER);

        if (menu.isEmpty()){
            firstLoad = true;
            loadingMenu = true;
            categField.setText("Category 1");
            addCategButton.getActionListeners()[0].actionPerformed(null);
            itemButtons.get(0).doClick();
            categField.setText("Category 2");
            addCategButton.getActionListeners()[0].actionPerformed(null);
            categField.setText("Category 3");
            addCategButton.getActionListeners()[0].actionPerformed(null);
            loadingMenu=false;
            firstLoad = false;
        }
        else
        {
            // If a menu exists than load it instead of showing an empty menu
            loadingMenu = true;
            Iterator<String> itr1 = menu.allItems().keySet().iterator();
            while (itr1.hasNext()) {
                String category = itr1.next();
                categField.setText(category);
                addCategButton.doClick();
                Iterator<String> itr2 = menu.allItems().get(category).keySet().iterator();
                while (itr2.hasNext()) {
                    String name = itr2.next(); 
                    MenuItem item = menu.allItems().get(category).get(name);
                    JTextField menuItemName = new JTextField();
                    menuItemName.setText(name);
                    JTextField menuItemETM = new JTextField();
                    menuItemETM.setText(String.valueOf(item.getTimeToMake()));
                    itemFields.add(menuItemName);
                    itemFields.add(menuItemETM);
                    itemButtons.get(itemButtons.size()-1).doClick();
                }
            }
            loadingMenu = false;
        }
    }
    /**
     * Check if a category or menu item entered is valid
     * @param value Category of Menu Item
     * @param type Type specifying if we are checking a category or menu item
     * @return True if value is vald or false if it is not
     */
    public boolean checkValidValue(String value, String type){
        if (type.equals("category")){
            /*check dup category or empty value*/
            if (value == null || value.equals("") || menu.containsCategory(value)){
                return false;
            }
        }
        else if (type.equals("menu_item")){
            // Check that the value is not empty before checking if any values can exist
            String[] itemTimeCombo = value.split("  ");
            // Check that the value is not empty before checking if any values can exist
            // If values exist check that at least 2 values were parsed
            if (value.equals("") || itemTimeCombo.length < 2){
                return false;
            }
            String menuItem = itemTimeCombo[0];
            String timeToMake = itemTimeCombo[1];
            /*check that the menu item is not empty and that the menu item does not already exist*/
            if (value == null || menuItem.equals("") || menu.findMenuItem(menuItem) != null){
                return false;
            }
            try {
                Integer actualTimeToMake = Integer.parseInt(timeToMake);
                if (actualTimeToMake<0){
                    throw new Exception();
                }
            }
            catch (Exception n){
                return false;
            }

        }
        return true;
    }

    /**
     * Button ActionClass that defines the action to add a 
     * <b>MenuItem</b> to a category in the <b>Menu</b>
     */
    class ButtonAction extends AbstractAction {
        /**
         * Name of Button Action
         */
        String name;
        /**
         * Parent panel of button
         */
        JPanel parentPanel;
        
        /**
         * Instanties the button action
         * @param name Name of Action
         * @param pPanel Parent Panel of Action/Button
         */
        public ButtonAction(String name, JPanel pPanel){
            super(name);
            this.name = name;
            this.parentPanel  = pPanel;
        }
        
        @Override
        public void actionPerformed(ActionEvent event){
            JTextField menuItem = new JTextField();
            //JTextField menuItemETM = new JTextField();
            SpinnerModel model = new SpinnerNumberModel(0,0,59,1);
            JSpinner menuItemMin = new JSpinner(model);
            JSpinner menuItemSec = new JSpinner(model);
            JComponent[] components = new JComponent[] {
                new JLabel("Enter the name of the item"),
                menuItem,
                new JLabel("Enter the estimated minutes to make the menu item"),
                menuItemMin,
                new JLabel("Enter the estimated seconds to make the menu item"),
                menuItemSec
            };
            int result = -1;
            if (firstLoad){
                result = 0;
                menuItem.setText("Menu Item");
                menuItemMin.setValue("10");
            }
            else if (loadingMenu){
                result = 0;
                menuItem.setText(itemFields.get(itemFields.size()-2).getText());
                menuItemMin.setValue(itemFields.get(itemFields.size()-1).getText());
            }
            else{
                result = JOptionPane.showConfirmDialog(null, components, "Add new menu item", JOptionPane.YES_NO_OPTION);
            }
            if(result == JOptionPane.OK_OPTION) {
                int minutes = (Integer) menuItemMin.getValue();
                int seconds = (Integer) menuItemSec.getValue();
                int totalSeconds = (minutes*60) + seconds;
                JLabel itemTimeCombo = new JLabel(menuItem.getText().strip()+"  "+ totalSeconds);
                if (loadingMenu == false && !(checkValidValue(itemTimeCombo.getText(),"menu_item"))){
                    return;
                }
                ArrayList<String> values = parseQuery(this.parentPanel);
                String category = values.get(0);
                // If it's the first time we load something into the menu from the GUI we want to populate the menu
                if (firstLoad || loadingMenu == false){
                    menu.addMenuItem(category, menuItem.getText().strip(), totalSeconds);
                }
                itemTimeCombo.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        handleMousePress(itemTimeCombo, "menu_item", parentPanel);
                    }
                });
                JPanel labelAndExitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
                JButton removeComponentButton = new JButton(new RemoveComponentAction("X", this.parentPanel, labelAndExitPanel));
                removeComponentButton.setBackground(Color.RED);
                labelAndExitPanel.setName("LabelAndExitcategory="+category+";menu_item="+menuItem.getText().strip());
                parentPanel.setName("ParentPanelcategory="+category+";menu_item="+menuItem.getText().strip());
                labelAndExitPanel.add(itemTimeCombo);
                labelAndExitPanel.add(removeComponentButton);
                parentPanel.add(labelAndExitPanel);
                parentPanel.revalidate();
                scrollPanel.revalidate();
            }
        }
    }
    /**
     * RemoveComponentAction class provides functionality
     * to remove a <b>Category</b> or <b>Menu Item</b> when 
     * needed.
     */
    class RemoveComponentAction extends AbstractAction{
        /**
         * Name of Action
         */
        String name;
        /**
         * Parent Panel of Action
         */
        JPanel parentPanel;
        /**
         * Child Panel of Action
         */
        JPanel childPanel;

        /**
         * Instantiates a Remove Component Action
         * @param name Name of Action
         * @param parentPanel Parent Panel of Action
         * @param childPanel Child Panel of Action
         */
        public RemoveComponentAction(String name, JPanel parentPanel, JPanel childPanel)
        {
            super(name);
            this.name = name;
            this.parentPanel = parentPanel;
            this.childPanel = childPanel;
        }
        @Override
        public void actionPerformed(ActionEvent event)
        {
            // this.childPanel.getName() will return
            // category=?;menu_item=?

            // Split the query into category=? AND menu_item=?
            ArrayList<String> values = parseQuery(this.childPanel);
            String category = values.get(0);
            String menuItem = values.get(1);
            menu.remove(category, menuItem, menuItem.equals(" ") ? "category" : "menu_item");
            this.parentPanel.remove(this.childPanel);
            this.parentPanel.revalidate();
            this.parentPanel.repaint();
        }
    }
    /**
     * Provides functionality to change a menu item or category by clicking on it
     * @param text Text to change category or menu item to
     * @param type Specifying whether we want to change a category or menu item
     * @param childPanel Child Panel of Action
     */
    public void handleMousePress(JLabel text, String type, JPanel childPanel)
    {
        String updatedString = null;
        JTextArea updatedMenuItem = new JTextArea();
        JTextArea updatedMenuItemETM = new JTextArea();
        ArrayList<String> values = parseQuery(childPanel);
        String category = values.get(0);
        if (type.toLowerCase().equals("category"))
        {
		    updatedString = JOptionPane.showInputDialog(this, "What would you like to rename the category to?");
            if (!checkValidValue(updatedString,"category")){
                return;
            }
            menu.replace(category, updatedString);
            String oldMenuItem = values.get(1).equals("") ? " " : values.get(1);
            childPanel.setName("category="+updatedString+";menu_item="+oldMenuItem);
            category = updatedString;
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
                updatedString = updatedMenuItem.getText().strip()+"  "+updatedMenuItemETM.getText().strip();
                if (!checkValidValue(updatedString,"menu_item")){
                    return;
                }
                // text == menu_item=? <=== and we parse for '?'
                String menuItem = text.getText().strip().split("  ")[0];
                menu.remove(category, menuItem, "menu_item");
                menu.addMenuItem(category, updatedMenuItem.getText().strip(), Integer.parseInt(updatedMenuItemETM.getText()));
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
            Component[] allComponents = childPanel.getComponents();
            for (int i = 0; i < allComponents.length; i++){
                if (allComponents[i].getName() != null){
                    String newMenuItem = updatedMenuItem.getText().strip().equals("") ? " " : updatedMenuItem.getText().strip();
                    allComponents[i].setName("category="+category+";menu_item="+newMenuItem);
                }
            }
            childPanel.revalidate();
        }
    }
    /**
     * Parses input that for a category or menu item
     * @param childPanel Parse the name of this panel
     * @return An ArrayList of strings where the first value is the category and the second value is the menu_item
     */
    private ArrayList<String> parseQuery(JPanel childPanel)
    {
        // Split the query into category=? AND menu_item=?
        System.out.println("query is: \'"+childPanel.getName()+"\'");
        String[] query = childPanel.getName().split(";");
        for (String val : query){
            System.out.println("query split val is \""+val+"\"");
        }
        ArrayList<String> ret = new ArrayList<String>();

        // parse, split and get the category and menu item
        String[] values = query[0].split("=");
        //category = values[1];
        ret.add(values[1]);
        values = query[1].split("=");
        //menu_item = values[1];

        /*//Debug Statements
        for (String vals: values){
            System.out.println("Values are: \""+vals+"\"");
        }*/
        ret.add(values[1]);
        return ret;
    }

}
