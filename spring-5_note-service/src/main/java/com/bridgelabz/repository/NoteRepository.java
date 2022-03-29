package com.bridgelabz.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bridgelabz.models.Note;

import reactor.core.publisher.Mono;


public interface NoteRepository extends ReactiveMongoRepository<Note, Integer> {

	Mono<List<Note>> findAllByUserId(String id);

	void deleteById(String id);

	Mono<Note> findById(String id);

}
