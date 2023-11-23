package com.cherish.backend.exception;

public class LeaveAccountStoreException extends RuntimeException {

    public LeaveAccountStoreException() {
        super("탈퇴한 계정이지만 아직 일주일이 되지 않았습니다. 기존의 아이디로 로그인 할지 혹은 새로운 아이디로 회원가입 할 지 선택한 후 다시 요청을 보내주세요.");
    }

}
