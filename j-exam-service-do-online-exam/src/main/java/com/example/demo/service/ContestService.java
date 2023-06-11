package com.example.demo.service;

import com.example.demo.command.contest.CreateExamineeAccount;
import com.example.demo.command.contest.UpdateContestCommand;
import com.example.demo.common.generator.PasswordGenerator;
import com.example.demo.dto.ExamineeAccount;
import com.example.demo.entity.Contest;
import com.example.demo.mapper.ContestMapper;
import com.example.demo.repository.ContestRepository;
import jakarta.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class ContestService {
	private ContestRepository contestRepository;

	public List<Contest> getContestsByUserName(String username) {
		return contestRepository.getContestsByCreatedBy(username);
	}

	public Contest getContestById(Long id) {
		return contestRepository
				.findById(id)
				.orElseThrow(() -> new BadRequestException("Cannot find contestId"));
	}

	public Contest createContest(Contest contest) {
		return contestRepository.save(contest);
	}

	public Contest updateContest(UpdateContestCommand command) {
		var contest =
				contestRepository
						.findById(command.getId())
						.orElseThrow(() -> new BadRequestException("Contest's id cannot be null"));
		ContestMapper.INSTANCE.updateContest(command, contest);
		return contestRepository.save(contest);
	}

	public List<ExamineeAccount> createExamineeAccount(List<CreateExamineeAccount> mails) {
		mails.forEach(mail -> mail.setPassword(PasswordGenerator.generateRandomPassword()));

		// TODO: Call to Auth-Service to create Examinee Account
		RestTemplate restTemplate = new RestTemplate();

		List<CreateExamineeAccount> result = new ArrayList<>();

		return null;
	}
}
