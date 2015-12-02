package com.mobile.future.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.util.ServletContextAware;

import net.sf.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport implements ServletRequestAware,
		ServletResponseAware, SessionAware, ServletContextAware {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -2710935118112345846L;
	
	
	public HttpServletRequest httpServletRequest;
    public HttpServletResponse httpServletResponse;
    public Map<String, Object> session;
    public ServletContext servletContext;

	public BaseAction() {
		super();
	}

	/**
	 * 输出JSON
	 * 
	 * @param res
	 */
	protected void outputResultJson(String res) {
		if (res == null) {
			return;
		}
		httpServletResponse.setContentType("application/json;charset=UTF-8");
		httpServletResponse.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = httpServletResponse.getWriter();
			out.println(res);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * 把一个Map输出为JSON
	 * 
	 * @param map
	 */
	protected void outputResultJson(Map<String, Object> map) {
		String jsonString = JSONObject.fromObject(map).toString();
		outputResultJson(jsonString);
	}

	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.httpServletRequest = request;
	}

	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.httpServletResponse=response;
	}

	public void setSession(Map<String, Object> session) {
		// TODO Auto-generated method stub
		this.session=session;
	}

	public void setServletContext(ServletContext context) {
		// TODO Auto-generated method stub
		this.servletContext=context;
	}

}
