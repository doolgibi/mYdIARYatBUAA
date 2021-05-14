package com.wishwzp.web;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import com.wishwzp.dao.UserDao;
import com.wishwzp.model.User;
import com.wishwzp.util.DateUtil;
import com.wishwzp.util.DbUtil;
import com.wishwzp.util.PropertiesUtil;

/**
 * Servlet implementation class UserServlet
 */
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	DbUtil dbUtil=new DbUtil();
	UserDao userDao=new UserDao();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		String action=request.getParameter("action");
		if("preSave".equals(action)){
			userPreSave(request,response);
		}else if("save".equals(action)){
			userSave(request,response);
		}else if("logout".equals(action)){
			logout(request,response);
		}
		else if("register".equals(action)){
			register(request,response);
		}
		else if("userList".equals(action)){
			userList(request,response);
		}
		else if("deleteUser".equals(action)){
			deleteUser(request,response);
		}
		
			
	}
	
	private void deleteUser(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		String userId=request.getParameter("userId");
		Connection con=null;
		try{
			con=dbUtil.getCon();
			userDao.deleteUser(con, userId);
			
			request.getRequestDispatcher("user?action=userList").forward(request, response);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void userList(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		System.out.println("1111");
		Connection con =null;
		try {
			con=dbUtil.getCon();
			
			List<User> userList = userDao.userList(con);
			request.setAttribute("userList", userList);
			request.setAttribute("mainPage", "user/userList.jsp");
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void logout(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.invalidate();
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}
	
	private void register(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String username = request.getParameter("userName");
		String password = request.getParameter("password");
		String nickName = request.getParameter("nickName");
	
		Connection con =null;
		try {
			con=dbUtil.getCon();
			User user = new User(username,password,nickName);
			
			int i=userDao.register(con, user);
			if (i > 0) {
				request.setAttribute("user", user);
				request.setAttribute("error", "请登录！");
				request.getRequestDispatcher("login.jsp").forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void userPreSave(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		request.setAttribute("mainPage", "user/userSave.jsp");
		request.getRequestDispatcher("mainTemp.jsp").forward(request, response);		
	}
	
	private void userSave(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		
		/** 
         * 首先判断form的enctype是不是multipart/form-data 
         * 同时也判断了form的提交方式是不是post 
         * 方法：isMultipartContent(request) 
         */  
		if(ServletFileUpload.isMultipartContent(request)){  
			 System.out.println("yes");
		}
		// 实例化一个硬盘文件工厂,用来配置上传组件ServletFileUpload
		FileItemFactory factory=new DiskFileItemFactory();
		//创建ServletFileUpload对象
		ServletFileUpload upload=new ServletFileUpload(factory);
		
		List<FileItem> items=null;
		try {
			items=upload.parseRequest(new ServletRequestContext(request));
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//取得items的迭代器  
		Iterator<FileItem> itr=items==null?null:items.iterator();
		
		HttpSession session=request.getSession();
		
		User user=(User)session.getAttribute("currentUser");
		boolean imageChange=false;
		//迭代items
		while(itr.hasNext()){
			FileItem item=(FileItem)itr.next();
			 //如果传过来的是普通的表单域  
			if(item.isFormField()){
				String fieldName=item.getFieldName();
				if("nickName".equals(fieldName)){
					user.setNickName(item.getString("utf-8"));
				}
				if("mood".equals(fieldName)){
					user.setMood(item.getString("utf-8"));
				}
			}else if(!"".equals(item.getName())){
				try{
					imageChange=true;
					String imageName=DateUtil.getCurrentDateStr();
					user.setImageName(imageName+"."+item.getName().split("\\.")[1]);
					String filePath=PropertiesUtil.getValue("imagePath")+imageName+"."+item.getName().split("\\.")[1];
					item.write(new File(filePath));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		if(!imageChange){
			user.setImageName(user.getImageName().replaceFirst(PropertiesUtil.getValue("imageFile"), ""));
		}
		
		Connection con=null;
		try {
			con=dbUtil.getCon();
			int saveNums=userDao.userUpdate(con, user);
			if(saveNums>0){
				user.setImageName(PropertiesUtil.getValue("imageFile")+user.getImageName());
				session.setAttribute("currentUser", user);
				request.getRequestDispatcher("main?all=true").forward(request, response);
			}else{
				request.setAttribute("currentUser", user);
				request.setAttribute("error", "保存失败！");
				request.setAttribute("mainPage", "user/userSave.jsp");
				request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
