package com.example.citations.service;

import com.example.citations.model.Citation;
import com.example.citations.model.User;
import com.example.citations.repository.CitationRepository;
import com.example.citations.repository.UserRepository;
import com.example.citations.request.CitationCreateRequest;
import com.example.citations.request.CitationUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitationService {
    private static final Logger logger = LoggerFactory.getLogger(CitationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CitationRepository citationRepository;

    public List<Citation> getAllCitations() {
        return citationRepository.findAll();
    }

    public Optional<Citation> getCitation(int id) {
        return citationRepository.findById(id);
    }

    public boolean createCitation(CitationCreateRequest request) {
        try {
            Citation citation = new Citation();
            Optional<User> requestResult = userRepository.findById(request.getAuthorId());
            if (requestResult.isEmpty()) {
                throw new IllegalStateException("No such user");
            }
            citation.setAuthor(requestResult.get());
            citation.setContent(request.getContent());
            citationRepository.save(citation);
            return true;
        }
        catch (Exception e) {
            logger.error("Can not create citation: ", e);
            return false;
        }
    }

    public boolean updateCitation(CitationUpdateRequest request) {
        try {
            Optional<User> requestResult = userRepository.findById(request.getAuthorId());
            if (requestResult.isEmpty()) {
                throw new IllegalStateException("No such user");
            }
            citationRepository.updateCitation(request.getContent(), requestResult.get(), request.getId());
            return true;
        }
        catch (Exception e) {
            logger.error("Can not create citation: ", e);
            return false;
        }
    }

    public boolean deleteCitation(int id) {
        Optional<Citation> requestResult = citationRepository.findById(id);
        if(requestResult.isPresent()) {
            citationRepository.deleteById(id);
            return true;
        }
        else {
            return false;
        }
    }

    public void clear() {
        citationRepository.deleteAll();
    }
}
