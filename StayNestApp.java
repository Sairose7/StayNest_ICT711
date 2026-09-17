import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/** Entry point: lets the user choose GUI or Text-Based Interface. */
public class StayNestApp {
    public static void main(String[] args) {
        StayNestSystem system = new StayNestSystem();
        try { system.loadUsers("data/users.csv"); }
        catch (Exception e) { System.out.println(e.getMessage()); }
        system.seedDemoData();

        if (args.length > 0 && args[0].equalsIgnoreCase("tbi")) { new StayNestTBI(system).run(); return; }
        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) { SwingUtilities.invokeLater(() -> new StayNestGUI(system).setVisible(true)); return; }
        String[] options = {"Graphical User Interface (GUI)", "Text-Based Interface (TBI)"};
        int choice = JOptionPane.showOptionDialog(null, "Choose your preferred StayNest interface:", "StayNest",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 0) SwingUtilities.invokeLater(() -> new StayNestGUI(system).setVisible(true));
        else if (choice == 1) new StayNestTBI(system).run();
        else saveAndExit(system);
    }

    static void saveAndExit(StayNestSystem system) {
        try { system.saveUsers("data/users.csv"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
}

class StayNestTBI {
    private final StayNestSystem system;
    private final java.util.Scanner scanner = new java.util.Scanner(System.in);
    StayNestTBI(StayNestSystem system) { this.system = system; }

    void run() {
        int choice;
        do {
            printMenu(); choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> addUser(); case 2 -> printUsers(system.getUsers()); case 3 -> searchUser();
                    case 4 -> updateUser(); case 5 -> deleteUser(); case 6 -> printProperties(system.getProperties());
                    case 7 -> addProperty(); case 8 -> deleteProperty(); case 9 -> addBooking();
                    case 10 -> printBookings(system.getBookings()); case 11 -> updateBooking(); case 12 -> deleteBooking(); case 13 -> searchBookings();
                    case 14 -> sortUsers(); case 15 -> sortProperties(); case 16 -> sortBookings();
                    case 17 -> addFeedback(); case 18 -> printFeedback(); case 0 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) { System.out.println("Operation failed: " + e.getMessage()); }
        } while (choice != 0);
        StayNestApp.saveAndExit(system); scanner.close();
    }

    private void printMenu() {
        System.out.println("\n========== STAYNEST TBI ==========");
        System.out.println("1 Add User | 2 View Users | 3 Search User | 4 Update User | 5 Delete User");
        System.out.println("6 View Properties | 7 Add Property | 8 Delete Property");
        System.out.println("9 Add Booking | 10 View Bookings | 11 Update Booking | 12 Delete Booking | 13 Search Bookings");
        System.out.println("14 Sort Users | 15 Sort Properties | 16 Sort Bookings");
        System.out.println("17 Add Feedback | 18 View Feedback | 0 Exit");
        System.out.println("==================================");
    }
    private int readInt(String prompt) {
        while (true) { System.out.print(prompt); try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); } }
    }
    private void addUser() { int id=readInt("ID: "); System.out.print("Name: "); String n=scanner.nextLine(); System.out.print("Email: "); String e=scanner.nextLine(); System.out.print("Role (Guest/Host): "); String r=scanner.nextLine(); system.addUser(r.equalsIgnoreCase("Host")?new Host(id,n,e):new Guest(id,n,e)); System.out.println("User added."); }
    private void searchUser() { int id=readInt("ID: "); Person p=system.findUserLinear(id); System.out.println(p==null?"User not found.":p); }
    private void updateUser() { int id=readInt("ID: "); System.out.print("New name: "); String n=scanner.nextLine(); System.out.print("New email: "); String e=scanner.nextLine(); System.out.println(system.updateUser(id,n,e)?"User updated.":"User not found."); }
    private void deleteUser() { System.out.println(system.deleteUser(readInt("ID: "))?"User deleted.":"User not found."); }
    private void addProperty() { int id=readInt("Property ID: "); System.out.print("Name: "); String n=scanner.nextLine(); System.out.print("Location: "); String l=scanner.nextLine(); double p=Double.parseDouble(readText("Price/night: ")); int h=readInt("Host ID: "); system.addProperty(new Property(id,n,l,p,h)); System.out.println("Property added."); }
    private void deleteProperty() { System.out.println(system.deleteProperty(readInt("Property ID: "))?"Property deleted.":"Property not found."); }
    private String readText(String prompt) { System.out.print(prompt); return scanner.nextLine().trim(); }
    private void addBooking() { int id=readInt("Booking ID: "); int g=readInt("Guest ID: "); int p=readInt("Property ID: "); String d=readText("Date (YYYY-MM-DD): "); system.addBooking(new Booking(id,g,p,d,"Confirmed")); System.out.println("Booking added."); }
    private void updateBooking() { int id=readInt("Booking ID: "); Booking b=system.findBooking(id); if(b==null){System.out.println("Booking not found.");return;} b.setDate(readText("New date (YYYY-MM-DD): ")); b.setStatus(readText("New status (Confirmed/Cancelled/Completed): ")); System.out.println("Booking updated."); }
    private void deleteBooking() { System.out.println(system.deleteBooking(readInt("Booking ID: "))?"Booking deleted.":"Booking not found."); }
    private void searchBookings() { String q=readText("Search ID/date/status: "); printBookings(system.searchBookings(q)); }
    private void sortUsers() { System.out.println("1 ID  2 Name  3 Email"); int c=readInt("Sort by: "); if(c==1)system.sortUsersById(); else if(c==2)system.sortUsersByName(); else system.sortUsersByEmail(); printUsers(system.getUsers()); }
    private void sortProperties() { System.out.println("1 Name  2 Price"); int c=readInt("Sort by: "); if(c==1)system.sortPropertiesByName(); else system.sortPropertiesByPrice(); printProperties(system.getProperties()); }
    private void sortBookings() { System.out.println("1 ID  2 Date"); int c=readInt("Sort by: "); if(c==1)system.sortBookingsById(); else system.sortBookingsByDate(); printBookings(system.getBookings()); }
    private void addFeedback() { int b=readInt("Booking ID: "); int r=readInt("Rating 1-5: "); String c=readText("Comment: "); System.out.println(system.evaluate(b,r,c)); }
    private void printUsers(List<Person> x) { x.forEach(System.out::println); }
    private void printProperties(List<Property> x) { x.forEach(System.out::println); }
    private void printBookings(List<Booking> x) { x.forEach(System.out::println); }
    private void printFeedback() { system.getFeedbackList().forEach(System.out::println); }
}

