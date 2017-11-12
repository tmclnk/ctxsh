package be.shouldyou.example.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Submission {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String xmlData;
	private Date createdDate;
	private String submissionId;
	
	public String getXmlData() {
		return xmlData;
	}
	public void setXmlData(String xml) {
		this.xmlData = xml;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(String submissionId) {
		this.submissionId = submissionId;
	}
}
