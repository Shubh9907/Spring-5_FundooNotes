package com.bridgelabz.models;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection = "notes")
public class Note {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	private String title;
	private String noteBody;
	private boolean inTrash;
	private boolean inArchieve;
	private Date trashedDate;
	private String noteColor;
	private Date reminder;
	private boolean pined;
	
	private String userId;

}