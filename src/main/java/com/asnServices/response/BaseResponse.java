package com.asnServices.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseResponse<T> {

    private Integer status;
    private String message;
    private List<T> data;
    private Integer code;
	private String logId;
	private Integer totalPageCount;
	private Long totalRecordCount;

    public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}


	public BaseResponse(Integer status, String message, Integer code, String logId) {
		this.status = status;
		this.message = message;

		this.code = code;
		this.logId = logId;
	}


}