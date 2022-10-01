package com.dolphin.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 401 Unauthorized.
    UNAUTHORIZED_LOGIN(HttpStatus.UNAUTHORIZED, "401", "로그인이 필요합니다."),
    DO_NOT_MATCH_USER(HttpStatus.UNAUTHORIZED, "401", "작성자만 수정, 삭제를 할 수 있습니다."),

    // 404 Not Fount
    Not_Found_Place(HttpStatus.NOT_FOUND, "404", "존재하는 여행지가 아닙니다."),
    Not_Found_Comment(HttpStatus.NOT_FOUND, "404", "후기가 존재하지 않습니다.");


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}