package org.test.mindexpanseweb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.mindexpanseweb.dto.MathProblemDto;
import org.test.mindexpanseweb.service.ProblemGeneratorService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/problems")

@CrossOrigin(origins = "http://localhost:5173")
public class ProblemController {
    @Autowired
    private   ProblemGeneratorService problemGeneratorService;

    @GetMapping("/generate")
    public ResponseEntity<List<MathProblemDto>> generateProblems(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "false") boolean withRegrouping
    ) {
        return ResponseEntity.ok(
                problemGeneratorService.generateAdditionWorksheet(count, withRegrouping)
        );
    }

    @PostMapping("/check")
    public ResponseEntity<List<MathProblemDto>> checkAnswers(@RequestBody List<MathProblemDto> problems) {
        return ResponseEntity.ok(
                problems.stream()
                        .map(this::evaluateProblem)
                        .collect(Collectors.toList())
        );
    }

    private MathProblemDto evaluateProblem(MathProblemDto problem) {
        problem.setCorrect(
                problem.getUserAnswer() != null &&
                        problem.getUserAnswer() == problem.getCorrectAnswer()
        );
        return problem;
    }
}
