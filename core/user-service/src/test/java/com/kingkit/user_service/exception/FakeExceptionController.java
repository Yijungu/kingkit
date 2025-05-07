package com.kingkit.user_service.exception;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class FakeExceptionController {

    @GetMapping("/duplicate")
    public void throwDuplicate() {
        throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
    }

    @GetMapping("/illegal")
    public void throwIllegal() {
        throw new IllegalArgumentException("유저가 없습니다.");
    }

    @PostMapping("/validate")
    public void validate(@RequestBody @jakarta.validation.Valid DummyDto dto) {
        // Validation 실패 시 MethodArgumentNotValidException 발생
    }

    public record DummyDto(
            @NotBlank(message = "이메일은 필수입니다.")
            String email
    ) {}
}
