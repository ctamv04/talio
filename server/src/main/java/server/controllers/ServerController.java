package server.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;


@RestController
@RequestMapping("/api/server")
public class ServerController {
    private final String password;

    public ServerController() {
        password = generatePassword();
        System.out.println("Generated password is: " + password);
    }

    /**
     * Get password Method
     *
     * @return Generated password
     */
    @GetMapping("")
    public String getPassword() {
        return password;
    }

    /**
     * Generates password with length in range [8, 16].
     * Password contains lowercase and uppercase english letters and digits.
     *
     * @return password
     */
    private String generatePassword() {
        int characterNumber = 62;

        Random random = new Random();
        StringBuilder buffer = new StringBuilder();
        int length = random.nextInt(8) + 8;
        for (int i = 0; i < length; i++) {
            int code = random.nextInt(characterNumber);
            if (code < 10) {
                code += 48;
            } else {
                if (code < 36) {
                    code += 55;
                } else {
                    code += 61;
                }
            }

            buffer.append((char) code);
        }

        return buffer.toString();
    }
}
