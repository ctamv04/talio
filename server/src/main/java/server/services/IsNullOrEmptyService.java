package server.services;

import org.springframework.stereotype.Service;

@Service
public class IsNullOrEmptyService {
    public  boolean check(String s) {
        return s == null || s.isEmpty();
    }
}
