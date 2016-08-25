package com.sd.action;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.jboss.logging.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.sd.service.PicService;
import com.sd.vo.Trespic;

@Controller
@RequestMapping("/pic")
public class PicAction {
	@Resource 
	private PicService picService;
	//加载
	private String gid;
	private String type;
	//上传
	private File imgData;
	private String picType;
	private String imgAngle;
	private int width;
	private int height;
	private void water(String imgPath) {
		try {
			String waterPath = PicAction.class.getResource("PicAction.class").toString(); 
			waterPath = waterPath.substring(0,waterPath.indexOf("WEB-INF"))+"Image/water.png "; 
			waterPath=waterPath.substring(waterPath.indexOf("/")+1,waterPath.length()-1).replace("/", "\\\\");
		    String extend = imgPath.substring(imgPath.lastIndexOf("."));
			if (!".gif".toLowerCase().equals(extend)) {
				BufferedImage src = ImageIO.read(new File(imgPath));
				int width = src.getWidth();
				int height = src.getHeight();
				BufferedImage water = ImageIO.read(new File(waterPath));
				int waWidth = water.getWidth();
				int waHeight = water.getHeight();
				BufferedImage image = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics graph = image.createGraphics();
				graph.drawImage(src, 0, 0, width, height, null);
				if (height > waHeight * 2 && width > waWidth * 2) {
					graph.drawImage(water, width-waWidth-8, height-waHeight-8, waWidth,
							waHeight, null);
					graph.dispose();
					OutputStream out = new FileOutputStream(imgPath);
					ImageIO.write(image, "jpeg", out);
					out.flush();
					out.close();
				}
				image.flush();
				water.flush();
				src.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//图片添加水印
	public void watermark(){
		String uuid = UUID.randomUUID().toString();
		try {
			String path="/usr/data/pic/";
			Date date=new Date(); 
			SimpleDateFormat stingDateFormat  = new SimpleDateFormat("yyyy/MM/dd/");  
			path+= stingDateFormat.format(date);
			File fileB = new File(path+"Big");
			File fileM= new File(path+"Mid");
			File fileS= new File(path+"Small");
			// 如果指定的路径没有就创建    
			if (!fileB.exists()) {    
				fileB.mkdirs();    
			} 
			if (!fileM.exists()) {    
				fileM.mkdirs();    
			} 
			if (!fileS.exists()) {    
				fileS.mkdirs();    
			} 
			FileOutputStream fos = new FileOutputStream(path+"Big/"+uuid+"."+picType);
			FileInputStream fis = new FileInputStream(imgData);  
			byte[] buf = new byte[1024];  
			int len = 0;  
			while ((len = fis.read(buf)) > 0) {  
			    fos.write(buf, 0, len);  
			}  
			if (fis != null)  
			    fis.close();  
			if (fos != null)  
			    fos.close();
			float mid,small;
			if(imgData.length()/1024>500){
				mid=1/(float)(imgData.length()/1024/500);
				small=(float)0.1;
			}else{
				mid=1;
				if(imgData.length()/1024>50){
					small=1/(float)(imgData.length()/1024/50);
				}else{
					small=1;
				}
			}
			water(path+"Big/"+uuid+"."+picType);
			compressPic(path+"Big/"+uuid+"."+picType,path+"Mid/"+uuid+"."+picType,mid);
			compressPic(path+"Mid/"+uuid+"."+picType,path+"Small/"+uuid+"."+picType,(float)small);
//			water(path+"Small\\"+uuid+"."+picType);
			Trespic trespic=new Trespic();
			trespic.setRpGid(uuid);
			trespic.setRpUrl(path+"Big/"+uuid+"."+picType);
			trespic.setRpSmall(path+"Small/"+uuid+"."+picType);
			trespic.setRpMid(path+"Mid/"+uuid+"."+picType);
			trespic.setRpAngle(imgAngle);
			if(width!=0){
				trespic.setRpWidth(width);
			}
			if(height!=0){
				trespic.setRpHeight(height);
			}
			picService.save(trespic);
			JSONObject json = new JSONObject();
			json.put("code", "1");
			json.put("message", "上传成功");
			json.put("imgGid",uuid);
			HttpServletResponse rep = ServletActionContext.getResponse();
			rep.setCharacterEncoding("UTF-8");
			rep.setContentType("text/plain");
			rep.getWriter().write(json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	//普通上传
	@RequestMapping("/upload.do")
	public void execute(HttpServletRequest request,HttpServletResponse response) throws Exception {  
		PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");  
        String uuid = UUID.randomUUID().toString();
        FileItemFactory factory = new DiskFileItemFactory();  
        ServletFileUpload upload = new ServletFileUpload(factory);  
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile imgData = multipartRequest.getFile("imgData");
        picType=multipartRequest.getParameter("picType");
		String path="/usr/data/pic/";
		Date date=new Date(); 
		SimpleDateFormat stingDateFormat  = new SimpleDateFormat("yyyy/MM/dd/");  
		path+= stingDateFormat.format(date);
		File fileB = new File(path+"Big");
		File fileM= new File(path+"Mid");
		File fileS= new File(path+"Small");
		// 如果指定的路径没有就创建    
		if (!fileB.exists()) {    
			fileB.mkdirs();    
		} 
		if (!fileM.exists()) {    
			fileM.mkdirs();    
		} 
		if (!fileS.exists()) {    
			fileS.mkdirs();    
		} 
		FileOutputStream fos = new FileOutputStream(path+"Big/"+uuid+picType);
//    			FileInputStream fis = new FileInputStream(imgData); 
		InputStream fis = imgData.getInputStream();  
		byte[] buf = new byte[1024];  
		int len = 0;  
		while ((len = fis.read(buf)) > 0) {  
		    fos.write(buf, 0, len);  
		}  
		if (fis != null)  
		    fis.close();  
		if (fos != null)  
		    fos.close();
		float mid,small;
		if(imgData.getSize()/1024>500){
			mid=1/(float)(imgData.getSize()/1024/500);
			small=(float)0.1;
		}else{
			mid=1;
			if(imgData.getSize()/1024>50){
				small=1/(float)(imgData.getSize()/1024/50);
			}else{
				small=1;
			}
		}
		compressPic(path+"Big/"+uuid+picType,path+"Mid/"+uuid+picType,mid);
		compressPic(path+"Mid/"+uuid+picType,path+"Small/"+uuid+picType,(float)small);
		Trespic trespic=new Trespic();
		trespic.setRpGid(uuid);
		trespic.setRpUrl(path+"Big/"+uuid+picType);
		trespic.setRpSmall(path+"Small/"+uuid+picType);
		trespic.setRpMid(path+"Mid/"+uuid+picType);
		trespic.setRpAngle(imgAngle);
		if(width!=0){
			trespic.setRpWidth(width);
		}
		if(height!=0){
			trespic.setRpHeight(height);
		}
		picService.save(trespic); 
        out.print(uuid);  
        out.close();  
    }
	//后台管理用的
	@RequestMapping("/save.do")
	public void savePic(Model model,HttpServletRequest request,HttpServletResponse response,@RequestParam MultipartFile imgData,@Param String imgAngle,@Param String picType){
		String uuid = UUID.randomUUID().toString();
		try {
			String path="/usr/data/pic/";
			Date date=new Date(); 
			SimpleDateFormat stingDateFormat  = new SimpleDateFormat("yyyy/MM/dd/");  
			path+= stingDateFormat.format(date);
			File fileB = new File(path+"Big");
			File fileM= new File(path+"Mid");
			File fileS= new File(path+"Small");
			// 如果指定的路径没有就创建    
			if (!fileB.exists()) {    
				fileB.mkdirs();    
			} 
			if (!fileM.exists()) {    
				fileM.mkdirs();    
			} 
			if (!fileS.exists()) {    
				fileS.mkdirs();    
			} 
			FileOutputStream fos = new FileOutputStream(path+"Big/"+uuid+"."+picType);
			InputStream fis =imgData.getInputStream();  
			byte[] buf = new byte[1024];  
			int len = 0;  
			while ((len = fis.read(buf)) > 0) {  
			    fos.write(buf, 0, len);  
			}  
			if (fis != null)  
			    fis.close();  
			if (fos != null)  
			    fos.close();
			float mid,small;
			if(imgData.getSize()/1024>500){
				mid=1/(float)(imgData.getSize()/1024/500);
				small=(float)0.1;
			}else{
				mid=1;
				if(imgData.getSize()/1024>50){
					small=1/(float)(imgData.getSize()/1024/50);
				}else{
					small=1;
				}
			}
			compressPic(path+"Big/"+uuid+"."+picType,path+"Mid/"+uuid+"."+picType,mid);
			compressPic(path+"Mid/"+uuid+"."+picType,path+"Small/"+uuid+"."+picType,(float)small);
			Trespic trespic=new Trespic();
			trespic.setRpGid(uuid);
			trespic.setRpUrl(path+"Big/"+uuid+"."+picType);
			trespic.setRpSmall(path+"Small/"+uuid+"."+picType);
			trespic.setRpMid(path+"Mid/"+uuid+"."+picType);
			trespic.setRpAngle(imgAngle);
			if(width!=0){
				trespic.setRpWidth(width);
			}
			if(height!=0){
				trespic.setRpHeight(height);
			}
			picService.save(trespic);
			response.setCharacterEncoding("UTF-8");
			String ss="<script>window.onload = function() {"
					+ "window.parent.postMessage('"+uuid+"', '*');};</script>";
			response.getWriter().write(ss);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public void load(){
		try {
			String s="";
			Trespic trespic=new Trespic();
			trespic.setRpGid(gid);
			trespic=picService.query(trespic);
			//现在没有传参type=null，那现在返回的都是small的照片
			if(type==null||type.equals("thumb")){
				s=trespic.getRpSmall();
			}else if(type.equals("normal")){
				s=trespic.getRpMid();
			}else{
				s=trespic.getRpUrl();
			}
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
	//手机端上传
	public void save(){
		String uuid = UUID.randomUUID().toString();
		try {
			String path="/usr/data/pic/";
			Date date=new Date(); 
			SimpleDateFormat stingDateFormat  = new SimpleDateFormat("yyyy/MM/dd/");  
			path+= stingDateFormat.format(date);
			File fileB = new File(path+"Big");
			File fileM= new File(path+"Mid");
			File fileS= new File(path+"Small");
			// 如果指定的路径没有就创建    
			if (!fileB.exists()) {    
				fileB.mkdirs();    
			} 
			if (!fileM.exists()) {    
				fileM.mkdirs();    
			} 
			if (!fileS.exists()) {    
				fileS.mkdirs();    
			} 
			FileOutputStream fos = new FileOutputStream(path+"Big/"+uuid+"."+picType);
			FileInputStream fis = new FileInputStream(imgData);  
			byte[] buf = new byte[1024];  
			int len = 0;  
			while ((len = fis.read(buf)) > 0) {  
			    fos.write(buf, 0, len);  
			}  
			if (fis != null)  
			    fis.close();  
			if (fos != null)  
			    fos.close();
			float mid,small;
			if(imgData.length()/1024>500){
				mid=1/(float)(imgData.length()/1024/500);
				small=(float)0.1;
			}else{
				mid=1;
				if(imgData.length()/1024>50){
					small=1/(float)(imgData.length()/1024/50);
				}else{
					small=1;
				}
			}
			compressPic(path+"Big/"+uuid+"."+picType,path+"Mid/"+uuid+"."+picType,mid);
			compressPic(path+"Mid/"+uuid+"."+picType,path+"Small/"+uuid+"."+picType,(float)small);
			Trespic trespic=new Trespic();
			trespic.setRpGid(uuid);
			trespic.setRpUrl(path+"Big/"+uuid+"."+picType);
			trespic.setRpSmall(path+"Small/"+uuid+"."+picType);
			trespic.setRpMid(path+"Mid/"+uuid+"."+picType);
			trespic.setRpAngle(imgAngle);
			if(width!=0){
				trespic.setRpWidth(width);
			}
			if(height!=0){
				trespic.setRpHeight(height);
			}
			picService.save(trespic);
			JSONObject json = new JSONObject();
			json.put("code", "1");
			json.put("message", "上传成功");
			json.put("imgGid",uuid);
			HttpServletResponse rep = ServletActionContext.getResponse();
			rep.setCharacterEncoding("UTF-8");
			rep.setContentType("text/plain");
			rep.getWriter().write(json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public boolean compressPic(String srcFilePath, String descFilePath,float qua)  
    {  
        File file = null;  
        BufferedImage src = null;  
        FileOutputStream out = null;  
        ImageWriter imgWrier;  
        ImageWriteParam imgWriteParams;  
  
        // 指定写图片的方式为 jpg  
        imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();  
        imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);  
        // 要使用压缩，必须指定压缩方式为MODE_EXPLICIT  
        imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);  
        // 这里指定压缩的程度，参数qality是取值0~1范围内，  
        imgWriteParams.setCompressionQuality(qua);  
        imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);  
        ColorModel colorModel = ColorModel.getRGBdefault();  
        // 指定压缩时使用的色彩模式  
        imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel  
                .createCompatibleSampleModel(16, 16)));  
  
        try  
        {  
            if(StringUtils.isBlank(srcFilePath))  
            {  
                return false;  
            }  
            else  
            {  
                file = new File(srcFilePath);  
                src = ImageIO.read(file);  
                out = new FileOutputStream(descFilePath);  
  
                imgWrier.reset();  
                // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何 OutputStream构造  
                imgWrier.setOutput(ImageIO.createImageOutputStream(out));  
                // 调用write方法，就可以向输入流写图片  
                imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);  
                out.flush();  
                out.close();  
            }  
        }  
        catch(Exception e)  
        {  
            e.printStackTrace();  
            return false;  
        }  
        return true;  
    }  
	
	
	public String getPicType() {
		return picType;
	}
	public void setPicType(String picType) {
		this.picType = picType;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public File getImgData() {
		return imgData;
	}
	public void setImgData(File imgData) {
		this.imgData = imgData;
	}
	public String getImgAngle() {
		return imgAngle;
	}
	public void setImgAngle(String imgAngle) {
		this.imgAngle = imgAngle;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	
}
