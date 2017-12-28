package com.dxiang.mring3.bean;

import java.io.Serializable;

/**
 * General Parameters for request
 * 
 * @author Administrator
 *
 */
public class InaccessInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String DID;
	private String SEQ;
	private String DIDPWD;
	private String role;
	private String roleCode;
	private String version;

	public String getDID() {
		return DID;
	}

	public void setDID(String dID) {
		DID = dID;
	}

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}

	public String getDIDPWD() {
		return DIDPWD;
	}

	public void setDIDPWD(String dIDPWD) {
		DIDPWD = dIDPWD;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public InaccessInfo(String dID, String sEQ, String dIDPWD, String role,
			String roleCode, String version) {
		super();
		DID = dID;
		SEQ = sEQ;
		DIDPWD = dIDPWD;
		this.role = role;
		this.roleCode = roleCode;
		this.version = version;
	}

	public InaccessInfo() {
		super();
	}

}
