package com.dolphin.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //400 Bad Request
    BAD_REQUEST_IMAGE(HttpStatus.BAD_REQUEST, "400", "이미지 파일이 아닙니다."),
    BAD_REQUEST_PASSWORD(HttpStatus.BAD_REQUEST, "400", "비밀번호가 일치하지 않습니다."),
    BAD_REQUEST_EMAIL(HttpStatus.BAD_REQUEST, "400", "아이디 중복체크는 필수입니다."),
    BAD_REQUEST_TOKEN(HttpStatus.BAD_REQUEST, "400", "토큰 기간이 만료되지 않아서 갱신되지 않습니다"),

    // 401 Unauthorized.
    UNAUTHORIZED_OUT(HttpStatus.UNAUTHORIZED, "401", "회원탈퇴 후 7일이 지나지 않았습니다."),
    UNAUTHORIZED_LOGIN(HttpStatus.UNAUTHORIZED, "401", "로그인이 필요합니다."),
    DO_NOT_MATCH_USER(HttpStatus.UNAUTHORIZED, "401", "작성자가 아닙니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "401", "refresh token 기간이 만료 되었습니다."),



    // 404 Not Fount
    NOT_FOUND_PLACE(HttpStatus.NOT_FOUND, "404", "존재하는 여행지가 아닙니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "404", "존재하지 않는 후기입니다."),
    NOT_FOUND_COURSE(HttpStatus.NOT_FOUND, "404", "존재하지 않는 코스입니다."),
    NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "404", "존재하지 않는 요청입니다."),
    NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, "404", "존재하지 않는 행사입니다."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "404", "토큰 정보가 없습니다."),

    // 500
    UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "500", "파일 업로드에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}