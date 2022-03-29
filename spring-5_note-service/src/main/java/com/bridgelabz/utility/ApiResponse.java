package com.bridgelabz.utility;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Component
public class ApiResponse {
	
	private String responseMsg;
	private int status;
	private Object data;
	
	public ApiResponse() {
		
	}
	
	public ApiResponse(String responseMsg, int status, Object data) {
		super();
		this.responseMsg = responseMsg;
		this.status = status;
		this.data = data;
	}
}

