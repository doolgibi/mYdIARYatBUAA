package com.wishwzp.web;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wishwzp.dao.DiaryDao;
import com.wishwzp.dao.DiaryTypeDao;
import com.wishwzp.model.Diary;
import com.wishwzp.model.DiaryType;
import com.wishwzp.model.PageBean;
import com.wishwzp.model.User;
import com.wishwzp.util.DbUtil;
import com.wishwzp.util.PropertiesUtil;
import com.wishwzp.util.StringUtil;

/**
 * Servlet implementation class MainServlet
 */
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	DbUtil dbUtil=new DbUtil();
	DiaryDao diaryDao=new DiaryDao();
	DiaryTypeDao diaryTypeDao = new DiaryTypeDao();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
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
		
		HttpSession session = request.getSession();
		String s_typeId=request.getParameter("s_typeId");
		String s_releaseDateStr=request.getParameter("s_releaseDateStr");
		String s_title=request.getParameter("s_title");
		String all=request.getParameter("all");
		
		String page=request.getParameter("page");
		Diary diary=new Diary();
		User user = (User) session.getAttribute("currentUser");
		if("true".equals(all)){
			if(StringUtil.isNotEmpty(s_title)){
				diary.setTitle(s_title);
			}
			session.removeAttribute("s_releaseDateStr");
			session.removeAttribute("s_typeId");
			session.setAttribute("s_title", s_title);
			
			System.out.println("111"+user.toString());
		}else{
			if(user.getIs_manage() == 0){
				diary.setUserId(user.getUserId());
			}
			if(StringUtil.isNotEmpty(s_typeId)){
				diary.setTypeId(Integer.parseInt(s_typeId));
				session.setAttribute("s_typeId", s_typeId);
				session.removeAttribute("s_releaseDateStr");
				session.removeAttribute("s_title");
			}
			if(StringUtil.isNotEmpty(s_releaseDateStr)){
				s_releaseDateStr=new String(s_releaseDateStr.getBytes("ISO-8859-1"),"UTF-8");
				diary.setReleaseDateStr(s_releaseDateStr);
				session.setAttribute("s_releaseDateStr", s_releaseDateStr);
				session.removeAttribute("s_typeId");
				session.removeAttribute("s_title");
			}
			if(StringUtil.isEmpty(s_typeId)){
				Object o=session.getAttribute("s_typeId");
				if(o!=null){
					diary.setTypeId(Integer.parseInt((String)o));
				}
			}
			if(StringUtil.isEmpty(s_releaseDateStr)){
				Object o=session.getAttribute("s_releaseDateStr");
				if(o!=null){
					diary.setReleaseDateStr((String)o);
				}
			}
			if(StringUtil.isEmpty(s_title)){
				Object o=session.getAttribute("s_title");
				if(o!=null){
					diary.setTitle((String)o);
				}
			}
		}
		
		if(StringUtil.isEmpty(page)){
			page="1";
		}
		Connection con=null;
		//初始化为1，4
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(PropertiesUtil.getValue("pageSize")));
		
		try {
			con=dbUtil.getCon();
			List<Diary> diaryList=diaryDao.diaryList(con,pageBean,diary);
			int total=diaryDao.diaryCount(con,diary);
			String pageCode=this.genPagation(total, Integer.parseInt(page), Integer.parseInt(PropertiesUtil.getValue("pageSize")));
			request.setAttribute("pageCode", pageCode);
			request.setAttribute("diaryList", diaryList);
			
			//按日志类别显示
			List<DiaryType> diaryTypeCountList = diaryTypeDao.diaryTypeCountList(con);
			//按日志日期显示
			List<Diary> diaryCountList = diaryDao.diaryCountList(con);
			session.setAttribute("diaryTypeCountList", diaryTypeCountList);
			session.setAttribute("diaryCountList", diaryCountList);
			
			request.setAttribute("mainPage", "diary/diaryList.jsp");
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 分页显示
	 * @param totalNum
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	private String genPagation(int totalNum,int currentPage,int pageSize){
		int totalPage=totalNum%pageSize==0?totalNum/pageSize:totalNum/pageSize+1;//7,totalPage表示一共有多少页
		StringBuffer pageCode=new StringBuffer();
		pageCode.append("<li><a href='main?page=1'>首页</a></li>");
		if(currentPage==1){
			pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
		}else{
			pageCode.append("<li><a href='main?page="+(currentPage-1)+"'>上一页</a></li>");
		}
		
		for(int i=currentPage-2;i<=currentPage+2;i++){
			if(i<1||i>totalPage){
				continue;
			}
			if(i==currentPage){
				pageCode.append("<li class='active'><a href='#'>"+i+"</a></li>");
			}else{
				pageCode.append("<li><a href='main?page="+i+"'>"+i+"</a></li>");
			}
		}
		
		if(currentPage==totalPage){
			pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
		}else{
			pageCode.append("<li><a href='main?page="+(currentPage+1)+"'>下一页</a></li>");
		}
		pageCode.append("<li><a href='main?page="+totalPage+"'>尾页</a></li>");
		return pageCode.toString();
	}

}
