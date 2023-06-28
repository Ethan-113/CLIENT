//主窗口可视化，主要是登录后的界面和聊天界面

package GUI;

import After.Client;
import After.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatProgramGUI {
    public JFrame frame;
    private JPanel friendPanel;
    private JPanel chatPanel;
    private ArrayList<String> list_friends;
    private ArrayList<String> list_groups;
    private ArrayList<String> currentFriendList;
    private JList<String> friendList;
    private ArrayList<String> if_close = new ArrayList<>();
    private Map<String, JTextArea> chatArea = new HashMap<>();
    private JButton toggleButton;
    private JButton flashButton;
    private JButton addButton;
    private String name_self;
    private Client client;
    public Login login;
    private ListenGUI listen;

    public ChatProgramGUI(Login login) throws SQLException, IOException {
        this.login = login;
        this.client = new Client(login.name);
        this.listen = new ListenGUI(login.name);
        this.name_self = login.name;
        this.list_friends = login.friends_mine();
        this.list_groups = login.groups_mine();
        reloadArea(this.list_friends, this.list_groups);

        //接收消息的线程
        client.receive(this);
        listen.start();

        //调用主方法
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("当前账号:"+name_self);
        frame.setBounds(100, 100, 300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setResizable(false);

        friendPanel = new JPanel();
        friendPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(friendPanel, BorderLayout.WEST);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(chatPanel, BorderLayout.CENTER);

        currentFriendList = list_friends;

        friendList = new JList<>(currentFriendList.toArray(new String[0]));
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setVisibleRowCount(10);

        //修改好友表的宽度
        Dimension friendListSize = new Dimension(145, 100);
        friendList.setPreferredSize(friendListSize);

        friendList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedFriend = friendList.getSelectedValue();
                if (selectedFriend != null) {
                    openChatWindow(selectedFriend);
                }
            }
        });

        JScrollPane friendScrollPane = new JScrollPane(friendList);
        friendPanel.add(friendScrollPane, BorderLayout.CENTER);

        //切换列表按钮
        toggleButton = new JButton("切换列表");
        toggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleFriends();
            }
        });
        friendPanel.add(toggleButton, BorderLayout.SOUTH);

        //刷新按钮
        flashButton = new JButton("刷新");
        flashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 刷新好友列表和群组列表
                Login login_now = new Login(login.name, login.password);

                list_friends = login_now.friends_mine();
                list_groups = login_now.groups_mine();

                // 更新当前显示的列表
                if (currentFriendList == list_friends) {
                    currentFriendList = list_friends;
                } else {
                    currentFriendList = list_groups;
                }
                friendList.setListData(currentFriendList.toArray(new String[0]));
                reloadArea(list_friends, list_groups);
            }
        });
        friendPanel.add(flashButton, BorderLayout.NORTH);

        //列表操作相关按钮
        addButton = new JButton("添加好友或操作群聊");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //操作群聊和好友模块
                SwitchListGUI switchListGUI = new SwitchListGUI(login);
            }
        });
        friendPanel.add(addButton, BorderLayout.EAST);
    }

    //改变显示的列表，如果当前显示私聊则改成显示群聊，反之亦然
    private void toggleFriends() {
        if (currentFriendList == list_friends) {
            currentFriendList = list_groups;
        } else {
            currentFriendList = list_friends;
        }

        friendList.setListData(currentFriendList.toArray(new String[0]));
    }

    //聊天窗口，传入的是对方的名字
    private void openChatWindow(String friend) {
        //在if_close添加目标的名称，说明该JTextArea（聊天窗口）已经打开
        if_close.add(friend);

        JFrame chatFrame = new JFrame("对方 " + friend + "（群聊时输入*****获取七天的聊天记录）");
        chatFrame.setBounds(100, 100, 700, 500);

        JTextArea chatTextArea = chatArea.get(friend);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        chatFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        //点击回车，进行消息发送
        JTextField messageTextField = new JTextField();
        chatFrame.getContentPane().add(messageTextField, BorderLayout.SOUTH);
        messageTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageTextField.getText();
                snedMessage(chatTextArea, message);
                client.send_one(friend, message);
                messageTextField.setText("");
            }
        });

        //点击发送按钮，进行消息发送
        JButton sendButton = new JButton("发送");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageTextField.getText();
                snedMessage(chatTextArea, message);
                client.send_one(friend, message);
                messageTextField.setText("");
            }
        });
        chatFrame.getContentPane().add(sendButton, BorderLayout.EAST);

        //如果窗口关闭，就将目标移出if_close，说明该聊天窗口不再打开
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if_close.remove(friend);
            }
        });

        chatFrame.setVisible(true);
    }

    //给对方发送信息
    private void snedMessage(JTextArea chatTextArea, String message) {
        chatTextArea.append("[我]: " + message + "\n");
    }

    //接收到信息，分配给目标好友的JTextArea中，如果该聊天窗口没打开，则执行打开操作
    public void receiveMessage(String friend, String message){
        if(!if_close.contains(friend)) {
            //打开窗口
            openChatWindow(friend);
        }
        JTextArea chatTextArea = chatArea.get(friend);
        chatTextArea.append("[" + friend + "]: " + message + "\n");
    }

    //给每个好友分配一个独立的JTextArea
    private void reloadArea(ArrayList<String> list_friends, ArrayList<String> list_groups){
        chatArea.clear();

        for(String name : list_friends){
            JTextArea chatTextArea = new JTextArea();
            chatArea.put(name, chatTextArea);
        }

        for(String name : list_groups){
            JTextArea chatTextArea = new JTextArea();
            chatArea.put(name, chatTextArea);
        }

        JTextArea chatTextArea = new JTextArea();
        chatArea.put("系统消息", chatTextArea);
    }
}
