package com.bytatech.patientservice.service.dto;

public class RecordDTO {
	private byte[] file;
	private String idpCode;
	private Long medicalCaseId;
	
	
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	public String getIdpCode() {
		return idpCode;
	}
	public void setIdpCode(String idpCode) {
		this.idpCode = idpCode;
	}
	public Long getMedicalCaseId() {
		return medicalCaseId;
	}
	public void setMedicalCaseId(Long medicalCaseId) {
		this.medicalCaseId = medicalCaseId;
	}
	

}

