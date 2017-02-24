package com.quanta.bu12.qoca.utility.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.quanta.bu12.qoca.utility.process.DeviceLogProcess;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
public class DeviceLogController {

	@RequestMapping(value="/getList", method=RequestMethod.POST)
	public @ResponseBody String getList(@RequestParam("data") String data) {
		DeviceLogProcess dlp = new DeviceLogProcess();
		JSONObject obj = new JSONObject();
		try {
			obj.put("result", true);
			obj.put("data", dlp.getList(data));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			obj.put("result", false);
			obj.put("data", new JSONArray());
		}
		return obj.toString();
	}
	@RequestMapping(value="/downLoadFilePrepare/{filePath}", method=RequestMethod.GET)
	public @ResponseBody void downLoadFilePrepare(HttpServletResponse response,@PathVariable(value="filePath") String filePath) {
		DeviceLogProcess dlp = new DeviceLogProcess();
			try {
				dlp.downLoadFilePrepare(filePath);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
	}
	@RequestMapping(value="/downLoadFile/{filePath}", method=RequestMethod.GET)
	public @ResponseBody void downLoadFile(HttpServletResponse response,@PathVariable(value="filePath") String filePath) {
		DeviceLogProcess dlp = new DeviceLogProcess();
			try {
				dlp.downLoadFile(response,filePath);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
	}
	
}
