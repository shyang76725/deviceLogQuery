package com.quanta.bu12.qoca.utility.process;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import com.quanta.bu12.qoca.utility.Property;
import com.quanta.bu12.qoca.utility.component.AAAService;
import com.quanta.bu12.qoca.utility.dbagent.ConnectionManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DeviceLogProcess {

	final Base64.Decoder decoder = Base64.getDecoder();
	final Base64.Encoder encoder = Base64.getEncoder();
	public JSONArray getList(String data) throws IOException, InterruptedException {
		JSONObject dataObj = getData(data);
		JSONArray arr = new JSONArray();
		arr.addAll(getQueryPathResult(dataObj,"uuid"));
		arr.addAll(getQueryPathResult(dataObj,"mac"));
		return arr;
	}
	private JSONArray getQueryPathResult(JSONObject dataObj, String conditonStr) throws IOException, InterruptedException {
		JSONArray arr = new JSONArray();
		String idPath = dataObj.getString(conditonStr);
		if("uuid".equals(conditonStr)&&!"".equals(idPath)){
			idPath="*"+idPath;
		}
		String condition = "\"./"
				+dataObj.getString("Year")+"/"
				+dataObj.getString("Month")+"/"
				+dataObj.getString("Date")+"/"
				+idPath+"/*\"";
		
		String[] cmd = {"sshpass",
	   			 "-p"+Property.getNodepw(),
	   			 "ssh",
	   			 "-o",
	   			 "stricthostkeychecking=no",
	   			 "-t",
	   			 Property.getNodeUser()+"@"+Property.getNodeIp(),
	   			 "cd "+Property.getDeviceLogLocation()+";find -type f -wholename "+condition+";"};
		Process p;
		p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			JSONObject obj = new JSONObject();
			obj.put("Q001", encode(line));
			String[] filePath = line.split("/");
			obj.put("Q002", filePath[1]);
			obj.put("Q003", filePath[2]);
			obj.put("Q004", filePath[3]);
			obj.put("Q005", filePath[5]);
			obj.put("Q006", dataObj.getString("mac"));
			obj.put("Q007", dataObj.getString("uuid"));
			arr.add(obj);
		}
		return arr;
	}
	private JSONObject getData(String data) throws FileNotFoundException, IOException {
		JSONObject dataObj = JSONObject.fromObject(data);
		AAAService aaa = new AAAService();
		String uuid=aaa.getUUIDFromAAA(dataObj.getString("ID"));
		if (uuid == null){
			uuid="";
		}
		dataObj.put("uuid",uuid);
		dataObj.put("quuid","%"+uuid);//for sql query
		if("".equals(uuid)){
			dataObj.put("mac","");
		}else{
			ConnectionManager conn = new ConnectionManager("csware");
			String macId = conn.queryForSingleString("SELECT mac "
					+ " FROM device d1 "
					+ " join th_patient t1 on t1.th_patient_group_id=d1.th_patient_group_id  WHERE th_patient_id like :quuid", dataObj);
			if (macId == null){
				macId="";
			}
			dataObj.put("mac",macId);
		}
		return dataObj;
	}
	public void downLoadFilePrepare(String data) throws IOException, InterruptedException {
		data = decode(data);
		String[] cmd = {"rm",
		"-rf",
		"deviceLogQueryFile" };
		String resultStr = cmdExe(cmd);
		String[] cmd2 = { 
				"mkdir",
				"deviceLogQueryFile" };
		resultStr = cmdExe(cmd2);
		String[] cmd3 = { "sshpass", 
				"-p"+Property.getNodepw(),
				"scp",
				Property.getNodeIp()+":"+Property.getDeviceLogLocation()+"/"+data,
				"deviceLogQueryFile" };
		resultStr = cmdExe(cmd3);
	}
	public void downLoadFile(HttpServletResponse response,String data) throws IOException, InterruptedException {
		data = decode(data);
		String fileName = data.split("/")[5];
		InputStream fileStream = new FileInputStream("deviceLogQueryFile/"+fileName);
		response.addHeader("Content-disposition", "attachment;filename="+fileName);
		response.setContentType("txt/plain");
		IOUtils.copy(fileStream, response.getOutputStream());
		response.flushBuffer();
	}
    /**
     * do shell command 
     * @param cmd, shell command with parameter
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String cmdExe(String[] cmd) throws IOException, InterruptedException{
    	Process p;
		p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		StringBuilder output = new StringBuilder();
		BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
		while ((line = reader.readLine())!= null) {
			output.append(line + "\n");
		}
		return output.toString();
    }
    /**
     * do shell command 
     * @param cmd, shell command
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String cmdExe(String cmd) throws IOException, InterruptedException{
    	Process p;
		p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		StringBuilder output = new StringBuilder();
		BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
		while ((line = reader.readLine())!= null) {
			output.append(line + "\n");
		}
		return output.toString();
    }
	public String encode(String text) throws UnsupportedEncodingException{
		final byte[] textByte = text.getBytes("UTF-8");
		return encoder.encodeToString(textByte);
	}
	public String decode(String encodedText) throws UnsupportedEncodingException{
		return new String(decoder.decode(encodedText), "UTF-8");
	}
}
