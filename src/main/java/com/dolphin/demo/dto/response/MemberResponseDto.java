package com.dolphin.demo.dto.response;

import com.dolphin.demo.domain.MemberRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {
    private String nickname;
    private String username;
    private MemberRoleEnum role;
}
