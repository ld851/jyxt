package com.jzsec.gupiao.entity;

public class ManageCommand {

	private String type;
	private String parameter;


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getParameter() {
		return parameter;
	}


	public void setParameter(String parameter) {
		this.parameter = parameter;
	}


	@Override
	public String toString()
	{
		return "{" + type + ":" + parameter + "}";
	}
	

	
	
}