class StayNestGUI extends JFrame {
    private final StayNestSystem system;
    private final JTextArea output = new JTextArea();
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);
    private String activeTab = "Users";

    StayNestGUI(StayNestSystem system) {
        this.system = system;
        setTitle("StayNest - Accommodation Booking System"); setSize(1000, 650); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter(){ public void windowClosing(java.awt.event.WindowEvent e){ StayNestApp.saveAndExit(system); dispose(); }});
        buildUI(); refreshUsers();
    }
    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] tabs={"Users","Properties","Bookings","Feedback"};
        for(String t:tabs){ JButton b=new JButton(t); b.addActionListener(e->showData(t)); top.add(b); }
        JButton addUser=new JButton("Add User"), edit=new JButton("Update User"), del=new JButton("Delete User"), search=new JButton("Search"), sort=new JButton("Sort");
        JButton searchCurrent=new JButton("Search Current"), sortCurrent=new JButton("Sort Current");
        addUser.addActionListener(e->addUser()); edit.addActionListener(e->updateUser()); del.addActionListener(e->deleteUser()); search.addActionListener(e->search()); sort.addActionListener(e->sortUsers());
        top.add(addUser); top.add(edit); top.add(del); top.add(search); top.add(sort);
        searchCurrent.addActionListener(e->searchCurrent()); sortCurrent.addActionListener(e->sortCurrent());
        top.add(searchCurrent); top.add(sortCurrent);
        JButton addProperty=new JButton("Add Property"), delProperty=new JButton("Delete Property"), addBooking=new JButton("Add Booking"), updateBooking=new JButton("Update Booking"), delBooking=new JButton("Delete Booking"), feedback=new JButton("Add Feedback");
        addProperty.addActionListener(e->addProperty()); delProperty.addActionListener(e->deleteProperty()); addBooking.addActionListener(e->addBooking()); updateBooking.addActionListener(e->updateBooking()); delBooking.addActionListener(e->deleteBooking()); feedback.addActionListener(e->addFeedback());
        top.add(addProperty); top.add(delProperty); top.add(addBooking); top.add(updateBooking); top.add(delBooking); top.add(feedback);
        add(top,BorderLayout.NORTH);
        output.setEditable(false); output.setFont(new Font(Font.MONOSPACED,Font.PLAIN,14));
        JSplitPane split=new JSplitPane(JSplitPane.VERTICAL_SPLIT,new JScrollPane(list),new JScrollPane(output)); split.setDividerLocation(300); add(split,BorderLayout.CENTER);
        JLabel status=new JLabel("  GUI mode | Sorting: ID/Name/Email, Properties by Name/Price, Bookings by ID/Date | Searching: Linear + Binary"); add(status,BorderLayout.SOUTH);
    }
    private void showData(String tab){
        activeTab = tab;
        if(tab.equals("Users")){refreshUsers();} else if(tab.equals("Properties")){model.clear();system.getProperties().forEach(p->model.addElement(p.toString()));}
        else if(tab.equals("Bookings")){model.clear();system.getBookings().forEach(b->model.addElement(b.toString()));} else {model.clear();system.getFeedbackList().forEach(f->model.addElement(f.toString()));}
    }
    private void refreshUsers(){model.clear();system.getUsers().forEach(p->model.addElement(p.toString()));}
    private String input(String label){ return JOptionPane.showInputDialog(this,label); }
    private int intInput(String label){ while(true){String s=input(label); if(s==null)throw new IllegalArgumentException("Cancelled"); try{return Integer.parseInt(s.trim());}catch(Exception e){JOptionPane.showMessageDialog(this,"Enter a valid integer.","Input error",JOptionPane.ERROR_MESSAGE);}}}
    private void addUser(){try{int id=intInput("User ID:");String n=input("Name:");String em=input("Email:");String[] roles={"Guest","Host"};String r=(String)JOptionPane.showInputDialog(this,"Role:","Add User",JOptionPane.QUESTION_MESSAGE,null,roles,roles[0]);system.addUser(r.equals("Host")?new Host(id,n,em):new Guest(id,n,em));refreshUsers();msg("User added successfully.");}catch(Exception e){err(e);}}
    private void updateUser(){try{int id=intInput("User ID:");String n=input("New name:");String em=input("New email:");if(!system.updateUser(id,n,em))throw new IllegalArgumentException("User not found.");refreshUsers();msg("User updated successfully.");}catch(Exception e){err(e);}}
    private void deleteUser(){try{int id=intInput("User ID:");if(system.deleteUser(id))msg("User deleted.");else msg("User not found.");refreshUsers();}catch(Exception e){err(e);}}
    private void search(){try{String[] methods={"Linear search by ID","Binary search by ID","Search name"};String m=(String)JOptionPane.showInputDialog(this,"Search method:","Search",JOptionPane.QUESTION_MESSAGE,null,methods,methods[0]);if(m==null)return; if(m.startsWith("Linear")){Person p=system.findUserLinear(intInput("User ID:"));model.clear();if(p!=null)model.addElement(p.toString());else msg("User not found.");}else if(m.startsWith("Binary")){Person p=system.findUserBinary(intInput("User ID:"));model.clear();if(p!=null)model.addElement(p.toString());else msg("User not found.");}else{List<Person> r=system.searchUsersByName(input("Name contains:"));model.clear();r.forEach(p->model.addElement(p.toString()));}}catch(Exception e){err(e);}}
    private void searchCurrent(){try{String q=input("Search properties/bookings by name, location, ID, date or status:"); if(q==null)return; String tab=currentTab(); model.clear(); if(tab.equals("Properties"))system.searchProperties(q).forEach(p->model.addElement(p.toString())); else if(tab.equals("Bookings"))system.searchBookings(q).forEach(b->model.addElement(b.toString())); else {system.searchUsersByName(q).forEach(p->model.addElement(p.toString()));}}catch(Exception e){err(e);}}
    private String currentTab(){ return activeTab; }
    private void sortCurrent(){String tab=currentTab(); if(tab.equals("Properties")){String[] x={"Name","Price"};String c=(String)JOptionPane.showInputDialog(this,"Sort properties by:","Sorting",JOptionPane.QUESTION_MESSAGE,null,x,x[0]);if(c==null)return;if(c.equals("Name"))system.sortPropertiesByName();else system.sortPropertiesByPrice();showData("Properties");}else if(tab.equals("Bookings")){String[] x={"ID","Date"};String c=(String)JOptionPane.showInputDialog(this,"Sort bookings by:","Sorting",JOptionPane.QUESTION_MESSAGE,null,x,x[0]);if(c==null)return;if(c.equals("ID"))system.sortBookingsById();else system.sortBookingsByDate();showData("Bookings");}else sortUsers();}
    private void sortUsers(){String[] x={"ID","Name","Email"};String c=(String)JOptionPane.showInputDialog(this,"Sort users by:","Sorting",JOptionPane.QUESTION_MESSAGE,null,x,x[0]);if(c==null)return;if(c.equals("ID"))system.sortUsersById();else if(c.equals("Name"))system.sortUsersByName();else system.sortUsersByEmail();refreshUsers();}
    private void addProperty(){try{int id=intInput("Property ID:");String n=input("Property name:");String l=input("Location:");double p=Double.parseDouble(input("Price per night:"));int h=intInput("Host ID:");system.addProperty(new Property(id,n,l,p,h));showData("Properties");msg("Property added successfully.");}catch(Exception e){err(e);}}
    private void deleteProperty(){try{int id=intInput("Property ID:");msg(system.deleteProperty(id)?"Property deleted.":"Property not found.");showData("Properties");}catch(Exception e){err(e);}}
    private void addBooking(){try{int id=intInput("Booking ID:");int g=intInput("Guest ID:");int p=intInput("Property ID:");String d=input("Date (YYYY-MM-DD):");system.addBooking(new Booking(id,g,p,d,"Confirmed"));showData("Bookings");msg("Booking added successfully.");}catch(Exception e){err(e);}}
    private void updateBooking(){try{int id=intInput("Booking ID:");Booking b=system.findBooking(id);if(b==null)throw new IllegalArgumentException("Booking not found.");b.setDate(input("New date (YYYY-MM-DD):"));b.setStatus(input("New status (Confirmed/Cancelled/Completed):"));showData("Bookings");msg("Booking updated successfully.");}catch(Exception e){err(e);}}
    private void deleteBooking(){try{int id=intInput("Booking ID:");msg(system.deleteBooking(id)?"Booking deleted.":"Booking not found.");showData("Bookings");}catch(Exception e){err(e);}}
    private void addFeedback(){try{int b=intInput("Booking ID:");int r=intInput("Rating (1-5):");String c=input("Comment:");msg(system.evaluate(b,r,c));showData("Feedback");}catch(Exception e){err(e);}}
    private void msg(String s){JOptionPane.showMessageDialog(this,s,"StayNest",JOptionPane.INFORMATION_MESSAGE);}
    private void err(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"StayNest - Error",JOptionPane.ERROR_MESSAGE);}
}
