//与登录相关的函数

package After;

import After.IPMysql;

import java.sql.*;
import java.util.ArrayList;

public class Login {
    public String name;
    public String password;

    public Login(String name, String password){
        this.name = name;
        this.password = password;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    //个人数据库的连接
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(new IPMysql().toSignal(),
                "root","admin");
    }

    //群聊数据库的连接
    private Connection getConnection_group() throws SQLException {
        return DriverManager.getConnection(new IPMysql().toGroup(),
                "root","admin");
    }

    //查询当前账户是否存在
    public boolean login(){
        String sql = "select * from users_list where name = '"+this.name+"' and password = '"+this.password+"'";
        try (Connection c = getConnection();
             Statement s = c.createStatement())
        {
            ResultSet rs = s.executeQuery(sql);
            //如果存在则返回true
            if(rs.next())
                return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        //如果账号与密码不匹配则返回false
        return false;
    }

    //打印当前用户已经添加的好友列表
    public ArrayList<String> friends_mine(){
        ArrayList<String> list = new ArrayList<>();
        String sql = "select * from "+this.name;

        try(Connection c = getConnection();
        Statement s = c.createStatement())
        {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()){
                list.add(rs.getString("name"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return list;
    }

    //打印当前用户还没添加的其他用户
    public ArrayList<String> friend_other(){
        ArrayList<String> have_list = friends_mine();
        //打印没有添加的好友时要排除自己
        have_list.add(this.name);
        ArrayList<String> other_list = new ArrayList<>();
        String sql="select * from users_list";

        try (Connection c = getConnection();
        Statement s = c.createStatement())
        {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()){

                if(!have_list.contains(rs.getString("name")))
                    other_list.add(rs.getString("name"));

            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return other_list;
    }

    //返回当前用户所加的所有群
    public ArrayList<String> groups_mine(){
        ArrayList<String> list = new ArrayList<>();
        String sql = "select * from "+this.name;

        try(Connection c = getConnection_group();
            Statement s = c.createStatement())
        {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()){
                list.add(rs.getString("name_group"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
