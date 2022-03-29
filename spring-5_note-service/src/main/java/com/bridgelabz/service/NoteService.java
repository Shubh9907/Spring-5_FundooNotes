package com.bridgelabz.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bridgelabz.dto.NoteDto;
import com.bridgelabz.models.Note;
import com.bridgelabz.models.User;
import com.bridgelabz.repository.NoteRepository;
import com.bridgelabz.utility.ApiResponse;
import com.bridgelabz.utility.JwtToken;
import com.bridgelabz.utility.MailService;
import com.rabbitmq.client.AMQP.Basic.Return;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NoteService implements INoteService {

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	NoteRepository noteRepo;

	@Autowired
	Environment environment;

	@Autowired
	ApiResponse apiResponse;

	@Autowired
	JwtToken jwtToken;

	@Autowired
	RabbitTemplate template;

	@Autowired
	MailService mailService;

	@Autowired
	private RestTemplate restTemplate;

	ApiResponse userNotFound = new ApiResponse("User Not Found", 601, null);

	@Override
	public Mono<ApiResponse> getNotes(String token) {
		String email = jwtToken.decodeToken(token);
		ApiResponse response = restTemplate.getForObject("http://localhost:9090/user/" + email, ApiResponse.class);
		User user = modelMapper.map(response.getData(), User.class);
		return noteRepo.findAllByUserId(user.getId()).map(note -> {
			return new ApiResponse("Note List", 1, note);
		});
	}

	@Override
	public Mono<ApiResponse> postNote(String token, NoteDto noteDto) {
		String email = jwtToken.decodeToken(token);
		ApiResponse response = restTemplate.getForObject("http://localhost:9090/user/" + email, ApiResponse.class);
		User user = modelMapper.map(response.getData(), User.class);
		Note note = modelMapper.map(noteDto, Note.class);
		note.setUserId(user.getId());
		return noteRepo.save(note).map(note1 -> {
			return new ApiResponse(environment.getProperty("note.addedSuccessfully"), 200, note1);
		});
	}

	@Override
	public Mono<ApiResponse> updateNote(String id, NoteDto noteDto) {
		return noteRepo.findById(id).flatMap(note -> {
			if (noteDto.getTitle() != null) {
				note.setTitle(noteDto.getTitle());
			}
			if (noteDto.getNoteBody() != null) {
				note.setNoteBody(noteDto.getNoteBody());
			}
			note.setInTrash(noteDto.isInTrash());
			note.setInArchieve(noteDto.isInArchieve());
			note.setNoteColor(noteDto.getNoteColor());
			note.setReminder(noteDto.getReminder());
			note.setPined(noteDto.isPined());

			return noteRepo.save(note).map(note1 -> {
				return new ApiResponse(environment.getProperty("note.updateSuccessfully"), 1, null);
			});
		}).switchIfEmpty(Mono.just(new ApiResponse(environment.getProperty("user.invalidDetails"), 2, null)));
	}

	@Override
	public Mono<ApiResponse> deleteNote(String id, String token) {
		String email = jwtToken.decodeToken(token);
		ApiResponse response = restTemplate.getForObject("http://localhost:9090/user/" + email, ApiResponse.class);
		User user = modelMapper.map(response.getData(), User.class);
		return noteRepo.findById(id).map(note -> {
			if (note.getUserId().equals(user.getId())) {
				noteRepo.deleteById(id);
				return new ApiResponse("Note Successfully Deleted", 1, note);
			}
			return new ApiResponse(environment.getProperty("user.invalidDetails"), 2, null);
		}).switchIfEmpty(Mono.just(new ApiResponse(environment.getProperty("user.invalidDetails"), 2, null)));
	}

	@Override
	public Mono<ApiResponse> trashAndRestoreNote(String id, String token) {
		String email = jwtToken.decodeToken(token);
		ApiResponse response = restTemplate.getForObject("http://localhost:9090/user/" + email, ApiResponse.class);
		User user = modelMapper.map(response.getData(), User.class);
		return noteRepo.findById(id).flatMap(note -> {
			if (!note.isInTrash()) {
				note.setInTrash(true);
				note.setTrashedDate(new Date());
				note.setInArchieve(false);
				note.setPined(false);
				if (note.getUserId().equals(user.getId())) {
					return noteRepo.save(note).map(note1 -> {
						return new ApiResponse("Note Trashed", 1, null);
					});
				}
			} else {
				note.setInTrash(false);
				note.setTrashedDate(null);
				if (note.getUserId().equals(user.getId())) {
					return noteRepo.save(note).map(note1 -> {
						return new ApiResponse("Note Restored", 1, null);
					});
				}
			}
			return null;
		}).switchIfEmpty(Mono.just(new ApiResponse(environment.getProperty("user.invalidDetails"), 2, null)));
	}

	@Override
	public Mono<ApiResponse> archieveAndUnarchiveNote(String id, String token) {
		String email = jwtToken.decodeToken(token);
		ApiResponse response = restTemplate.getForObject("http://localhost:9090/user/" + email, ApiResponse.class);
		User user = modelMapper.map(response.getData(), User.class);
		return noteRepo.findById(id).flatMap(note -> {
			if (!note.isInArchieve()) {
				note.setInArchieve(true);
				note.setInTrash(false);
				note.setPined(false);
				if (note.getUserId().equals(user.getId())) {
					return noteRepo.save(note).map(note1 -> {
						return new ApiResponse("Note successfully Archieves", 1, null);
					});
				}
			} else {
				note.setInArchieve(false);
				if (note.getUserId().equals(user.getId())) {
					return noteRepo.save(note).map(note1 -> {
						return new ApiResponse("Note successfully Unarchieved", 1, null);
					});
				}
			}
			return null;
		}).switchIfEmpty(Mono.just(new ApiResponse(environment.getProperty("user.invalidDetails"), 2, null)));
	}

	@Override
	public void deleteTrashedNote() {
		noteRepo.findAll().filter(note -> note.isInTrash()).map(note1 -> {
			LocalDate date = note1.getTrashedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate date2 = date.plusDays(7);
			if (date2.compareTo(currentDate) < 0) {
				noteRepo.deleteById(note1.getId());
			}
			return null;
		});
	}

	@Override
	public Mono<ApiResponse> searchNoteByKeyword(String key, String token)
			throws IllegalArgumentException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<ApiResponse> remindNote(String id, String token, Date date) {
		String email = jwtToken.decodeToken(token);
		ApiResponse response = restTemplate.getForObject("http://localhost:9090/user/" + email, ApiResponse.class);
		User user = modelMapper.map(response.getData(), User.class);
		return noteRepo.findById(id).flatMap(note -> {
			note.setReminder(date);
			if (note.getUserId().equals(user.getId())) {
				return noteRepo.save(note).map(note1 -> {
					return new ApiResponse("Note successfully added to Reminder", 1, null);
				});
			}
			return null;
		}).switchIfEmpty(Mono.just(new ApiResponse(environment.getProperty("user.invalidDetails"), 2, null)));
	}

	@Override
	public void sendReminderEmail() {
		noteRepo.findAll().filter(note -> note.getReminder() != null).map(note1 -> {
			LocalDate remindDate = note1.getReminder().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (remindDate.compareTo(currentDate) < 0) {
//				mailService.sendNoteReminderMail(note.getUser().getEmail(), note);
			}
			return null;
		});
	}

	@Override
	public Mono<ApiResponse> pinAndUnpinNote(String id, String token) {
		String email = jwtToken.decodeToken(token);
		ApiResponse response = restTemplate.getForObject("http://localhost:9090/user/" + email, ApiResponse.class);
		User user = modelMapper.map(response.getData(), User.class);
		return noteRepo.findById(id).flatMap(note -> {
			if (note.isPined() == false) {
				note.setPined(true);
				note.setInArchieve(false);
				note.setInTrash(false);
				if (note.getUserId().equals(user.getId())) {
					return noteRepo.save(note).map(note1 -> {
						return new ApiResponse("Note successfully pinned", 1, null);
					});
				}
			} else {
				note.setPined(false);
				if (note.getUserId().equals(user.getId())) {
					return noteRepo.save(note).map(note1 -> {
						return new ApiResponse("Note successfully unpinned", 1, null);
					});
				}
			}
			return null;
		}).switchIfEmpty(Mono.just(new ApiResponse(environment.getProperty("user.invalidDetails"), 2, null)));
}

}