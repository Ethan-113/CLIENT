//登录窗口可视化

package GUI;

import After.AccountCreate;
import After.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        setTitle("登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(300, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JLabel usernameLabel = new JLabel("账号：");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("密码：");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                boolean isAuthenticated = authenticateUser(username, password);

                if (isAuthenticated) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "登录成功！");

                    // 执行登录成功后的操作
                    Login login = new Login(username, password);

                    ChatProgramGUI window = null;
                    try {
                        window = new ChatProgramGUI(login);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    window.frame.setVisible(true);

                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginGUI.this, "登录失败，请检查账号和密码！");
                }

                // 清除密码字段
                passwordField.setText("");
            }
        });

        //注册按钮
        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 执行注册操作
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                AccountCreate accountCreate = new AccountCreate(username, password);
                boolean success = accountCreate.createSignal();
                if(success) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "注册成功");
                }else {
                    JOptionPane.showMessageDialog(LoginGUI.this, "注册失败，或是账号已经存在");
                }
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    //检查该账号与密码是否匹配
    private boolean authenticateUser(String username, String password) {
        Login login = new Login(username, password);
        return login.login();
    }

    // 公共方法，用于在其他地方调用创建登录界面
    public static void createLoginGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginGUI();
            }
        });
    }
}
