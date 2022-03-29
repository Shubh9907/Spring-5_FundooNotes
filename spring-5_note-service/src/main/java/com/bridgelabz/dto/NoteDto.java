package com.bridgelabz.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoteDto {

	private String title;
	private String noteBody;
	private boolean inTrash;
	private boolean inArchieve;
	private String noteColor;
	private Date reminder;
	private boolean pined;
}