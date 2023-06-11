package com.example.demo.mapper;

import com.example.demo.command.contest.CreateContestCommand;
import com.example.demo.command.contest.UpdateContestCommand;
import com.example.demo.entity.Contest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ContestMapper {

	ContestMapper INSTANCE = Mappers.getMapper(ContestMapper.class);

	Contest toContest(CreateContestCommand command);

	Contest toContest(UpdateContestCommand command);

	void updateContest(UpdateContestCommand command, @MappingTarget Contest contest);
}
