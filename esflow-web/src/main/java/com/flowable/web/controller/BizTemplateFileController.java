package com.flowable.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.flowable.core.bean.BizTemplateFile;
import com.flowable.core.common.utils.Json;
import com.flowable.core.common.utils.LoginUser;
import com.flowable.core.common.utils.PageHelper;
import com.flowable.core.service.BizTemplateFileService;
import com.flowable.core.util.WebUtil;

@Controller
@RequestMapping("/bizTemplateFile")
public class BizTemplateFileController {
	
	@Value("${templateFilePath}")
	private String path ;
	
	@Autowired
	private BizTemplateFileService bizTemplateFileService;
	
	private Logger logger = Logger.getLogger("bizTemplateFileController");

	@RequestMapping("/index")
	public String index(){
		
		return "process/config/bizTemplateFileList";
	}
	
	@ResponseBody
	@RequestMapping("/list")
	public Map<String,Object> list(PageHelper<BizTemplateFile> page,BizTemplateFile file){
		
		PageHelper<BizTemplateFile> helper = bizTemplateFileService.findTemplateFlies(page,file,true);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("total", helper.getTotal());
		data.put("rows", helper.getList());
		return data;
	}
	
	@ResponseBody
	@RequestMapping("/upload")
	public ResponseEntity<String> upload(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response){
		
		HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		Json json = new Json();
		try {
			if("".equals(file.getOriginalFilename())){
				json.setSuccess(false);
				json.setMsg("文件框不能为空!");
				ResponseEntity<String> entity = new ResponseEntity<String>(JSONObject.toJSONString(json), responseHeaders, HttpStatus.OK);
				return entity;
			}
			WebUtil.getLoginUser(request, response);
			LoginUser createUser = WebUtil.getLoginUser();
			String username = createUser.getUsername();
			String flowName = request.getParameter("flowName");
			logger.info("flowName : "+ flowName);
			BizTemplateFile bizTemplateFile = new BizTemplateFile();
			bizTemplateFile.setCreateUser(username);
			bizTemplateFile.setFullName(createUser.getName());
			bizTemplateFile.setFlowName(flowName);
			bizTemplateFileService.saveOrUpdate(bizTemplateFile,file);
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("上传失败: " + (e.getCause() == null ? e.getLocalizedMessage() : e.getCause()));
			ResponseEntity<String> entity = new ResponseEntity<String>(JSONObject.toJSONString(json), responseHeaders, HttpStatus.OK);
			return entity;
		}
		json.setSuccess(true);
		json.setMsg("上传成功");
		ResponseEntity<String> entity = new ResponseEntity<String>(JSONObject.toJSONString(json), responseHeaders, HttpStatus.OK);
		return entity;
	}
	
	@ResponseBody
	@RequestMapping("/downloadTemplate")
	public void downloadTemplate(@RequestParam Map<String,String> params, HttpServletRequest request, HttpServletResponse response){
		
		try {
			BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
			response.reset();
			response.setContentType("application/octet-stream;charset=UTF-8");
			params.put("fileName", URLDecoder.decode(request.getParameter("fileName"),"UTF-8"));
			BizTemplateFile file = bizTemplateFileService.getBizTemplateFile(params);
			String headfilename = null;
			if(file !=null){
				String fileName = file.getFileName();
				String suffix = "";
				if(fileName.lastIndexOf(".")!=-1){
					suffix = fileName.substring(fileName.lastIndexOf("."));
				}
				File inputfile = new File(path+File.separator+file.getId()+suffix);
				BufferedInputStream downfile = null;
				
				if(inputfile.exists()&&inputfile.isFile()){				
					headfilename = new String((file.getFileName()).getBytes("gb2312"),"ISO-8859-1");
					downfile = new BufferedInputStream(new FileInputStream(inputfile));		
				}else{
					headfilename = new String("错误报告.txt".getBytes("gb2312"),"ISO-8859-1");
				}
				response.setHeader("Content-Disposition", "attachment;filename="+headfilename);
				if(downfile!=null){
					byte[] buff = new byte[512];
					while(downfile.read(buff)!=-1){
						outputStream.write(buff);
					}
					downfile.close();
				}else{
					outputStream.write("文件不存在!".getBytes());
				}
			}else{
				logger.info(" templateFile is null ");
				headfilename = new String("错误报告.txt".getBytes("gb2312"),"ISO-8859-1");
				response.setHeader("Content-Disposition", "attachment;filename="+headfilename);
				outputStream.write("文件不存在!请检查文件参数配置是否正确!".getBytes());
			}
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public Json remove(@RequestParam List<String> ids){
		
		Json json = new Json();
		try {
			bizTemplateFileService.deleteByIds(ids);
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("删除失败: "+ (e.getCause() == null ? e.getLocalizedMessage() : e.getCause()));
			return json;
		}
		json.setSuccess(true);
		json.setMsg("删除成功");
		return json;
	}
}
