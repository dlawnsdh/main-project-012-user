package com.example.mainproject012.dto.responose;

import java.util.List;

public record MemberResponseWithInfo(
        MemberResponse memberResponse,
        Long followingCount,
        Long followerCount,
        List<SimplePostResponse> postResponse,
        List<PickerResponse> pickerResponse
) {
    /*public static MemberResponseWithInfo of(MemberResponse memberResponse, List<SimplePostResponse> postResponse, List<PickerResponse> pickerResponse) {
        return new MemberResponseWithInfo(memberResponse, postResponse, pickerResponse);
    }*/

    public static MemberResponseWithInfo of(MemberResponse memberResponse, Long followingCount, Long followerCount, List<SimplePostResponse> postResponse, List<PickerResponse> pickerResponse) {
        return new MemberResponseWithInfo(memberResponse, followingCount, followerCount, postResponse, pickerResponse);
    }

}
