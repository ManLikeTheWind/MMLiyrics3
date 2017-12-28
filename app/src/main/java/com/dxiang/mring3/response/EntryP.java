package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;

import com.dxiang.mring3.utils.Utils;

/**
 * 父类
 * 
 * @author hezhejing
 * 
 */
public class EntryP {

	// 描述
	public String description;
	// 返回结果
	public String result = "";
	// 默认错误码
	public int error_code = 1;

	public String returnKey = "";

	protected SoapObject resultObject;

	/**
	 * 父类数据解析
	 * 
	 * @param result
	 * @return
	 */
	public EntryP getEntryP(SoapObject result) {
		resultObject = result;
		if (Utils.CheckTextNull(returnKey)) {
			resultObject = (SoapObject) result.getProperty(returnKey);
		}
		this.setResult(Utils.reSoapObjectStr(resultObject, "returnCode"));
		this.setDescription(Utils.reSoapObjectStr(resultObject, "description"));
		return this;
	}

	public void setResult(String result) {
		this.result = result;
		if ("000000".equalsIgnoreCase(result)||result.length()==0) {
			this.error_code = 0;
		} else {
			this.error_code = 1;
		}

	}

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	public String getResult() {
		return result;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
