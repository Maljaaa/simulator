package sm.playground.simulator.kisapi.token;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sm.playground.simulator.kisapi.token.service.TokenService;

@RestController
@RequiredArgsConstructor
public class TokenTestController {

    private final TokenService tokenService;

    @GetMapping("/token/test")
    public ResponseEntity<String> testToken() {
        String token = tokenService.getValidAccessToken();
        return ResponseEntity.ok(token);
    }
}
