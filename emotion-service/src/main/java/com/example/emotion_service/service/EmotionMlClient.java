//package com.example.emotion_service.service;
//
//import com.example.emotion_service.config.FeignMultipartConfig;
//import com.example.emotion_service.dto.EmotionResultDTO;
//import com.example.emotion_service.dto.FastApiResponseDTO;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.multipart.MultipartFile;
//
//@FeignClient(
//        name = "emotion-ml-service",
//        url = "http://127.0.0.1:8000",
//        configuration = FeignMultipartConfig.class
//)
//public interface EmotionMlClient {
//
//    @PostMapping(
//            value = "/api/v1/image/predict",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    FastApiResponseDTO detectImageEmotion(@RequestPart("file") MultipartFile file);
//
//    @PostMapping(
//            value = "/chat/speech-emotion/predict/uploadfile",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    EmotionResultDTO detectSpeechEmotion(@RequestPart("file") MultipartFile file);
//}
//
//
//
