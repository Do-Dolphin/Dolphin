package com.dolphin.demo.dto.request;

import com.dolphin.demo.dto.response.WorldCupResponseDto;
import lombok.Getter;

import java.util.List;

@Getter
public class WorldCupRequestDto {

    private List<WorldCupResponseDto> worldCupResponseDtoList;
}
