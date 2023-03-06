package server.configs;

import models.Board;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.repositories.BoardRepository;

import java.util.List;

@Configuration
public class BoardConfig {
    @Bean
    CommandLineRunner commandLineRunner(BoardRepository repository){
        return args -> {
            Board board1=new Board("Board1");
            Board board2=new Board("Board2");
            Board board3=new Board("Board3");
            repository.saveAll(List.of(board1,board2,board3));
        };
    }
}
