package com.sd.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.sd.service.MovService;
import com.sd.vo.Tresvid;
@Controller
@RequestMapping("/video")
public class MovieAction {
	@Resource 
	private MovService movService;
	//加载
	private String gid;
	//上传
	private File movData;
	private String movType;
	
	
	@RequestMapping("/upload.do")
	public void execute(HttpServletRequest request,HttpServletResponse response) throws Exception {  
		PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");  
        String uuid = UUID.randomUUID().toString();
        FileItemFactory factory = new DiskFileItemFactory();  
        ServletFileUpload upload = new ServletFileUpload(factory);  
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile movData = multipartRequest.getFile("movData");
        movType=multipartRequest.getParameter("movType");
        String str=new File(request.getSession().getServletContext().getRealPath("")).getParent();
		String path=str+"/movie/";
		Date date=new Date(); 
		SimpleDateFormat stingDateFormat  = new SimpleDateFormat("yyyy/MM/dd/");  
		path+= stingDateFormat.format(date);
		File fileM= new File(path);
		// 如果指定的路径没有就创建    
		if (!fileM.exists()) {    
			fileM.mkdirs();    
		} 
		
		FileOutputStream fos = new FileOutputStream(path+uuid+"."+movType);
		InputStream fis = movData.getInputStream(); 
		byte[] buf = new byte[1024];  
		int len = 0;  
		while ((len = fis.read(buf)) > 0) {  
		    fos.write(buf, 0, len);  
		}  
		if (fis != null)  
		    fis.close();  
		if (fos != null)  
		    fos.close();
		float mid;
		if(movData.getSize()/1024>500){
			mid=1/(float)(movData.getSize()/1024/500);
		}else{
			mid=1;
		}
		JSONObject json = new JSONObject();
		if("mp4".equals(movType)){
			this.take(path+uuid+"."+movType, path+uuid+".jpg");
			json.put("pic","movie/"+stingDateFormat.format(date)+uuid+".jpg");
		}
		
		Tresvid tresvid=new Tresvid();
		tresvid.setRvGid(uuid);
		tresvid.setRvUrl("movie/"+stingDateFormat.format(date)+uuid+"."+movType);
		movService.save(tresvid);
		json.put("url","movie/"+stingDateFormat.format(date)+uuid+"."+movType);
        out.print(json.toString());  
        out.close();  
    } 
	@RequestMapping("/download.do")
	public void download(String path,HttpServletRequest request, HttpServletResponse response) {
        try {
        	String str=new File(request.getSession().getServletContext().getRealPath("")).getParent();
            // path是指欲下载的文件的路径。
        	path=str+'/'+path;
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	public void save(){
		String uuid = UUID.randomUUID().toString();
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			String str=new File(request.getSession().getServletContext().getRealPath("")).getParent();
			String path=str+"/movie/";
			Date date=new Date(); 
			SimpleDateFormat stingDateFormat  = new SimpleDateFormat("yyyy/MM/dd/");  
			path+= stingDateFormat.format(date);
			File fileM= new File(path);
			// 如果指定的路径没有就创建    
			if (!fileM.exists()) {    
				fileM.mkdirs();    
			} 
			
			FileOutputStream fos = new FileOutputStream(path+uuid+"."+movType);
			FileInputStream fis = new FileInputStream(movData);  
			byte[] buf = new byte[1024];  
			int len = 0;  
			while ((len = fis.read(buf)) > 0) {  
			    fos.write(buf, 0, len);  
			}  
			if (fis != null)  
			    fis.close();  
			if (fos != null)  
			    fos.close();
			float mid;
			if(movData.length()/1024>500){
				mid=1/(float)(movData.length()/1024/500);
			}else{
				mid=1;
			}
			Tresvid tresvid=new Tresvid();
			tresvid.setRvGid(uuid);
			tresvid.setRvUrl("movie\\"+stingDateFormat.format(date)+uuid+"."+movType);
			movService.save(tresvid);
			JSONObject json = new JSONObject();
			json.put("code", "1");
			json.put("message", "上传成功");
			json.put("url","movie/"+stingDateFormat.format(date)+uuid+"."+movType);
			if("mp4".equals(movType)){
				this.take(path+uuid+"."+movType, path+uuid+".jpg");
				json.put("pic","movie/"+stingDateFormat.format(date)+uuid+".jpg");
			}
			json.put("gid",uuid);
			HttpServletResponse rep = ServletActionContext.getResponse();
			rep.setCharacterEncoding("UTF-8");
			rep.setContentType("text/plain");
			rep.getWriter().write(json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public void load(){
		try {
			String s="";
			Tresvid tresvid=movService.get(gid);
			HttpServletRequest request = ServletActionContext.getRequest();
			String str=new File(request.getSession().getServletContext().getRealPath("")).getParent();
			s=str+"\\"+tresvid.getRvUrl();
			// 以byte流的方式打开文件 d:\1.gif
			FileInputStream hFile = new FileInputStream(s); 
			//得到文件大小   
			int i=hFile.available(); 
			byte data[]=new byte[i]; 
			//读数据   
			hFile.read(data);  
			
			//得到向客户端输出二进制数据的对象
			HttpServletResponse response=ServletActionContext.getResponse();
			OutputStream toClient=response.getOutputStream();  
			//输出数据   
			toClient.write(data);  
			
			toClient.flush();  
			toClient .close();   
			hFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping("/firstPic.do")
	public boolean take(String videoLocation, String imageLocation)
	{
		// 低精度
		List commend = new java.util.ArrayList();
		commend.add("/usr/local/ffmpeg/bin/ffmpeg");//视频提取工具的位置
		commend.add("-i");
		commend.add(videoLocation);
		commend.add("-y");
		commend.add("-f");
		commend.add("image2");
		commend.add("-ss");
		commend.add("0");
		commend.add("-t");
		commend.add("0.001");
		commend.add("-s");
		commend.add("375x220");
		commend.add(imageLocation);
	try {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(commend);
		builder.start();
		return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public File getMovData() {
		return movData;
	}

	public void setMovData(File movData) {
		this.movData = movData;
	}

	public String getMovType() {
		return movType;
	}

	public void setMovType(String movType) {
		this.movType = movType;
	}
	
}
