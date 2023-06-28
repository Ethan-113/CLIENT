//好友申请相关

package After;

import java.sql.*;
import java.util.ArrayList;

public class AddRequest {
    private String name;

    public AddRequest(String name){
        this.name = name;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(new IPMysql().toSignal(),
                "root","admin");
    }

    //申请好友，传入对方的name
    public void request(String name_opposite){
        String sql_self = "insert into "+ this.name + " value ('wait',?,'#')";
        String sql_opposite = "insert into " + name_opposite + " value ('yes',?,'#')";

        try(Connection c = getConnection();
            PreparedStatement self = c.prepareStatement(sql_self);
            PreparedStatement opposite = c.prepareStatement(sql_opposite))
        {
            self.setString(1, name_opposite);
            opposite.setString(1, this.name);

            self.execute();
            opposite.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //检查当前账号的好友列表中是否有新的申请，如果有则返回一个arraylist，里面是申请人的name
    public ArrayList<String> check(){
        String name;
        ArrayList<String> request_list = new ArrayList<>();

        String sql_check = "select * from " + this.name + " where newornot = 'yes'";

        try(Connection c = getConnection();
            Statement s = c.createStatement();)
        {
            ResultSet rs = s.executeQuery(sql_check);
            while (rs.next()){
                name = rs.getString("name");
                request_list.add(name);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return request_list;
    }

    //同意某人的好友请求，就是把双方的newornot栏都改为no
    public void agree(String name_opposite){
        String sql_self = "update "+this.name+" set newornot = 'no' where name = '"+name_opposite+"'";
        String sql_opposite = "update "+name_opposite+" set newornot = 'no' where name = '"+this.name+"'";

        try(Connection c =getConnection();
            Statement self = c.createStatement();
            Statement opposite = c.createStatement())
        {
            self.execute(sql_self);
            opposite.execute(sql_opposite);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //拒绝某人的好友申请，就是把申请和等待分别从对方和自己的列表中删除
    public void disagree(String name_opposite){
        String sql_self = "delete from "+this.name+" where name = '"+name_opposite+"'";
        String sql_opposite = "delete from "+name_opposite+" where name = '"+this.name+"'";

        try(Connection c = getConnection();
            Statement self = c.createStatement();
            Statement opposite = c.createStatement())
        {
            self.execute(sql_self);
            opposite.execute(sql_opposite);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
