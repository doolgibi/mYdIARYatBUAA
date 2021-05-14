package com.wishwzp.web;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wishwzp.dao.DiaryDao;
import com.wishwzp.model.Diary;
import com.wishwzp.model.User;
import com.wishwzp.util.DateUtil;
import com.wishwzp.util.DbUtil;
import com.wishwzp.util.StringUtil;

/**
 * Servlet implementation class DiaryServlet
 */
public class DiaryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	DbUtil dbUtil=new DbUtil();
	DiaryDao diaryDao=new DiaryDao();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DiaryServlet() {
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
		if("show".equals(action)){
			diaryShow(request,response);
		}else if("preSave".equals(action)){
			diaryPreSave(request,response);
		}else if("save".equals(action)){
			try {
				diarySave(request,response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if("delete".equals(action)){
			diaryDelete(request,response);
		}
	}
	

	private void diaryShow(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		String diaryId=request.getParameter("diaryId");
		Connection con=null;
		try{
			con=dbUtil.getCon();
			Diary diary=diaryDao.diaryShow(con, diaryId);
			request.setAttribute("diary", diary);
			request.setAttribute("mainPage", "diary/diaryShow.jsp");
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
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
	
	private void diaryPreSave(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		String diaryId=request.getParameter("diaryId");
		Connection con=null;
		try {
			if(StringUtil.isNotEmpty(diaryId)){
				con=dbUtil.getCon();
				Diary diary=diaryDao.diaryShow(con, diaryId);
				request.setAttribute("diary", diary);
			}
			request.setAttribute("mainPage", "diary/diarySave.jsp");
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void diarySave(HttpServletRequest request, HttpServletResponse response)throws Exception {
		String title=request.getParameter("title");
		String releaseDateString=request.getParameter("releaseDate");
		String content=request.getParameter("content");
		String typeId=request.getParameter("typeId");
		String diaryId=request.getParameter("diaryId");
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");
		System.out.println(releaseDateString+"111");
		/*Date releaseDate = DateUtil.formatString(releaseDateString, "yyyy-MM-dd HH:mm:ss");*/
		
		Diary diary=new Diary(title,content,Integer.parseInt(typeId),releaseDateString,user.getUserId());
		if(StringUtil.isNotEmpty(diaryId)){
			diary.setDiaryId(Integer.parseInt(diaryId));
		}
		Connection con=null;
		try {
			con=dbUtil.getCon();
			int saveNums;
			
			if(StringUtil.isNotEmpty(diaryId)){
				saveNums=diaryDao.diaryUpdate(con, diary);	
			}else{
				saveNums=diaryDao.diaryAdd(con, diary);				
			}
			
			if(saveNums>0){
				request.getRequestDispatcher("main?all=true").forward(request, response);
			}else{
				request.setAttribute("diary", diary);
				request.setAttribute("error", "保存失败");
				request.setAttribute("mainPage", "diary/diarySave.jsp");
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
	
	private void diaryDelete(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		String diaryId=request.getParameter("diaryId");
		Connection con=null;
		try{
			con=dbUtil.getCon();
			diaryDao.diaryDelete(con, diaryId);
			request.getRequestDispatcher("main?all=true").forward(request, response);
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

}
