package com.example.demo.dto;

import com.example.demo.entity.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto {
    private String exam;
    private Double point;
    private String userName;
    private String createdAt;
    private Integer correctAnswers;
    private Integer incorrectAnswers;

    public ResultDto(Result result, String examName) {
        this.exam = examName;
        this.point = result.getTotalPoint();
        this.exam = result.getEmailExaminee();
        this.createdAt = result.getCreatedAt().toString();
        this.correctAnswers = result.getANumberAnswerCorrect();
        this.incorrectAnswers = result.getANumberAnswerInCorrect();
    }
}
