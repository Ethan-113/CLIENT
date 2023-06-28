package After;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Token {
    /*
    返回的是存储在数据库中所有账号的包含faceToken的链表
     */
    public static HashMap<String, ArrayList<String>> allToken(){
        HashMap<String, ArrayList<String>> tokens = new HashMap<>();
        String faceToken;
        String name;
        String password;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        String sql = "select * from users_list";

        try (Connection c = DriverManager.getConnection(new IPMysql().toSignal(), "root","admin");
             Statement s = c.createStatement()){
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()){
                ArrayList<String> information = new ArrayList<>();
                faceToken = rs.getString("faceToken");
                name = rs.getString("name");
                password = rs.getString("password");
                information.add(name);
                information.add(password);
                tokens.put(faceToken, information);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tokens;
    }
}
