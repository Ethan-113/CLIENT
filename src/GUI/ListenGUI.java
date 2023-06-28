//监听是否有好友申请，固定时间（2s）检查一次，如果有则进行相关操作

package GUI;

import After.AddRequest;

import javax.swing.*;
import java.util.ArrayList;

public class ListenGUI extends Thread{
    private AddRequest addRequest;

    public ListenGUI(String name_self){
        addRequest = new AddRequest(name_self);
    }

    public void run() {
        while (true){
            //检查是否有好友申请
            ArrayList<String> list = addRequest.check();
            if(!list.isEmpty()){
                //弹出提醒框，选择是否同意
                int option = JOptionPane.showOptionDialog(null, print(list)+"申请成为好友，是否同意？", "好友请求",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (option == JOptionPane.YES_OPTION) {
                    for(String name : list){
                        addRequest.agree(name);
                    }
                } else {
                    for(String name : list){
                        addRequest.disagree(name);
                    }
                }
            }

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String print(ArrayList<String> list){
        String res = "";
        for(String name : list){
            res = res + name + ",";
        }
        return res;
    }
}
