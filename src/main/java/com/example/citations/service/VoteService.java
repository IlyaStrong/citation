package com.example.citations.service;

import com.example.citations.model.Citation;
import com.example.citations.model.CitationWithScore;
import com.example.citations.model.User;
import com.example.citations.model.Vote;
import com.example.citations.repository.CitationRepository;
import com.example.citations.repository.UserRepository;
import com.example.citations.repository.VoteRepository;
import com.example.citations.request.VoteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoteService {
    private static final Logger logger = LoggerFactory.getLogger(VoteService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CitationRepository citationRepository;
    @Autowired
    private VoteRepository voteRepository;

    public boolean vote(VoteRequest request) {
        try {
            if (Math.abs(request.getScore()) != 1) {
                throw new IllegalArgumentException("Invalid score");
            }
            User user = userRepository.findById(request.getUserId()).orElse(null);
            if(user == null) {
                throw new IllegalStateException("No such user");
            }
            Citation citation = citationRepository.findById(request.getCitationId()).orElse(null);
            if(citation == null) {
                throw new IllegalStateException("No such citation");
            }
            Vote vote = new Vote();
            vote.setScore(request.getScore());
            vote.setUser(user);
            vote.setCitation(citation);
            vote.setCreatedAt(Instant.now());
            voteRepository.save(vote);
            return true;
        }
        catch (Exception e) {
            logger.error("Can not vote: ", e);
            return false;
        }
    }

    public List<Citation> getTopCitations(int n) {
        return voteRepository.getTop(PageRequest.of(0, n));
    }

    public CitationWithScore getGraph(int id) {
        Citation citation = citationRepository.findById(id).orElse(null);
        if (citation == null) {
            throw new IllegalStateException();
        }

        List<Vote> votes = voteRepository.getGraph(citation);
        int score = votes.stream().mapToInt(Vote::getScore).sum();
        List<Integer> graph = new ArrayList<>();
        int curr = 0;
        graph.add(curr);
        for (Vote vote : votes) {
            curr += vote.getScore();
            graph.add(curr);
        }
        return new CitationWithScore(citation, score, graph);
    }


    public void clear() {
        voteRepository.deleteAll();
    }
}
