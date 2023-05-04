package com.example.demo.service;

import java.util.Map;

import com.example.demo.command.SaveEndPointCommand;
import com.example.demo.common.response.CommonResponse;
import com.example.demo.entity.EndPoint;
import com.example.demo.repository.EndPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.example.demo.constant.TranslationCodeConstant.SAVE_FAILURE_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.SAVE_SUCCESS_INFORMATION;

@Service
public class EndPointService {
    @Autowired
    EndPointRepository endPointRepository;

    @Autowired
    private TranslationService translationService;

    public ResponseEntity<?> saveEndPoint(SaveEndPointCommand command){
        var endPointExist = endPointRepository.findByEndPoint(command.getEndPoint());
        if(endPointExist.isPresent()){
            var error = CommonResponse.builder().body(Map.of("message", translationService.getTranslation(SAVE_FAILURE_INFORMATION))).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getBody());
        }
        EndPoint endPoint = new EndPoint();
        endPoint.setEndPoint(command.getEndPoint());
        endPointRepository.save(endPoint);
        var response = CommonResponse.builder().body(Map.of("message", translationService.getTranslation(SAVE_SUCCESS_INFORMATION))).build();
        //TODO: need add message
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getBody());
    }
}
