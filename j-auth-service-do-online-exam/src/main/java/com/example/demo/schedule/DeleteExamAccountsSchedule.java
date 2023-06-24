package com.example.demo.schedule;

import com.example.demo.service.AccountExamService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DeleteExamAccountsSchedule {

	private AccountExamService accountExamService;

	@Scheduled(cron = "0 0 0 * * *")
	public void deleteAllAccountsExam() {
		accountExamService.removeAccountsExam();
	}
}
