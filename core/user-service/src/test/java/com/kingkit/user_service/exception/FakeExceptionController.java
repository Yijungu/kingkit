package com.kingkit.user_service.exception;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Validated // ← ConstraintViolationException을 유발하려면 반드시 필요
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
    public void validate(@RequestBody @Valid DummyDto dto) {
        // MethodArgumentNotValidException 발생 지점
    }

    @GetMapping("/missing")
    public void missing(@RequestParam String email) {
        // MissingServletRequestParameterException 발생 (없으면 자동으로 Spring이 던짐)
    }

    @GetMapping("/constraint")
    public void constraint(@RequestParam @Min(value = 0, message = "나이는 0 이상이어야 합니다.") int age) {
        // ConstraintViolationException 발생 지점
    }

    @GetMapping("/global")
    public void unexpected() {
        throw new RuntimeException("알 수 없는 서버 에러");
    }

    public record DummyDto(
            @NotBlank(message = "이메일은 필수입니다.")
            String email
    ) {}
}
