package com.bridgelabz.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.bridgelabz.dto.NoteDto;
import com.bridgelabz.utility.ApiResponse;

import reactor.core.publisher.Mono;


public interface INoteService {
	
	public Mono<ApiResponse> getNotes(String token);
	
	public Mono<ApiResponse> postNote(String token, NoteDto noteDto);

	public Mono<ApiResponse> updateNote(String id, NoteDto noteDto);

	public Mono<ApiResponse> deleteNote(String id, String token) ;

	public Mono<ApiResponse> trashAndRestoreNote(String id, String token);

	public Mono<ApiResponse> archieveAndUnarchiveNote(String id, String token);
	
	public void deleteTrashedNote();

	public Mono<ApiResponse> searchNoteByKeyword(String key, String token) throws IllegalArgumentException, UnsupportedEncodingException;

	public Mono<ApiResponse> remindNote(String id, String token, Date date);

	public void sendReminderEmail();

	public Mono<ApiResponse> pinAndUnpinNote(String id, String token);
}