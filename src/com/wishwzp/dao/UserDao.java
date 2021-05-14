package com.wishwzp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.wishwzp.model.DiaryType;
import com.wishwzp.model.User;
import com.wishwzp.util.PropertiesUtil;

public class UserDao {

	/**
	 * 登录验证
	 * @param con
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public User login(Connection con,User user)throws Exception{
		User resultUser=null;
		String sql="select * from t_user where userName=? and password=? and is_manage=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassword());
		pstmt.setInt(3, user.getIs_manage());
		ResultSet rs=pstmt.executeQuery();
		if(rs.next()){
			resultUser=new User();
			resultUser.setUserId(rs.getInt("userId"));
			resultUser.setUserName(rs.getString("userName"));
			resultUser.setPassword(rs.getString("password"));
			resultUser.setNickName(rs.getString("nickName"));
			resultUser.setImageName(PropertiesUtil.getValue("imageFile")+rs.getString("imageName"));
			resultUser.setMood(rs.getString("mood"));
			resultUser.setIs_manage(rs.getInt("is_manage"));
		}
		return resultUser;
	}
	
	/**
	 * 个人资料修改
	 * @param con
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public int userUpdate(Connection con,User user)throws Exception{
		String sql="update t_user set nickName=?,imageName=?,mood=? where userId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getNickName());
		pstmt.setString(2, user.getImageName());
		pstmt.setString(3, user.getMood());
		pstmt.setInt(4, user.getUserId());
		return pstmt.executeUpdate();
	}

	public int register(Connection con, User user)throws Exception {
		String sql="insert into t_user values(null,?,?,?,?,null,0)";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassword());
		pstmt.setString(3, user.getNickName());
		pstmt.setString(4, "20210430111416.jpg");
		return pstmt.executeUpdate();
	}

	public List<User> userList(Connection con) throws SQLException {
		List<User> users = new ArrayList<User>();
		String sql="select * from t_user where is_manage=0";
		PreparedStatement pstmt=con.prepareStatement(sql);
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			User resultUser=new User();
			resultUser.setUserId(rs.getInt("userId"));
			resultUser.setUserName(rs.getString("userName"));
			resultUser.setPassword(rs.getString("password"));
			resultUser.setNickName(rs.getString("nickName"));
			resultUser.setImageName(PropertiesUtil.getValue("imageFile")+rs.getString("imageName"));
			resultUser.setMood(rs.getString("mood"));
			resultUser.setIs_manage(rs.getInt("is_manage"));
			users.add(resultUser);
		}
		return users;
	}

	public int deleteUser(Connection con, String userId) throws SQLException {
		// TODO Auto-generated method stub
		String sql="delete from t_user where userId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, userId);
		return pstmt.executeUpdate();
	}
}
