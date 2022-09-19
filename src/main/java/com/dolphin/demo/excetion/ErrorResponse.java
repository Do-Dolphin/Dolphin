package com.dolphin.demo.excetion;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {    //예외 형태를 잡아주는 클래스. 메세지와 상태, 시간정보를 답고 있으며 발생된 에러를 반환할 형태
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public static ResponseEntity<ErrorResponse> responseEntity(ErrorCode errorCode){
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getErrorCode())
                        .code(errorCode.name())
                        .message(errorCode.getErrorMessage())
                        .build()
                );
    }
}