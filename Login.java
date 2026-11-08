import javax.swing.JOptionPane;

public class Login {
    public static void main(String[] args) {

        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");

        if ("Hero".equals(username) && "Zero".equals(password)) {
            JOptionPane.showMessageDialog(null, "Access Granted");
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect Credentials");
        }
    }
}