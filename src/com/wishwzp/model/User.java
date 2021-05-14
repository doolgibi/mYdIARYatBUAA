package com.wishwzp.model;

/**
 * 用户登录实体类
 * @author wzp
 *
 */
public class User {

	private int userId;//编号
	private String userName;//用户名
	private String password;//密码
	private String nickName;//昵称
	private String imageName;//头像图片
	private String mood;//心情
	private int is_manage;//是否管理员；1：是；2：不是

	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	
	
	public User(String userName, String password, int is_manage) {
		super();
		this.userName = userName;
		this.password = password;
		this.is_manage = is_manage;
	}
	
	public User(String userName, String password, String nickName) {
		super();
		this.userName = userName;
		this.password = password;
		this.nickName = nickName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public int getIs_manage() {
		return is_manage;
	}
	public void setIs_manage(int is_manage) {
		this.is_manage = is_manage;
	}
	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", password=" + password + ", nickName=" + nickName
				+ ", imageName=" + imageName + ", mood=" + mood + ", is_manage=" + is_manage + "]";
	}
	
	
}
