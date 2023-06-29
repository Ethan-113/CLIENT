package After;


import After.Login;
import After.Token;
import Face.FaceUtil;
import GUI.ChatProgramGUI;
import GUI.LoginGUI;
import Photo.WebcamViewer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Show {
    public static void main(String[] args) throws Exception {
        boolean flag = false;
        HashMap<String, ArrayList<String>> tokens = Token.allToken();
        ArrayList<String> information = new ArrayList<>();

        WebcamViewer.photo();
        String faceToken;
        File file = new File("pic\\zy.jpg");
        faceToken = FaceUtil.detect(file);

        if (faceToken != null) {
            for (String Token : tokens.keySet()) {
                boolean res = FaceUtil.compareFace(Token, faceToken);
                if (res) {
                    //找到该人
                    flag = true;
                    information = tokens.get(Token);
                    break;
                }
            }

            //没找到用户，注册或者账号密码登录
            if (!flag) {
                LoginGUI.createLoginGUI();
            }

            //找到用户，直接登录
            else {
                Login login = new Login(information.get(0), information.get(1));
                ChatProgramGUI window = null;
                try {
                    window = new ChatProgramGUI(login);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                window.frame.setVisible(true);
            }
        }
        else {
            LoginGUI.createLoginGUI();
        }
    }
}
