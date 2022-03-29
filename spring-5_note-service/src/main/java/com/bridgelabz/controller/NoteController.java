package com.bridgelabz.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.dto.NoteDto;
import com.bridgelabz.service.INoteService;
import com.bridgelabz.utility.ApiResponse;

import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/note")
public class NoteController {

	@Autowired
	INoteService iNoteService;
	
	@GetMapping("/notes")
	@ApiOperation("This api is used for getting all the records from Note")
    public Mono<ApiResponse> getAllNotesByUserId( @RequestHeader String token ) {
        return iNoteService.getNotes(token);
	}
	
	@PostMapping("/note")
	@ApiOperation("This api is used for creating a new note")
	public Mono<ApiResponse> postNote(@RequestHeader String token, @RequestBody NoteDto noteDto) {
		return iNoteService.postNote(token, noteDto);
	}
	
	@GetMapping("/searchnote")
	@ApiOperation("This api is used for serching a note based on keyword")
    public Mono<ApiResponse> searchNote(@RequestParam String key, @RequestHeader String token) throws IllegalArgumentException, UnsupportedEncodingException {
        return iNoteService.searchNoteByKeyword(key, token);
	}
	
	@PutMapping("/updateNote/{id}")
	@ApiOperation("This api is used for updating a note")
	public Mono<ApiResponse> updateNote(@PathVariable String id, @RequestBody NoteDto noteDto) {
		return iNoteService.updateNote(id,noteDto);	
	}
	
	@DeleteMapping("/note/{id}")
	@ApiOperation("This api is used for deleting a note")
	public Mono<ApiResponse> deleteNote(@PathVariable String id, @RequestHeader String token) {
		return iNoteService.deleteNote(id, token);	
	}
	
	@PutMapping("/trashNote/{id}")
	@ApiOperation("This api is used for trashing and restoring a note")
	public Mono<ApiResponse> trashAndRestoreNote(@PathVariable String id, @RequestHeader String token) {
		return iNoteService.trashAndRestoreNote(id, token);
	}
	
	@PutMapping("/archieveNote/{id}")
	@ApiOperation("This api is used for archiving and unarchiving a note")
	public Mono<ApiResponse> archieveNote(@PathVariable String id, @RequestHeader String token) {
		return iNoteService.archieveAndUnarchiveNote(id, token);
	}
	
	@PutMapping("/notereminder/{id}")
	@ApiOperation("This api is used for setting reminder for a note")
	public Mono<ApiResponse> remindNote(@PathVariable String id, @RequestHeader String token, @RequestParam Date date) {
		return iNoteService.remindNote(id, token, date);
	}
	
	@PutMapping("/pinnote/{id}")
	@ApiOperation("This api is used for pinning and unpinning a note")
	public Mono<ApiResponse> pinAndUnPinNote(@PathVariable String id, @RequestHeader String token) {
		System.out.println("your token is :- " +token);
		return iNoteService.pinAndUnpinNote(id, token);
	}


	
}
