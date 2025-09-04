package com.asnServices.response;


public class ValidationResultResponse {

    private String type;
    private Integer rowIndex;
    private String columnName;
    private String errorMessage;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(Integer rowIndex) {
		this.rowIndex = rowIndex;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public ValidationResultResponse(String type, Integer rowIndex, String columnName, String errorMessage) {
		super();
		this.type = type;
		this.rowIndex = rowIndex;
		this.columnName = columnName;
		this.errorMessage = errorMessage;
	}
	public ValidationResultResponse() {
		super();
		// TODO Auto-generated constructor stub
	}


}
