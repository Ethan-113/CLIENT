//列表操作可视化，发送好友申请，创建群聊，退出群聊，邀请加入群聊

package GUI;

import After.AddRequest;
import After.Group;
import After.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class SwitchListGUI extends JFrame {
    private ArrayList<String> list_request;
    private ArrayList<String> list_group;

    private ArrayList<String> currentList;
    private JList<String> list;
    private JButton switchButton;
    private JButton createGroupButton;
    private String name_self;
    private Login login;

    public SwitchListGUI(Login login) {
        this.login = login;
        this.name_self = login.name;
        //list_request是未添加的账号的名称
        this.list_request = login.friend_other();
        //加入的群聊
        this.list_group = login.groups_mine();
        this.currentList = list_request;

        initialize();
    }

    private void initialize() {
        setResizable(false);
        setSize(350, 450);
        setTitle("添加好友或操作群聊");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        //给JList赋值为未成为好友的账户
        list = new JList<>(currentList.toArray(new String[0]));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(5);
        JScrollPane scrollPane = new JScrollPane(list);

        panel.add(scrollPane, BorderLayout.CENTER);

        //初始显示
        switchButton = new JButton(getSwitchButtonText());
        //切换显示
        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchList();
                switchButton.setText(getSwitchButtonText());
            }
        });

        panel.add(switchButton, BorderLayout.SOUTH);

        //创建群聊操作
        createGroupButton = new JButton("创建群聊");
        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    createGroup();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        panel.add(createGroupButton, BorderLayout.NORTH);

        //列出所有相关好友和群聊，并且每次点击名称时进行相关操作
        //设计有点问题，逻辑复杂
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedItem = list.getSelectedValue();
                    if (currentList == list_request) {
                        friendMethod(selectedItem);
                    } else if (currentList == list_group) {
                        try {
                            groupMethod(selectedItem);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    //给对方发出好友邀请
    private void friendMethod(String selectedItem) {
        int result = JOptionPane.showConfirmDialog(this, "是否发出好友申请", "提示", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            AddRequest addRequest = new AddRequest(this.name_self);
            addRequest.request(selectedItem);
            JOptionPane.showMessageDialog(this, "已经发出申请");
        }
    }

    //操作目标群聊
    private void groupMethod(String selectedItem) throws SQLException {
        String[] options = {"退出群聊", "邀请好友", "取消"};
        int choice = JOptionPane.showOptionDialog(this, "请选择操作：", "提示", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            Group group = new Group(selectedItem);
            group.del_out(this.name_self);
            JOptionPane.showMessageDialog(this, "退出群聊成功");
        } else if (choice == 1) {
            String selectedMember = showMemberSelectionDialog();
            Group group = new Group(selectedItem);
            boolean success = group.add_in(selectedMember);
            if(success) {
                JOptionPane.showMessageDialog(this, "邀请好友成功");
            }else {
                JOptionPane.showMessageDialog(this, "邀请好友失败，群聊人数已满或好友已存在该群");
            }
        }
    }

    //切换当前显示的表
    private void switchList() {
        if (currentList == list_request) {
            currentList = list_group;
        } else {
            currentList = list_request;
        }
        list.setListData(currentList.toArray(new String[0]));
    }

    //切换按钮的名称
    private String getSwitchButtonText() {
        if (currentList == list_request) {
            return "当前为未添加好友的账号，点击显示加入的群聊";
        } else {
            return "当前为加入的群聊，点击显示未添加好友的账号";
        }
    }

    //创建群聊的窗口
    private void createGroup() throws SQLException {
        String groupName = JOptionPane.showInputDialog(this, "请输入群聊名称:");
        if (groupName != null && !groupName.isEmpty()) {
            String selectedMember = showMemberSelectionDialog();
            if (selectedMember != null) {
                // 执行创建群聊的操作
                Group group = new Group(groupName);
                int rank = rank_choice();
                group.create_group(this.name_self, selectedMember, rank);
                JOptionPane.showMessageDialog(this, "成功创建群聊 '" + groupName + "'，成员: " + selectedMember);
            } else {
                JOptionPane.showMessageDialog(this, "请选择一个组员");
            }
        } else {
            JOptionPane.showMessageDialog(this, "请输入有效的群聊名称");
        }
    }

    //设置群聊的等级
    private int rank_choice(){
        int rank = 1;
        String[] options = {"一级（10人）", "二级（20人）", "三级（30人）"};
        int choice = JOptionPane.showOptionDialog(this, "请选择群聊等级（默认为一级）：", "等级选择", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            rank = 1;
        } else if (choice == 1) {
            rank = 2;
        }else if (choice == 2){
            rank = 3;
        }

        return rank;
    }

    //选择成员界面，实现点击选择
    private String showMemberSelectionDialog() {
        JList<String> memberList = new JList<>(login.friends_mine().toArray(new String[0]));
        memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(memberList);

        int option = JOptionPane.showConfirmDialog(this, scrollPane, "选择群聊成员", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            return memberList.getSelectedValue();
        } else {
            return null;
        }
    }
}
